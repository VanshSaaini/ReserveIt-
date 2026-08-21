package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.request.DoctorCreateRequest;
import com.Reserveit.v1.dto.request.DoctorUpdateRequest;
import com.Reserveit.v1.dto.response.DoctorResponse;
import com.Reserveit.v1.entity.Clinic;
import com.Reserveit.v1.entity.Doctor;
import com.Reserveit.v1.entity.Role;
import com.Reserveit.v1.entity.User;
import com.Reserveit.v1.exception.DuplicateResourceException;
import com.Reserveit.v1.exception.ForbiddenActionException;
import com.Reserveit.v1.exception.ResourceNotFoundException;
import com.Reserveit.v1.repository.DoctorRepository;
import com.Reserveit.v1.repository.UserRepository;
import com.Reserveit.v1.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorManagementService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ClinicManagementService clinicManagementService;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;
    private final ClinicSubscriptionService clinicSubscriptionService;

    @Transactional(readOnly = true)
    public List<DoctorResponse> listByClinic(Long clinicId) {
        return doctorRepository.findByClinic_IdAndUser_ActiveTrue(clinicId).stream().map(mapper::toDoctorResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> listMyClinicDoctors() {
        Clinic clinic = clinicManagementService.findMyClinicEntity();
        return doctorRepository.findByClinic_Id(clinic.getId()).stream().map(mapper::toDoctorResponse).toList();
    }

    @Transactional(readOnly = true)
    public DoctorResponse getMyProfile() {
        return mapper.toDoctorResponse(findMyDoctorEntity());
    }

    @Transactional
    public DoctorResponse updateMyProfile(DoctorUpdateRequest req) {
        Doctor doctor = findMyDoctorEntity();
        User user = doctor.getUser();
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setMobile(req.mobile());
        doctor.setSpecialization(req.specialization());
        doctor.setQualifications(req.qualifications());
        doctor.setExperienceYears(req.experienceYears());
        if (req.defaultSlotMinutes() != null && req.defaultSlotMinutes() > 0) {
            doctor.setDefaultSlotMinutes(req.defaultSlotMinutes());
        }
        return mapper.toDoctorResponse(doctor);
    }

    @Transactional(readOnly = true)
    public DoctorResponse getById(Long doctorId) {
        return mapper.toDoctorResponse(findDoctorOrThrow(doctorId));
    }

    @Transactional(readOnly = true)
    public void assertCanAddDoctor(Long clinicId) {
        clinicSubscriptionService.assertCanAddDoctor(clinicId);
    }

    @Transactional
    public DoctorResponse createDoctor(DoctorCreateRequest req) {
        Clinic clinic = clinicManagementService.findMyClinicEntity();
        clinicSubscriptionService.assertCanAddDoctor(clinic.getId());

        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new DuplicateResourceException("An account with this email already exists.");
        }

        User user = User.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .mobile(req.mobile())
                .password(passwordEncoder.encode(req.password()))
                .role(Role.DOCTOR)
                .active(true)
                .build();
        user = userRepository.save(user);

        Doctor doctor = Doctor.builder()
                .user(user)
                .clinic(clinic)
                .specialization(req.specialization())
                .qualifications(req.qualifications())
                .experienceYears(req.experienceYears())
                .defaultSlotMinutes(req.defaultSlotMinutes() != null ? req.defaultSlotMinutes() : 30)
                .build();
        doctor = doctorRepository.save(doctor);

        return mapper.toDoctorResponse(doctor);
    }

    @Transactional
    public DoctorResponse setActive(Long doctorId, boolean active) {
        Doctor doctor = findDoctorOrThrow(doctorId);
        assertOwnsDoctorOrIsAdmin(doctor);
        doctor.getUser().setActive(active);
        return mapper.toDoctorResponse(doctor);
    }

    Doctor findDoctorOrThrow(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found."));
    }

    Doctor findMyDoctorEntity() {
        Long userId = SecurityUtils.currentUserId();
        return doctorRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ForbiddenActionException("No doctor profile is linked to this account."));
    }

    private void assertOwnsDoctorOrIsAdmin(Doctor doctor) {
        var principal = SecurityUtils.currentPrincipal();
        if ("SUPER_ADMIN".equals(principal.getRole())) {
            return;
        }
        Clinic myClinic = clinicManagementService.findMyClinicEntity();
        if (!myClinic.getId().equals(doctor.getClinic().getId())) {
            throw new ForbiddenActionException("This doctor does not belong to your clinic.");
        }
    }
}
