package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.request.PatientUpdateRequest;
import com.Reserveit.v1.dto.response.PatientResponse;
import com.Reserveit.v1.service.PatientManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientManagementService patientManagementService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public PatientResponse myProfile() {
        return patientManagementService.getMyProfile();
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public PatientResponse updateMyProfile(@Valid @RequestBody PatientUpdateRequest request) {
        return patientManagementService.updateMyProfile(request);
    }
}
