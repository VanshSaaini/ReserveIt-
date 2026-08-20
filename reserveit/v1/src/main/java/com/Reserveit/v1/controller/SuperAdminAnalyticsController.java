package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.response.SuperAdminAnalyticsResponse;
import com.Reserveit.v1.service.SuperAdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminAnalyticsController {

    private final SuperAdminAnalyticsService analyticsService;

    @GetMapping
    public SuperAdminAnalyticsResponse dashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.dashboard(date, month);
    }
}
