package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.request.ClinicSubscriptionSelectRequest;
import com.Reserveit.v1.dto.response.ClinicSubscriptionResponse;
import com.Reserveit.v1.dto.response.SubscriptionHistoryResponse;
import com.Reserveit.v1.service.ClinicSubscriptionService;
import com.Reserveit.v1.service.SubscriptionPlanService;
import com.Reserveit.v1.dto.response.SubscriptionPlanResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinic/subscription")
@PreAuthorize("hasRole('CLINIC_ADMIN')")
@RequiredArgsConstructor
public class SubscriptionController {

    private final ClinicSubscriptionService subscriptionService;
    private final SubscriptionPlanService planService;

    @GetMapping("/plans")
    public List<SubscriptionPlanResponse> activePlans() {
        return planService.listActive();
    }

    @GetMapping
    public ClinicSubscriptionResponse getMine() {
        return subscriptionService.getMine();
    }

    @GetMapping("/history")
    public List<SubscriptionHistoryResponse> history() {
        return subscriptionService.getMyHistory();
    }

    @PostMapping
    public ClinicSubscriptionResponse selectPlan(
            @Valid @RequestBody ClinicSubscriptionSelectRequest request) {
        return subscriptionService.selectPlan(request);
    }

    @PostMapping("/renew")
    public ClinicSubscriptionResponse renew() {
        return subscriptionService.renewMine();
    }

    @PostMapping("/cancel")
    public ClinicSubscriptionResponse cancel() {
        return subscriptionService.cancelMine();
    }
}
