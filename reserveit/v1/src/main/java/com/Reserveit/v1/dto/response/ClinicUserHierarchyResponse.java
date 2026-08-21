package com.Reserveit.v1.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ClinicUserHierarchyResponse(
        Long clinicId,
        String clinicName,
        boolean clinicActive,
        String subscriptionPlan,
        String subscriptionStatus,
        long doctorCount,
        long patientCount,
        LocalDateTime registeredAt,
        List<DoctorNode> doctors
) {
    public record DoctorNode(
            Long doctorId,
            Long userId,
            String name,
            String specialization,
            String email,
            boolean active,
            long patientCount,
            List<PatientNode> patients
    ) {}

    public record PatientNode(
            Long patientId,
            Long userId,
            String name,
            String email,
            String mobile,
            boolean active,
            LocalDate dateOfBirth,
            LocalDateTime registeredAt
    ) {}
}
