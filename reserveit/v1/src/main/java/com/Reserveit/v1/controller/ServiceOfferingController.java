package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.request.ServiceOfferingRequest;
import com.Reserveit.v1.dto.response.ServiceOfferingResponse;
import com.Reserveit.v1.service.ServiceOfferingManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Update/delete for a single service. Creation & listing-mine live under /api/clinics/me/services. */
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceOfferingController {

    private final ServiceOfferingManagementService serviceOfferingManagementService;

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public ServiceOfferingResponse update(@PathVariable Long id, @Valid @RequestBody ServiceOfferingRequest request) {
        return serviceOfferingManagementService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLINIC_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceOfferingManagementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
