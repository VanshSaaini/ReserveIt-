package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.request.AvailabilityRequest;
import com.Reserveit.v1.dto.request.DoctorCreateRequest;
import com.Reserveit.v1.dto.request.DoctorUpdateRequest;
import com.Reserveit.v1.dto.response.AvailabilityResponse;
import com.Reserveit.v1.dto.response.DoctorResponse;
import com.Reserveit.v1.dto.response.SlotResponse;
import com.Reserveit.v1.service.AvailabilityManagementService;
import com.Reserveit.v1.service.DoctorManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorManagementService doctorManagementService;
    private final AvailabilityManagementService availabilityManagementService;

    @GetMapping("/{id}")
    public DoctorResponse getDoctor(@PathVariable Long id) {
        return doctorManagementService.getById(id);
    }

    @GetMapping("/{id}/availability/slots")
    public List<SlotResponse> getSlots(@PathVariable Long id,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return availabilityManagementService.getAvailableSlots(id, date);
    }

    @PostMapping
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorManagementService.createDoctor(request));
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('CLINIC_ADMIN', 'SUPER_ADMIN')")
    public DoctorResponse setActive(@PathVariable Long id,
                                     @Valid @RequestBody com.Reserveit.v1.dto.request.ActiveStatusRequest request) {
        return doctorManagementService.setActive(id, request.active());
    }

    // ---- Doctor self-service ----

    @GetMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public DoctorResponse getMyProfile() {
        return doctorManagementService.getMyProfile();
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public DoctorResponse updateMyProfile(@Valid @RequestBody DoctorUpdateRequest request) {
        return doctorManagementService.updateMyProfile(request);
    }

    @GetMapping("/me/availability")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<AvailabilityResponse> getMyAvailability() {
        return availabilityManagementService.listMine();
    }

    @PostMapping("/me/availability")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AvailabilityResponse> addMyAvailability(@Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(availabilityManagementService.create(request));
    }

    @DeleteMapping("/me/availability/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteMyAvailability(@PathVariable Long id) {
        availabilityManagementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
