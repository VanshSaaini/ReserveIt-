package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.request.SubscriptionPlanRequest;
import com.Reserveit.v1.dto.response.SubscriptionPlanResponse;
import com.Reserveit.v1.entity.SubscriptionPlan;
import com.Reserveit.v1.exception.DuplicateResourceException;
import com.Reserveit.v1.exception.ResourceNotFoundException;
import com.Reserveit.v1.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository repository;

    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> listAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> listActive() {
        return repository.findByActiveTrueOrderByPriceMonthlyAsc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public SubscriptionPlanResponse create(SubscriptionPlanRequest request) {
        if (repository.findByNameIgnoreCase(request.name().trim()).isPresent()) {
            throw new DuplicateResourceException("A subscription plan with this name already exists.");
        }
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(request.name().trim())
                .priceMonthly(request.priceMonthly())
                .maxDoctors(request.maxDoctors())
                .active(true)
                .build();
        return toResponse(repository.save(plan));
    }

    @Transactional
    public SubscriptionPlanResponse update(Long id, SubscriptionPlanRequest request) {
        SubscriptionPlan plan = find(id);
        repository.findByNameIgnoreCase(request.name().trim()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("A subscription plan with this name already exists.");
            }
        });
        plan.setName(request.name().trim());
        plan.setPriceMonthly(request.priceMonthly());
        plan.setMaxDoctors(request.maxDoctors());
        return toResponse(plan);
    }

    @Transactional
    public SubscriptionPlanResponse setActive(Long id, boolean active) {
        SubscriptionPlan plan = find(id);
        plan.setActive(active);
        return toResponse(plan);
    }

    SubscriptionPlan find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found."));
    }

    private SubscriptionPlanResponse toResponse(SubscriptionPlan p) {
        return new SubscriptionPlanResponse(p.getId(), p.getName(), p.getPriceMonthly(),
                p.getMaxDoctors(), p.isActive(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
