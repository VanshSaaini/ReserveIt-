package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.response.ClinicAnalyticsResponse;
import com.Reserveit.v1.entity.Appointment;
import com.Reserveit.v1.entity.AppointmentStatus;
import com.Reserveit.v1.entity.Clinic;
import com.Reserveit.v1.entity.PaymentStatus;
import com.Reserveit.v1.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicAnalyticsService {

    private final AppointmentRepository appointmentRepository;
    private final ClinicManagementService clinicManagementService;

    @Transactional(readOnly = true)
    public ClinicAnalyticsResponse getDashboard(LocalDate selectedDate, YearMonth selectedMonth) {
        Clinic clinic = clinicManagementService.findMyClinicEntity();
        LocalDate reportDate = selectedDate == null ? LocalDate.now() : selectedDate;
        YearMonth reportMonth = selectedMonth == null ? YearMonth.from(reportDate) : selectedMonth;

        List<Appointment> all = appointmentRepository
                .findByClinic_IdOrderByAppointmentDateDescStartTimeDesc(clinic.getId());

        List<Appointment> dayRows = all.stream()
                .filter(a -> reportDate.equals(a.getAppointmentDate()))
                .toList();

        List<Appointment> monthRows = all.stream()
                .filter(a -> reportMonth.equals(YearMonth.from(a.getAppointmentDate())))
                .toList();

        return new ClinicAnalyticsResponse(
                reportDate, reportMonth,
                dayRows.size(), countStatus(dayRows, AppointmentStatus.COMPLETED),
                countStatus(dayRows, AppointmentStatus.CANCELLED),
                sum(dayRows), sumPaid(dayRows), sumPending(dayRows),
                monthRows.size(), countStatus(monthRows, AppointmentStatus.COMPLETED),
                countStatus(monthRows, AppointmentStatus.CANCELLED),
                sum(monthRows), sumPaid(monthRows), sumPending(monthRows),
                monthRows.stream().filter(a -> a.getPaymentStatus() == PaymentStatus.PAID).count(),
                monthRows.stream().filter(a -> (a.getPaymentStatus() == null || a.getPaymentStatus() == PaymentStatus.PENDING)
                        && a.getStatus() != AppointmentStatus.CANCELLED).count(),
                doctorPerformance(monthRows), servicePerformance(monthRows)
        );
    }

    private long countStatus(List<Appointment> rows, AppointmentStatus status) {
        return rows.stream().filter(a -> a.getStatus() == status).count();
    }

    private BigDecimal amount(Appointment a) {
        return a.getPrice() == null ? BigDecimal.ZERO : a.getPrice();
    }

    private BigDecimal sum(List<Appointment> rows) {
        return money(rows.stream().filter(a -> a.getStatus() != AppointmentStatus.CANCELLED).map(this::amount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private BigDecimal sumPaid(List<Appointment> rows) {
        return money(rows.stream().filter(a -> a.getStatus() != AppointmentStatus.CANCELLED && a.getPaymentStatus() == PaymentStatus.PAID).map(this::amount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private BigDecimal sumPending(List<Appointment> rows) {
        return money(rows.stream().filter(a -> a.getStatus() != AppointmentStatus.CANCELLED && (a.getPaymentStatus() == null || a.getPaymentStatus() == PaymentStatus.PENDING)).map(this::amount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private List<ClinicAnalyticsResponse.DoctorPerformance> doctorPerformance(List<Appointment> rows) {
        return rows.stream().filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .collect(Collectors.groupingBy(a -> a.getDoctor().getId(), LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(group -> {
                    Appointment first = group.get(0);
                    return new ClinicAnalyticsResponse.DoctorPerformance(
                            first.getDoctor().getId(), first.getDoctor().getUser().getFullName(), group.size(),
                            countStatus(group, AppointmentStatus.COMPLETED), sum(group), sumPaid(group), sumPending(group));
                }).sorted(Comparator.comparing(ClinicAnalyticsResponse.DoctorPerformance::bookedRevenue).reversed()).toList();
    }

    private List<ClinicAnalyticsResponse.ServicePerformance> servicePerformance(List<Appointment> rows) {
        return rows.stream().filter(a -> a.getStatus() != AppointmentStatus.CANCELLED && a.getService() != null)
                .collect(Collectors.groupingBy(a -> a.getService().getId(), LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(group -> {
                    Appointment first = group.get(0);
                    return new ClinicAnalyticsResponse.ServicePerformance(first.getService().getId(), first.getService().getName(), group.size(), sum(group), sumPaid(group));
                }).sorted(Comparator.comparing(ClinicAnalyticsResponse.ServicePerformance::bookedRevenue).reversed()).toList();
    }
}
