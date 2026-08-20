package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.response.*;
import com.Reserveit.v1.entity.*;
import org.springframework.stereotype.Component;

/** Central place for turning entities into their outward-facing response DTOs. */
@Component
public class Mapper {

    public UserResponse toUserResponse(User u) {
        return new UserResponse(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(),
                u.getMobile(), u.getRole().name(), u.isActive());
    }

    public ClinicResponse toClinicResponse(Clinic c) {
        return new ClinicResponse(c.getId(), c.getName(), c.getAddress(), c.getPhone(), c.getEmail(),
                c.isActive(), c.getAdmin().getId(), c.getAdmin().getFullName());
    }

    public PatientResponse toPatientResponse(Patient p) {
        User u = p.getUser();
        return new PatientResponse(p.getId(), u.getId(), u.getFirstName(), u.getLastName(),
                u.getEmail(), u.getMobile(), p.getDateOfBirth());
    }

    public DoctorResponse toDoctorResponse(Doctor d) {
        User u = d.getUser();
        return new DoctorResponse(d.getId(), u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(),
                u.getMobile(), u.isActive(), d.getClinic().getId(), d.getClinic().getName(),
                d.getSpecialization(), d.getQualifications(), d.getExperienceYears(), d.getDefaultSlotMinutes());
    }

    public ServiceOfferingResponse toServiceResponse(ServiceOffering s) {
        return new ServiceOfferingResponse(s.getId(), s.getClinic().getId(), s.getName(),
                s.getDescription(), s.getDurationMinutes(), s.getPrice(), s.isActive());
    }

    public AvailabilityResponse toAvailabilityResponse(DoctorAvailability a) {
        return new AvailabilityResponse(a.getId(), a.getDoctor().getId(), a.getDayOfWeek(),
                a.getStartTime(), a.getEndTime(), a.getSlotDurationMinutes(), a.isActive());
    }

    public AppointmentResponse toAppointmentResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPatient().getId(),
                a.getPatient().getUser().getFullName(),
                a.getDoctor().getId(),
                a.getDoctor().getUser().getFullName(),
                a.getClinic().getId(),
                a.getClinic().getName(),
                a.getService() != null ? a.getService().getId() : null,
                a.getService() != null ? a.getService().getName() : null,
                a.getAppointmentDate(),
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus().name(),
                a.getNotes(),
                a.getCreatedAt());
    }
}
