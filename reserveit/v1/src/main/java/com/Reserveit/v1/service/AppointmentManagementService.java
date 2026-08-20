package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.request.AppointmentBookRequest;
import com.Reserveit.v1.dto.request.AppointmentStatusUpdateRequest;
import com.Reserveit.v1.dto.request.RescheduleRequest;
import com.Reserveit.v1.dto.response.AppointmentResponse;
import com.Reserveit.v1.entity.*;
import com.Reserveit.v1.exception.BadRequestException;
import com.Reserveit.v1.exception.ForbiddenActionException;
import com.Reserveit.v1.exception.ResourceNotFoundException;
import com.Reserveit.v1.repository.*;
import com.Reserveit.v1.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentManagementService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final DoctorManagementService doctorManagementService;
    private final ClinicManagementService clinicManagementService;
    private final Mapper mapper;
    private final EmailService emailService;

    @Transactional
    public AppointmentResponse book(AppointmentBookRequest req) {
        Long userId = SecurityUtils.currentUserId();
        Patient patient = patientRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ForbiddenActionException("No patient profile is linked to this account."));

        Doctor doctor = doctorRepository.findById(req.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found."));

        if (!doctor.getUser().isActive()) {
            throw new BadRequestException("This doctor is not currently accepting bookings.");
        }

        ServiceOffering service = null;
        int durationMinutes = doctor.getDefaultSlotMinutes();
        if (req.serviceId() != null) {
            service = serviceOfferingRepository.findById(req.serviceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found."));
            if (!service.getClinic().getId().equals(doctor.getClinic().getId())) {
                throw new BadRequestException("That service is not offered at this doctor's clinic.");
            }
            if (service.getDurationMinutes() != null) {
                durationMinutes = service.getDurationMinutes();
            }
        }

        LocalTime startTime = req.startTime();
        LocalTime endTime = startTime.plusMinutes(durationMinutes);

        boolean conflict = appointmentRepository
                .findByDoctor_IdAndAppointmentDateAndStatusNot(doctor.getId(), req.appointmentDate(), AppointmentStatus.CANCELLED)
                .stream()
                .anyMatch(a -> startTime.isBefore(a.getEndTime()) && a.getStartTime().isBefore(endTime));

        if (conflict) {
            throw new BadRequestException("That slot is no longer available. Please choose another.");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .clinic(doctor.getClinic())
                .service(service)
                .appointmentDate(req.appointmentDate())
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.CONFIRMED)
                .notes(req.notes())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        emailService.sendAppointmentConfirmation(savedAppointment);
        return mapper.toAppointmentResponse(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listMine() {
        Long userId = SecurityUtils.currentUserId();
        Patient patient = patientRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ForbiddenActionException("No patient profile is linked to this account."));
        return appointmentRepository.findByPatient_IdOrderByAppointmentDateDescStartTimeDesc(patient.getId())
                .stream().map(mapper::toAppointmentResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listForMyDoctorSchedule() {
        Doctor doctor = doctorManagementService.findMyDoctorEntity();
        return appointmentRepository.findByDoctor_IdOrderByAppointmentDateDescStartTimeDesc(doctor.getId())
                .stream().map(mapper::toAppointmentResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listForMyClinic() {
        Clinic clinic = clinicManagementService.findMyClinicEntity();
        return appointmentRepository.findByClinic_IdOrderByAppointmentDateDescStartTimeDesc(clinic.getId())
                .stream().map(mapper::toAppointmentResponse).toList();
    }

    @Transactional
    public void sendReminder(Long appointmentId) {
        Appointment appointment = findOrThrow(appointmentId);
        assertCanManage(appointment);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("A reminder cannot be sent for a "
                    + appointment.getStatus().name().toLowerCase() + " appointment.");
        }

        boolean sent = emailService.sendAppointmentReminder(appointment);
        if (!sent) {
            throw new BadRequestException("The reminder email could not be sent. Check the Gmail SMTP configuration.");
        }
    }

    @Transactional
    public AppointmentResponse updateStatus(Long appointmentId, AppointmentStatusUpdateRequest req) {
        Appointment appointment = findOrThrow(appointmentId);
        assertCanManage(appointment);
        appointment.setStatus(req.status());
        if (req.notes() != null) {
            appointment.setNotes(req.notes());
        }
        return mapper.toAppointmentResponse(appointment);
    }

    @Transactional
    public AppointmentResponse reschedule(Long appointmentId, RescheduleRequest req) {
        Appointment appointment = findOrThrow(appointmentId);
        assertOwnsAsPatient(appointment);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("A " + appointment.getStatus().name().toLowerCase()
                    + " appointment can't be rescheduled.");
        }

        long durationMinutes = java.time.Duration.between(appointment.getStartTime(), appointment.getEndTime()).toMinutes();
        LocalTime newStart = req.startTime();
        LocalTime newEnd = newStart.plusMinutes(durationMinutes);

        boolean conflict = appointmentRepository
                .findByDoctor_IdAndAppointmentDateAndStatusNot(
                        appointment.getDoctor().getId(), req.appointmentDate(), AppointmentStatus.CANCELLED)
                .stream()
                .filter(a -> !a.getId().equals(appointment.getId()))
                .anyMatch(a -> newStart.isBefore(a.getEndTime()) && a.getStartTime().isBefore(newEnd));

        if (conflict) {
            throw new BadRequestException("That slot is no longer available. Please choose another.");
        }

        appointment.setAppointmentDate(req.appointmentDate());
        appointment.setStartTime(newStart);
        appointment.setEndTime(newEnd);
        appointment.setStatus(AppointmentStatus.RESCHEDULED);

        return mapper.toAppointmentResponse(appointment);
    }

    @Transactional
    public AppointmentResponse cancel(Long appointmentId) {
        Appointment appointment = findOrThrow(appointmentId);

        var principal = SecurityUtils.currentPrincipal();
        if ("PATIENT".equals(principal.getRole())) {
            assertOwnsAsPatient(appointment);
        } else {
            assertCanManage(appointment);
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return mapper.toAppointmentResponse(appointment);
    }

    private Appointment findOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found."));
    }

    private void assertOwnsAsPatient(Appointment appointment) {
        Long userId = SecurityUtils.currentUserId();
        if (!appointment.getPatient().getUser().getId().equals(userId)) {
            throw new ForbiddenActionException("This appointment does not belong to you.");
        }
    }

    /** Doctor owning the appointment, or the clinic admin whose clinic it belongs to, or super admin. */
    private void assertCanManage(Appointment appointment) {
        var principal = SecurityUtils.currentPrincipal();
        switch (principal.getRole()) {
            case "SUPER_ADMIN" -> { }
            case "DOCTOR" -> {
                if (!appointment.getDoctor().getUser().getId().equals(principal.getId())) {
                    throw new ForbiddenActionException("This appointment does not belong to you.");
                }
            }
            case "CLINIC_ADMIN" -> {
                Clinic clinic = clinicManagementService.findMyClinicEntity();
                if (!appointment.getClinic().getId().equals(clinic.getId())) {
                    throw new ForbiddenActionException("This appointment is not part of your clinic.");
                }
            }
            default -> throw new ForbiddenActionException("You do not have permission to manage this appointment.");
        }
    }
}
