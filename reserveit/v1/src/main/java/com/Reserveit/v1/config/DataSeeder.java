package com.Reserveit.v1.config;

import com.Reserveit.v1.entity.Role;
import com.Reserveit.v1.entity.User;
import com.Reserveit.v1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ensures exactly one SUPER_ADMIN account exists on startup, since that role
 * can't be created through the public /api/auth/register endpoint.
 * Configure the bootstrap credentials via app.admin.email / app.admin.password.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.existsByRole(Role.SUPER_ADMIN)) {
            return;
        }

        User admin = User.builder()
                .firstName("Super")
                .lastName("Admin")
                .email(adminEmail)
                .mobile("0000000000")
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.SUPER_ADMIN)
                .active(true)
                .build();

        userRepository.save(admin);
        log.info("Seeded default SUPER_ADMIN account: {}", adminEmail);
    }
}
