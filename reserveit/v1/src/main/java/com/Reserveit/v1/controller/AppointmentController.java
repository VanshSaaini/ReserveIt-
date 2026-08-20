package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.request.AppointmentBookRequest;
import com.Reserveit.v1.dto.request.AppointmentStatusUpdateRequest;
import com.Reserveit.v1.dto.request.RescheduleRequest;
import com.Reserveit.v1.dto.response.AppointmentResponse;
import com.Reserveit.v1.service.AppointmentManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentManagementService appointmentManagementService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody AppointmentBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentManagementService.book(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public List<AppointmentResponse> myAppointments() {
        return appointmentManagementService.listMine();
    }

    @GetMapping("/doctor/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<AppointmentResponse> myDoctorSchedule() {
        return appointmentManagementService.listForMyDoctorSchedule();
    }

    @GetMapping("/clinic/me")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public List<AppointmentResponse> myClinicAppointments() {
        return appointmentManagementService.listForMyClinic();
    }

    @PostMapping("/{id}/reminder")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'DOCTOR', 'SUPER_ADMIN')")
    public ResponseEntity<Void> sendReminder(@PathVariable Long id) {
        appointmentManagementService.sendReminder(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'SUPER_ADMIN')")
    public AppointmentResponse updatePayment(@PathVariable Long id, @RequestParam boolean paid) {
        return appointmentManagementService.updatePayment(id, paid);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC_ADMIN', 'SUPER_ADMIN')")
    public AppointmentResponse updateStatus(@PathVariable Long id,
            @Valid @RequestBody AppointmentStatusUpdateRequest request) {
        return appointmentManagementService.updateStatus(id, request);
    }

    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentResponse reschedule(@PathVariable Long id, @Valid @RequestBody RescheduleRequest request) {
        return appointmentManagementService.reschedule(id, request);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC_ADMIN', 'SUPER_ADMIN')")
    public AppointmentResponse cancel(@PathVariable Long id) {
        return appointmentManagementService.cancel(id);
    }
}
