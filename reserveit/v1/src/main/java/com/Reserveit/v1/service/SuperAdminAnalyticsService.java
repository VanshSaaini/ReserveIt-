package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.response.SuperAdminAnalyticsResponse;
import com.Reserveit.v1.entity.Clinic;
import com.Reserveit.v1.entity.ClinicSubscription;
import com.Reserveit.v1.entity.SubscriptionPayment;
import com.Reserveit.v1.entity.SubscriptionStatus;
import com.Reserveit.v1.repository.ClinicRepository;
import com.Reserveit.v1.repository.ClinicSubscriptionRepository;
import com.Reserveit.v1.repository.DoctorRepository;
import com.Reserveit.v1.repository.PatientRepository;
import com.Reserveit.v1.repository.SubscriptionPaymentRepository;
import com.Reserveit.v1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminAnalyticsService {
    private final ClinicRepository clinicRepository;
    private final ClinicSubscriptionRepository subscriptionRepository;
    private final SubscriptionPaymentRepository paymentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public SuperAdminAnalyticsResponse dashboard(LocalDate selectedDate, YearMonth selectedMonth) {
        LocalDate date = selectedDate == null ? LocalDate.now() : selectedDate;
        YearMonth month = selectedMonth == null ? YearMonth.from(date) : selectedMonth;

        List<Clinic> clinics = clinicRepository.findAll();
        List<ClinicSubscription> subscriptions = subscriptionRepository.findAll();
        List<SubscriptionPayment> payments = paymentRepository.findByBillingMonthOrderByClinic_NameAsc(month.atDay(1));

        BigDecimal expected = money(payments.stream().map(SubscriptionPayment::getAmount)
                .filter(x -> x != null).reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal collected = money(payments.stream().filter(SubscriptionPayment::isPaid)
                .map(SubscriptionPayment::getAmount).filter(x -> x != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        Map<String, List<ClinicSubscription>> byPlan = subscriptions.stream()
                .filter(s -> s.getSubscriptionPlan() != null)
                .collect(Collectors.groupingBy(s -> s.getSubscriptionPlan().getName(), LinkedHashMap::new, Collectors.toList()));

        List<SuperAdminAnalyticsResponse.PlanDistribution> planDistribution = byPlan.values().stream()
                .map(rows -> {
                    var plan = rows.get(0).getSubscriptionPlan();
                    return new SuperAdminAnalyticsResponse.PlanDistribution(
                            plan.getName(), rows.size(), plan.getPriceMonthly(), plan.getMaxDoctors());
                }).toList();

        LocalDate firstOfMonth = month.atDay(1);
        long newClinics = clinics.stream().filter(c -> c.getCreatedAt() != null
                && !c.getCreatedAt().toLocalDate().isBefore(firstOfMonth)
                && c.getCreatedAt().toLocalDate().isBefore(month.plusMonths(1).atDay(1))).count();

        List<SuperAdminAnalyticsResponse.RecentClinic> recentClinics = clinics.stream()
                .sorted(Comparator.comparing(Clinic::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(8)
                .map(c -> new SuperAdminAnalyticsResponse.RecentClinic(c.getId(), c.getName(), c.isActive(), c.getCreatedAt()))
                .toList();

        return new SuperAdminAnalyticsResponse(
                date, month,
                clinics.size(), clinics.stream().filter(Clinic::isActive).count(), clinics.stream().filter(c -> !c.isActive()).count(),
                doctorRepository.count(), patientRepository.count(), userRepository.count(), userRepository.countByActiveTrue(),
                subscriptions.stream().filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE).count(),
                subscriptions.stream().filter(s -> s.getStatus() == SubscriptionStatus.EXPIRING).count(),
                subscriptions.stream().filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED).count(),
                newClinics, expected, collected, money(expected.subtract(collected)),
                payments.stream().filter(SubscriptionPayment::isPaid).count(),
                payments.stream().filter(p -> !p.isPaid()).count(),
                planDistribution, recentClinics);
    }

    private BigDecimal money(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }
}
