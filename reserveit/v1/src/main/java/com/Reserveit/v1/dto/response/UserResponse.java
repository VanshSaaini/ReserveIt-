package com.Reserveit.v1.dto.response;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String mobile,
        String role,
        boolean active
) {
}
