package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.request.ClinicUpdateRequest;
import com.Reserveit.v1.dto.response.ClinicResponse;
import com.Reserveit.v1.entity.Clinic;
import com.Reserveit.v1.exception.ForbiddenActionException;
import com.Reserveit.v1.exception.ResourceNotFoundException;
import com.Reserveit.v1.repository.ClinicRepository;
import com.Reserveit.v1.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicManagementService {

    private final ClinicRepository clinicRepository;
    private final Mapper mapper;

    @Transactional(readOnly = true)
    public List<ClinicResponse> listActive(String search) {
        List<Clinic> clinics = (search == null || search.isBlank())
                ? clinicRepository.findByActiveTrue()
                : clinicRepository.findByNameContainingIgnoreCaseAndActiveTrue(search);
        return clinics.stream().map(mapper::toClinicResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClinicResponse getById(Long id) {
        return mapper.toClinicResponse(findClinicOrThrow(id));
    }

    @Transactional(readOnly = true)
    public ClinicResponse getMyClinic() {
        return mapper.toClinicResponse(findMyClinicEntity());
    }

    @Transactional
    public ClinicResponse updateMyClinic(ClinicUpdateRequest req) {
        Clinic clinic = findMyClinicEntity();
        clinic.setName(req.name());
        clinic.setAddress(req.address());
        clinic.setPhone(req.phone());
        clinic.setEmail(req.email());
        return mapper.toClinicResponse(clinic);
    }

    @Transactional(readOnly = true)
    public List<ClinicResponse> listAll() {
        return clinicRepository.findAll().stream().map(mapper::toClinicResponse).toList();
    }

    @Transactional
    public ClinicResponse setActive(Long clinicId, boolean active) {
        Clinic clinic = findClinicOrThrow(clinicId);
        clinic.setActive(active);
        return mapper.toClinicResponse(clinic);
    }

    Clinic findClinicOrThrow(Long id) {
        return clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found."));
    }

    /** Resolves + ownership-checks the clinic belonging to the currently logged-in clinic admin. */
    Clinic findMyClinicEntity() {
        Long userId = SecurityUtils.currentUserId();
        return clinicRepository.findByAdmin_Id(userId)
                .orElseThrow(() -> new ForbiddenActionException("No clinic is registered to this account."));
    }
}
