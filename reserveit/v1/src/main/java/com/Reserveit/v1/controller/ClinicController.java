package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.request.ClinicUpdateRequest;
import com.Reserveit.v1.dto.response.ClinicResponse;
import com.Reserveit.v1.dto.response.DoctorResponse;
import com.Reserveit.v1.dto.response.ServiceOfferingResponse;
import com.Reserveit.v1.service.ClinicManagementService;
import com.Reserveit.v1.service.DoctorManagementService;
import com.Reserveit.v1.service.ServiceOfferingManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicManagementService clinicManagementService;
    private final DoctorManagementService doctorManagementService;
    private final ServiceOfferingManagementService serviceOfferingManagementService;

    @GetMapping
    public List<ClinicResponse> listClinics(@RequestParam(required = false) String search) {
        return clinicManagementService.listActive(search);
    }

    @GetMapping("/{id}")
    public ClinicResponse getClinic(@PathVariable Long id) {
        return clinicManagementService.getById(id);
    }

    @GetMapping("/{id}/doctors")
    public List<DoctorResponse> getClinicDoctors(@PathVariable Long id) {
        return doctorManagementService.listByClinic(id);
    }

    @GetMapping("/{id}/services")
    public List<ServiceOfferingResponse> getClinicServices(@PathVariable Long id) {
        return serviceOfferingManagementService.listByClinic(id);
    }

    // ---- Clinic-admin self-service (the logged-in clinic admin's own clinic) ----

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public ClinicResponse getMyClinic() {
        return clinicManagementService.getMyClinic();
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public ClinicResponse updateMyClinic(@Valid @RequestBody ClinicUpdateRequest request) {
        return clinicManagementService.updateMyClinic(request);
    }

    @GetMapping("/me/doctors")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public List<DoctorResponse> getMyClinicDoctors() {
        return doctorManagementService.listMyClinicDoctors();
    }

    @GetMapping("/me/services")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public List<ServiceOfferingResponse> getMyClinicServices() {
        return serviceOfferingManagementService.listMine();
    }

    @PostMapping("/me/services")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public ServiceOfferingResponse createMyClinicService(
            @Valid @RequestBody com.Reserveit.v1.dto.request.ServiceOfferingRequest request) {
        return serviceOfferingManagementService.create(request);
    }
}
