package com.Reserveit.v1.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        Long clinicId,
        String clinicName,
        Long serviceId,
        String serviceName,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        BigDecimal price,
        String paymentStatus,
        LocalDateTime paidAt,
        String notes,
        LocalDateTime createdAt
) {
}
