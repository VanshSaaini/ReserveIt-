package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.response.ClinicAnalyticsResponse;
import com.Reserveit.v1.service.ClinicAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/clinics/me/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLINIC_ADMIN')")
public class ClinicAnalyticsController {

    private final ClinicAnalyticsService analyticsService;

    @GetMapping
    public ClinicAnalyticsResponse dashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.getDashboard(date, month);
    }
}
