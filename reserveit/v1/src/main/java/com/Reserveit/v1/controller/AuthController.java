package com.Reserveit.v1.controller;

import com.Reserveit.v1.dto.request.LoginRequest;
import com.Reserveit.v1.dto.request.RegisterRequest;
import com.Reserveit.v1.dto.response.AuthResponse;
import com.Reserveit.v1.dto.response.RegisterResponse;
import com.Reserveit.v1.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Matches src/api/client.js on the frontend: POST /api/auth/login and /api/auth/register. */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
