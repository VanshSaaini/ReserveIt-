package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.response.SuperAdminAnalyticsResponse;
import com.Reserveit.v1.entity.Appointment;
import com.Reserveit.v1.entity.AppointmentStatus;
import com.Reserveit.v1.entity.Clinic;
import com.Reserveit.v1.entity.PaymentStatus;
import com.Reserveit.v1.entity.SubscriptionPayment;
import com.Reserveit.v1.repository.AppointmentRepository;
import com.Reserveit.v1.repository.ClinicRepository;
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

    private final AppointmentRepository appointmentRepository;
    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final SubscriptionPaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public SuperAdminAnalyticsResponse dashboard(
            LocalDate selectedDate,
            YearMonth selectedMonth) {

        LocalDate date = selectedDate != null
                ? selectedDate
                : LocalDate.now();

        YearMonth month = selectedMonth != null
                ? selectedMonth
                : YearMonth.from(date);

        /*
         * ---------------------------------------------------------
         * APPOINTMENTS
         * ---------------------------------------------------------
         */

        List<Appointment> allAppointments = appointmentRepository.findAll();

        // Selected day's appointments
        List<Appointment> dayAppointments = allAppointments.stream()
                .filter(a -> a.getAppointmentDate() != null
                        && date.equals(a.getAppointmentDate()))
                .toList();

        // Selected month's appointments
        List<Appointment> monthAppointments = allAppointments.stream()
                .filter(a -> a.getAppointmentDate() != null
                        && month.equals(
                                YearMonth.from(
                                        a.getAppointmentDate())))
                .toList();

        /*
         * ---------------------------------------------------------
         * MANUAL CLINIC SUBSCRIPTION PAYMENTS
         * ---------------------------------------------------------
         */

        LocalDate billingMonth = month.atDay(1);

        List<SubscriptionPayment> subscriptionPayments = paymentRepository
                .findByBillingMonthOrderByClinic_NameAsc(
                        billingMonth);

        BigDecimal expectedSubscriptionRevenue = money(
                subscriptionPayments.stream()
                        .map(SubscriptionPayment::getAmount)
                        .filter(amount -> amount != null)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add));

        BigDecimal collectedSubscriptionRevenue = money(
                subscriptionPayments.stream()
                        .filter(SubscriptionPayment::isPaid)
                        .map(SubscriptionPayment::getAmount)
                        .filter(amount -> amount != null)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add));

        BigDecimal pendingSubscriptionRevenue = money(
                expectedSubscriptionRevenue
                        .subtract(
                                collectedSubscriptionRevenue));

        /*
         * ---------------------------------------------------------
         * CLINIC-WISE BUSINESS PERFORMANCE
         * ---------------------------------------------------------
         */

        List<SuperAdminAnalyticsResponse.ClinicBusiness> clinicBusiness = monthAppointments.stream()

                // Do not include cancelled appointments
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)

                // Group appointments by clinic
                .filter(a -> a.getClinic() != null)
                .collect(
                        Collectors.groupingBy(
                                a -> a.getClinic().getId(),
                                LinkedHashMap::new,
                                Collectors.toList()))

                .values()
                .stream()

                .map(rows -> {

                    Clinic clinic = rows.get(0).getClinic();

                    return new SuperAdminAnalyticsResponse.ClinicBusiness(
                            clinic.getId(),
                            clinic.getName(),
                            rows.size(),
                            sum(rows),
                            sumPaid(rows),
                            sumPending(rows));
                })

                // Highest revenue clinic first
                .sorted(
                        Comparator.comparing(
                                SuperAdminAnalyticsResponse.ClinicBusiness::bookedRevenue).reversed())

                .toList();

        /*
         * ---------------------------------------------------------
         * RETURN DASHBOARD RESPONSE
         * ---------------------------------------------------------
         */

        return new SuperAdminAnalyticsResponse(

                // Selected date
                date,

                // Selected month
                month,

                // -------------------------------------------------
                // CLINICS
                // -------------------------------------------------

                clinicRepository.count(),

                clinicRepository.findByActiveTrue().size(),

                // -------------------------------------------------
                // USERS
                // -------------------------------------------------

                userRepository.count(),

                userRepository.countByActiveTrue(),

                // -------------------------------------------------
                // TODAY / SELECTED DAY
                // -------------------------------------------------

                dayAppointments.size(),

                sum(dayAppointments),

                sumPaid(dayAppointments),

                sumPending(dayAppointments),

                // -------------------------------------------------
                // MONTHLY PERFORMANCE
                // -------------------------------------------------

                monthAppointments.size(),

                countStatus(
                        monthAppointments,
                        AppointmentStatus.COMPLETED),

                countStatus(
                        monthAppointments,
                        AppointmentStatus.CANCELLED),

                sum(monthAppointments),

                sumPaid(monthAppointments),

                sumPending(monthAppointments),

                // -------------------------------------------------
                // CLINIC SUBSCRIPTION COLLECTION
                // -------------------------------------------------

                expectedSubscriptionRevenue,

                collectedSubscriptionRevenue,

                pendingSubscriptionRevenue,

                subscriptionPayments.stream()
                        .filter(SubscriptionPayment::isPaid)
                        .count(),

                subscriptionPayments.stream()
                        .filter(payment -> !payment.isPaid())
                        .count(),

                // -------------------------------------------------
                // CLINIC-WISE PERFORMANCE
                // -------------------------------------------------

                clinicBusiness);
    }

    /*
     * -------------------------------------------------------------
     * COUNT APPOINTMENT STATUS
     * -------------------------------------------------------------
     */

    private long countStatus(
            List<Appointment> appointments,
            AppointmentStatus status) {

        return appointments.stream()
                .filter(a -> a.getStatus() == status)
                .count();
    }

    /*
     * -------------------------------------------------------------
     * GET APPOINTMENT PRICE
     * -------------------------------------------------------------
     */

    private BigDecimal amount(Appointment appointment) {

        if (appointment == null) {
            return BigDecimal.ZERO;
        }

        if (appointment.getPrice() == null) {
            return BigDecimal.ZERO;
        }

        return appointment.getPrice();
    }

    /*
     * -------------------------------------------------------------
     * TOTAL BOOKED REVENUE
     * -------------------------------------------------------------
     */

    private BigDecimal sum(List<Appointment> appointments) {

        return money(
                appointments.stream()
                        .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                        .map(this::amount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add));
    }

    /*
     * -------------------------------------------------------------
     * TOTAL PAID REVENUE
     * -------------------------------------------------------------
     */

    private BigDecimal sumPaid(
            List<Appointment> appointments) {

        return money(
                appointments.stream()
                        .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                        .filter(a -> a.getPaymentStatus() == PaymentStatus.PAID)
                        .map(this::amount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add));
    }

    /*
     * -------------------------------------------------------------
     * TOTAL PENDING REVENUE
     * -------------------------------------------------------------
     */

    private BigDecimal sumPending(
            List<Appointment> appointments) {

        return money(
                appointments.stream()
                        .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                        .filter(a -> a.getPaymentStatus() == null
                                || a.getPaymentStatus() == PaymentStatus.PENDING)
                        .map(this::amount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add));
    }

    /*
     * -------------------------------------------------------------
     * MONEY ROUNDING
     * -------------------------------------------------------------
     */

    private BigDecimal money(BigDecimal value) {

        if (value == null) {
            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP);
        }

        return value.setScale(
                2,
                RoundingMode.HALF_UP);
    }
}