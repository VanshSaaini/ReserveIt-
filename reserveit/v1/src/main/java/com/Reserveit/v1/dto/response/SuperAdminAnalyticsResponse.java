package com.Reserveit.v1.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public record SuperAdminAnalyticsResponse(
        LocalDate reportDate,
        YearMonth reportMonth,
        long totalClinics,
        long activeClinics,
        long totalUsers,
        long activeUsers,
        long dayAppointments,
        BigDecimal dayBookedRevenue,
        BigDecimal dayCollectedRevenue,
        BigDecimal dayPendingRevenue,
        long monthAppointments,
        long monthCompleted,
        long monthCancelled,
        BigDecimal monthBookedRevenue,
        BigDecimal monthCollectedRevenue,
        BigDecimal monthPendingRevenue,
        BigDecimal subscriptionExpected,
        BigDecimal subscriptionCollected,
        BigDecimal subscriptionPending,
        long subscriptionPaidCount,
        long subscriptionPendingCount,
        List<ClinicBusiness> clinics
) {
    public record ClinicBusiness(
            Long clinicId,
            String clinicName,
            long appointments,
            BigDecimal bookedRevenue,
            BigDecimal collectedRevenue,
            BigDecimal pendingRevenue
    ) {}
}
