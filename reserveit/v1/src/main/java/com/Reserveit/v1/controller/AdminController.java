package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.request.ActiveStatusRequest;
import com.Reserveit.v1.dto.response.ClinicResponse;
import com.Reserveit.v1.dto.response.UserResponse;
import com.Reserveit.v1.service.AdminService;
import com.Reserveit.v1.service.ClinicManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Super-admin-only platform oversight: every clinic, every user account. */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ClinicManagementService clinicManagementService;

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return adminService.listUsers();
    }

    @PatchMapping("/users/{id}/active")
    public UserResponse setUserActive(@PathVariable Long id, @Valid @RequestBody ActiveStatusRequest request) {
        return adminService.setUserActive(id, request.active());
    }

    @GetMapping("/clinics")
    public List<ClinicResponse> listAllClinics() {
        return clinicManagementService.listAll();
    }

    @PatchMapping("/clinics/{id}/active")
    public ClinicResponse setClinicActive(@PathVariable Long id, @Valid @RequestBody ActiveStatusRequest request) {
        return clinicManagementService.setActive(id, request.active());
    }
}
