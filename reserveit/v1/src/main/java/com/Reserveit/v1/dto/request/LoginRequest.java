package com.Reserveit.v1.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password,
        Boolean rememberMe
) {}
