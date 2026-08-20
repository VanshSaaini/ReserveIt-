package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.request.ClinicSubscriptionSelectRequest;
import com.Reserveit.v1.dto.response.ClinicSubscriptionResponse;
import com.Reserveit.v1.dto.response.SubscriptionHistoryResponse;
import com.Reserveit.v1.entity.*;
import com.Reserveit.v1.exception.BadRequestException;
import com.Reserveit.v1.exception.ForbiddenActionException;
import com.Reserveit.v1.exception.ResourceNotFoundException;
import com.Reserveit.v1.repository.ClinicSubscriptionRepository;
import com.Reserveit.v1.repository.DoctorRepository;
import com.Reserveit.v1.repository.SubscriptionHistoryRepository;
import com.Reserveit.v1.repository.SubscriptionPlanRepository;
import com.Reserveit.v1.repository.SubscriptionPaymentRepository;


import com.Reserveit.v1.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicSubscriptionService {

    private static final int EXPIRING_THRESHOLD_DAYS = 7;

    private final ClinicSubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionHistoryRepository historyRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicManagementService clinicManagementService;
    private final SubscriptionPaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public ClinicSubscriptionResponse getMine() {
        Clinic clinic = clinicManagementService.findMyClinicEntity();
        ClinicSubscription subscription = subscriptionRepository.findByClinic_Id(clinic.getId())
                .orElse(null);
        return subscription == null ? null : toResponse(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionHistoryResponse> getMyHistory() {
        Clinic clinic = clinicManagementService.findMyClinicEntity();
        return historyRepository.findByClinic_IdOrderByCreatedAtDesc(clinic.getId())
                .stream().map(this::toHistoryResponse).toList();
    }

    @Transactional(readOnly = true)
    public void assertCanAddDoctor(Long clinicId) {
        ClinicSubscription subscription = subscriptionRepository.findByClinic_Id(clinicId)
                .orElseThrow(() -> new BadRequestException(
                        "Your clinic does not have an active subscription. Select a subscription plan first."));

        LocalDate today = LocalDate.now();
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE
                && subscription.getStatus() != SubscriptionStatus.EXPIRING) {
            throw new BadRequestException(
                    "Your subscription is " + subscription.getStatus().name().toLowerCase()
                            + ". Renew or select a plan before adding doctors.");
        }
        if (subscription.getEndDate().isBefore(today)) {
            throw new BadRequestException("Your subscription has expired. Renew it before adding doctors.");
        }

        int currentDoctors = doctorRepository.findByClinic_Id(clinicId).size();
        if (currentDoctors >= subscription.getSubscriptionPlan().getMaxDoctors()) {
            throw new BadRequestException(
                    "Your " + subscription.getSubscriptionPlan().getName() + " plan allows a maximum of "
                            + subscription.getSubscriptionPlan().getMaxDoctors()
                            + " doctors. Upgrade your plan to add another doctor.");
        }
    }

    @Transactional
    public ClinicSubscriptionResponse selectPlan(ClinicSubscriptionSelectRequest request) {
        Clinic clinic = clinicManagementService.findMyClinicEntity();
        SubscriptionPlan newPlan = activePlan(request.planId());

        ClinicSubscription current = subscriptionRepository.findWithLockByClinic_Id(clinic.getId()).orElse(null);

        if (current == null) {
            LocalDate start = LocalDate.now();
            LocalDate end = start.plusMonths(1).minusDays(1);
            ClinicSubscription subscription = ClinicSubscription.builder()
                    .clinic(clinic)
                    .subscriptionPlan(newPlan)
                    .status(SubscriptionStatus.ACTIVE)
                    .startDate(start)
                    .endDate(end)
                    .build();
            subscription = subscriptionRepository.save(subscription);
            recordHistory(subscription, "SUBSCRIPTION_STARTED", "Initial subscription selected.");
            ensureMonthlyPayment(subscription, start);
            return toResponse(subscription);
        }

        return changePlanInternal(current, newPlan);
    }

    @Transactional
    public ClinicSubscriptionResponse renewMine() {
        Clinic clinic = clinicManagementService.findMyClinicEntity();
        ClinicSubscription subscription = subscriptionRepository.findWithLockByClinic_Id(clinic.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Your clinic does not have a subscription."));

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new BadRequestException("A cancelled subscription cannot be renewed. Select a plan again.");
        }

        LocalDate today = LocalDate.now();
        LocalDate newStart = today.isAfter(subscription.getEndDate())
                ? today
                : subscription.getEndDate().plusDays(1);
        LocalDate newEnd = newStart.plusMonths(1).minusDays(1);

        subscription.setStartDate(newStart);
        subscription.setEndDate(newEnd);
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        recordHistory(subscription, "RENEWED", "Subscription renewed for another billing period.");
        ensureMonthlyPayment(subscription, newStart);
        return toResponse(subscription);
    }

    @Transactional
    public ClinicSubscriptionResponse cancelMine() {
        Clinic clinic = clinicManagementService.findMyClinicEntity();
        ClinicSubscription subscription = subscriptionRepository.findWithLockByClinic_Id(clinic.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Your clinic does not have a subscription."));

        if (subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            throw new BadRequestException("The subscription is already expired.");
        }
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            return toResponse(subscription);
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        recordHistory(subscription, "CANCELLED", "Subscription cancelled by clinic.");
        return toResponse(subscription);
    }

    /**
     * Upgrade/downgrade uses the same plan-selection endpoint.
     * Upgrade is immediate. Downgrade is immediate only if current usage fits
     * within the new plan's doctor limit; otherwise it is rejected so the
     * clinic cannot enter an invalid state.
     */
    @Transactional
    public ClinicSubscriptionResponse changePlan(Long clinicId, Long planId) {
        assertAdmin();
        ClinicSubscription subscription = subscriptionRepository.findWithLockByClinic_Id(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic subscription not found."));
        return changePlanInternal(subscription, activePlan(planId));
    }

    @Transactional
    public void expireSubscriptions() {
        LocalDate today = LocalDate.now();
        List<ClinicSubscription> due = subscriptionRepository
                .findByStatusInAndEndDateLessThanEqual(
                        List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.EXPIRING), today);

        for (ClinicSubscription subscription : due) {
            if (subscription.getEndDate().isBefore(today)) {
                subscription.setStatus(SubscriptionStatus.EXPIRED);
                recordHistory(subscription, "EXPIRED", "Subscription expired automatically.");
            } else if (subscription.getEndDate().equals(today)) {
                subscription.setStatus(SubscriptionStatus.EXPIRED);
                recordHistory(subscription, "EXPIRED", "Subscription expired at the end of its billing period.");
            }
        }
    }

    @Transactional
    public void markExpiringSubscriptions() {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(EXPIRING_THRESHOLD_DAYS);
        List<ClinicSubscription> due = subscriptionRepository
                .findByStatusInAndEndDateLessThanEqual(
                        List.of(SubscriptionStatus.ACTIVE), threshold);

        for (ClinicSubscription subscription : due) {
            if (!subscription.getEndDate().isBefore(today)
                    && !subscription.getEndDate().isAfter(threshold)) {
                subscription.setStatus(SubscriptionStatus.EXPIRING);
            }
        }
    }

    private ClinicSubscriptionResponse changePlanInternal(ClinicSubscription subscription, SubscriptionPlan newPlan) {
        if (subscription.getSubscriptionPlan().getId().equals(newPlan.getId())) {
            if (subscription.getStatus() == SubscriptionStatus.CANCELLED ||
                    subscription.getStatus() == SubscriptionStatus.EXPIRED) {
                LocalDate start = LocalDate.now();
                subscription.setStartDate(start);
                subscription.setEndDate(start.plusMonths(1).minusDays(1));
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                recordHistory(subscription, "REACTIVATED", "Subscription reactivated on the selected plan.");
                ensureMonthlyPayment(subscription, start);
            }
            return toResponse(subscription);
        }

        int currentDoctors = doctorRepository.findByClinic_Id(subscription.getClinic().getId()).size();
        boolean downgrade = newPlan.getMaxDoctors() < subscription.getSubscriptionPlan().getMaxDoctors();

        if (downgrade && currentDoctors > newPlan.getMaxDoctors()) {
            throw new BadRequestException(
                    "Downgrade is not allowed while your clinic has " + currentDoctors +
                            " doctors. Reduce the doctor count to " + newPlan.getMaxDoctors() +
                            " or fewer first.");
        }

        String event = downgrade ? "PLAN_DOWNGRADED" : "PLAN_UPGRADED";
        String note = downgrade
                ? "Plan downgraded immediately after usage validation."
                : "Plan upgraded immediately.";

        subscription.setSubscriptionPlan(newPlan);
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED ||
                subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            LocalDate start = LocalDate.now();
            subscription.setStartDate(start);
            subscription.setEndDate(start.plusMonths(1).minusDays(1));
        }
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        recordHistory(subscription, event, note);
        ensureMonthlyPayment(subscription, subscription.getStartDate());
        return toResponse(subscription);
    }

    private void ensureMonthlyPayment(ClinicSubscription subscription, LocalDate billingMonthDate) {
        LocalDate month = billingMonthDate.withDayOfMonth(1);
        SubscriptionPayment payment = paymentRepository
                .findByClinic_IdAndBillingMonth(subscription.getClinic().getId(), month)
                .orElseGet(() -> SubscriptionPayment.builder()
                        .clinic(subscription.getClinic())
                        .subscriptionPlan(subscription.getSubscriptionPlan())
                        .billingMonth(month)
                        .amount(subscription.getSubscriptionPlan().getPriceMonthly())
                        .paid(false)
                        .build());

        payment.setSubscriptionPlan(subscription.getSubscriptionPlan());
        payment.setAmount(subscription.getSubscriptionPlan().getPriceMonthly());
        paymentRepository.save(payment);
    }

    private SubscriptionPlan activePlan(Long id) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found."));
        if (!plan.isActive()) {
            throw new BadRequestException("This subscription plan is not active.");
        }
        return plan;
    }

    private void assertAdmin() {
        if (!"SUPER_ADMIN".equals(SecurityUtils.currentPrincipal().getRole())) {
            throw new ForbiddenActionException("Only a super administrator can manage another clinic's subscription.");
        }
    }

    private void recordHistory(ClinicSubscription s, String eventType, String notes) {
        SubscriptionPlan p = s.getSubscriptionPlan();
        historyRepository.save(SubscriptionHistory.builder()
                .clinic(s.getClinic())
                .subscriptionPlan(p)
                .status(s.getStatus())
                .eventType(eventType)
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .priceMonthly(p.getPriceMonthly())
                .maxDoctors(p.getMaxDoctors())
                .notes(notes)
                .build());
    }

    private ClinicSubscriptionResponse toResponse(ClinicSubscription s) {
        LocalDate today = LocalDate.now();
        long days = Math.max(0, ChronoUnit.DAYS.between(today, s.getEndDate()));
        int currentDoctors = doctorRepository.findByClinic_Id(s.getClinic().getId()).size();
        int remaining = Math.max(0, s.getSubscriptionPlan().getMaxDoctors() - currentDoctors);
        return new ClinicSubscriptionResponse(
                s.getId(),
                s.getClinic().getId(),
                s.getSubscriptionPlan().getId(),
                s.getSubscriptionPlan().getName(),
                s.getSubscriptionPlan().getPriceMonthly(),
                s.getSubscriptionPlan().getMaxDoctors(),
                currentDoctors,
                remaining,
                s.getStatus(),
                s.getStartDate(),
                s.getEndDate(),
                days,
                s.getUpdatedAt());
    }

    private SubscriptionHistoryResponse toHistoryResponse(SubscriptionHistory h) {
        return new SubscriptionHistoryResponse(
                h.getId(),
                h.getSubscriptionPlan().getId(),
                h.getSubscriptionPlan().getName(),
                h.getPriceMonthly(),
                h.getMaxDoctors(),
                h.getStatus(),
                h.getEventType(),
                h.getStartDate(),
                h.getEndDate(),
                h.getNotes(),
                h.getCreatedAt());
    }
}
