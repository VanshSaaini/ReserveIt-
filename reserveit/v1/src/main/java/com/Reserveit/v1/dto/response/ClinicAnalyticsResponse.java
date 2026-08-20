package com.Reserveit.v1.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public record ClinicAnalyticsResponse(
        LocalDate reportDate,
        YearMonth reportMonth,
        long todayAppointments,
        long todayCompleted,
        long todayCancelled,
        BigDecimal todayBookedRevenue,
        BigDecimal todayCollectedRevenue,
        BigDecimal todayPendingRevenue,
        long monthAppointments,
        long monthCompleted,
        long monthCancelled,
        BigDecimal monthBookedRevenue,
        BigDecimal monthCollectedRevenue,
        BigDecimal monthPendingRevenue,
        long monthPaidAppointments,
        long monthPendingPayments,
        List<DoctorPerformance> doctors,
        List<ServicePerformance> services
) {
    public record DoctorPerformance(
            Long doctorId,
            String doctorName,
            long appointments,
            long completed,
            BigDecimal bookedRevenue,
            BigDecimal collectedRevenue,
            BigDecimal pendingRevenue
    ) {}

    public record ServicePerformance(
            Long serviceId,
            String serviceName,
            long appointments,
            BigDecimal bookedRevenue,
            BigDecimal collectedRevenue
    ) {}
}
