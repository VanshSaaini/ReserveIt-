package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.request.SubscriptionPlanRequest;
import com.Reserveit.v1.dto.response.ClinicSubscriptionResponse;
import com.Reserveit.v1.dto.response.SubscriptionPaymentResponse;
import com.Reserveit.v1.dto.response.SubscriptionPlanResponse;
import com.Reserveit.v1.entity.SubscriptionPayment;
import com.Reserveit.v1.repository.SubscriptionPaymentRepository;
import com.Reserveit.v1.security.SecurityUtils;
import com.Reserveit.v1.service.ClinicSubscriptionService;
import com.Reserveit.v1.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/admin/subscription-plans")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminSubscriptionController {

    private final SubscriptionPlanService planService;
    private final ClinicSubscriptionService subscriptionService;
    private final SubscriptionPaymentRepository paymentRepository;

    // =========================================================
    // SUBSCRIPTION PLANS
    // =========================================================

    @GetMapping
    public List<SubscriptionPlanResponse> list() {
        return planService.listAll();
    }

    @PostMapping
    public SubscriptionPlanResponse create(
            @Valid @RequestBody SubscriptionPlanRequest request) {
        return planService.create(request);
    }

    @PutMapping("/{id}")
    public SubscriptionPlanResponse update(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionPlanRequest request) {
        return planService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public SubscriptionPlanResponse setActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return planService.setActive(id, active);
    }

    // =========================================================
    // CLINIC SUBSCRIPTION PAYMENTS
    // =========================================================

    @GetMapping("/payments")
    public List<SubscriptionPaymentResponse> payments(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

        YearMonth selectedMonth = month == null ? YearMonth.now() : month;

        LocalDate billingMonth = selectedMonth.atDay(1);

        return paymentRepository
                .findByBillingMonthOrderByClinic_NameAsc(
                        billingMonth)
                .stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    // =========================================================
    // MARK SUBSCRIPTION PAYMENT PAID / UNPAID
    // =========================================================

    @PatchMapping("/payments/{paymentId}/paid")
    public SubscriptionPaymentResponse markPaid(
            @PathVariable Long paymentId,
            @RequestParam boolean paid) {

        SubscriptionPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new com.Reserveit.v1.exception.ResourceNotFoundException(
                        "Subscription payment record not found."));

        payment.setPaid(paid);

        if (paid) {
            payment.setPaidAt(LocalDateTime.now());

            payment.setMarkedBy(
                    SecurityUtils
                            .currentPrincipal()
                            .getUser()
                            .getId());
        } else {
            payment.setPaidAt(null);
            payment.setMarkedBy(null);
        }

        SubscriptionPayment savedPayment = paymentRepository.save(payment);

        return toPaymentResponse(savedPayment);
    }

    // =========================================================
    // PAYMENT RESPONSE
    // =========================================================

    private SubscriptionPaymentResponse toPaymentResponse(
            SubscriptionPayment payment) {

        return new SubscriptionPaymentResponse(
                payment.getId(),

                payment.getClinic().getId(),
                payment.getClinic().getName(),

                payment.getSubscriptionPlan().getId(),
                payment.getSubscriptionPlan().getName(),

                payment.getBillingMonth(),

                payment.getAmount(),

                payment.isPaid(),

                payment.getPaidAt(),

               payment.getMarkedBy() == null
                       ? null
                       : String.valueOf(payment.getMarkedBy()));
    }

    // =========================================================
    // CHANGE CLINIC PLAN
    // =========================================================

    @PostMapping("/clinics/{clinicId}/change")
    public ClinicSubscriptionResponse changeClinicPlan(
            @PathVariable Long clinicId,
            @RequestParam Long planId) {

        return subscriptionService.changePlan(
                clinicId,
                planId);
    }
}