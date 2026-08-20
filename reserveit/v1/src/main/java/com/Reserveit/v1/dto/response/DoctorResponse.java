package com.Reserveit.v1.dto.response;

public record DoctorResponse(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        String email,
        String mobile,
        boolean active,
        Long clinicId,
        String clinicName,
        String specialization,
        String qualifications,
        Integer experienceYears,
        Integer defaultSlotMinutes
) {
}
