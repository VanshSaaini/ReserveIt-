package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.request.LoginRequest;
import com.Reserveit.v1.dto.request.RegisterRequest;
import com.Reserveit.v1.dto.response.AuthResponse;
import com.Reserveit.v1.dto.response.RegisterResponse;
import com.Reserveit.v1.entity.Clinic;
import com.Reserveit.v1.entity.Patient;
import com.Reserveit.v1.entity.Role;
import com.Reserveit.v1.entity.User;
import com.Reserveit.v1.exception.DuplicateResourceException;
import com.Reserveit.v1.repository.ClinicRepository;
import com.Reserveit.v1.repository.DoctorRepository;
import com.Reserveit.v1.repository.PatientRepository;
import com.Reserveit.v1.repository.UserRepository;
import com.Reserveit.v1.security.AppUserPrincipal;
import com.Reserveit.v1.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new DuplicateResourceException("An account with this email already exists.");
        }

        boolean isClinic = "clinic".equalsIgnoreCase(req.accountType());

        User user = User.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .mobile(req.mobile())
                .password(passwordEncoder.encode(req.password()))
                .role(isClinic ? Role.CLINIC_ADMIN : Role.PATIENT)
                .active(true)
                .build();
        user = userRepository.save(user);

        Long clinicId = null;

        if (isClinic) {
            Clinic clinic = Clinic.builder()
                    .name(req.clinicName())
                    .address(req.clinicAddress())
                    .phone(req.mobile())
                    .email(req.email())
                    .admin(user)
                    .active(true)
                    .build();
            clinic = clinicRepository.save(clinic);
            clinicId = clinic.getId();
        } else {
            LocalDate dob = null;
            if (req.dob() != null && !req.dob().isBlank()) {
                dob = LocalDate.parse(req.dob());
            }
            Patient patient = Patient.builder()
                    .user(user)
                    .dateOfBirth(dob)
                    .build();
            patientRepository.save(patient);
        }

        String message = isClinic
                ? "Clinic account created. You can now log in and start setting up your clinic."
                : "Account created. You can now log in and start booking appointments.";

        return new RegisterResponse(user.getId(), user.getEmail(), user.getRole().name(), clinicId, message);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        User user = userRepository.findByEmailIgnoreCase(req.username())
                .orElseThrow(() -> new org.springframework.security.authentication.BadCredentialsException("Invalid email or password."));

        AppUserPrincipal principal = new AppUserPrincipal(user);
        String token = jwtService.generateToken(principal);

        Long clinicId = resolveClinicId(user);

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFirstName(),
                user.getLastName(), user.getRole().name(), clinicId);
    }

    private Long resolveClinicId(User user) {
        return switch (user.getRole()) {
            case CLINIC_ADMIN -> clinicRepository.findByAdmin_Id(user.getId()).map(Clinic::getId).orElse(null);
            case DOCTOR -> doctorRepository.findByUser_Id(user.getId())
                    .map(d -> d.getClinic().getId()).orElse(null);
            default -> null;
        };
    }
}
