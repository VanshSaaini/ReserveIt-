package com.Reserveit.v1.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public record SuperAdminAnalyticsResponse(
        LocalDate reportDate,
        YearMonth reportMonth,
        long totalClinics,
        long activeClinics,
        long inactiveClinics,
        long totalDoctors,
        long totalPatients,
        long totalUsers,
        long activeUsers,
        long activeSubscriptions,
        long expiringSubscriptions,
        long expiredSubscriptions,
        long newClinicsThisMonth,
        BigDecimal subscriptionExpected,
        BigDecimal subscriptionCollected,
        BigDecimal subscriptionPending,
        long subscriptionPaidCount,
        long subscriptionPendingCount,
        List<PlanDistribution> planDistribution,
        List<RecentClinic> recentClinics
) {
    public record PlanDistribution(String planName, long clinics, BigDecimal monthlyPrice, Integer maxDoctors) {}
    public record RecentClinic(Long clinicId, String clinicName, boolean active, LocalDateTime registeredAt) {}
}
