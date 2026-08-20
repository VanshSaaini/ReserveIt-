package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.request.SubscriptionPlanRequest;
import com.Reserveit.v1.dto.response.ClinicSubscriptionResponse;
import com.Reserveit.v1.dto.response.SubscriptionPlanResponse;
import com.Reserveit.v1.service.ClinicSubscriptionService;
import com.Reserveit.v1.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/subscription-plans")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminSubscriptionController {

    private final SubscriptionPlanService planService;
    private final ClinicSubscriptionService subscriptionService;

    @GetMapping
    public List<SubscriptionPlanResponse> list() {
        return planService.listAll();
    }

    @PostMapping
    public SubscriptionPlanResponse create(@Valid @RequestBody SubscriptionPlanRequest request) {
        return planService.create(request);
    }

    @PutMapping("/{id}")
    public SubscriptionPlanResponse update(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionPlanRequest request) {
        return planService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public SubscriptionPlanResponse setActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return planService.setActive(id, active);
    }

    @PostMapping("/clinics/{clinicId}/change")
    public ClinicSubscriptionResponse changeClinicPlan(
            @PathVariable Long clinicId,
            @RequestParam Long planId) {
        return subscriptionService.changePlan(clinicId, planId);
    }
}
