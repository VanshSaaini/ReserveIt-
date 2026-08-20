package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.request.PatientUpdateRequest;
import com.Reserveit.v1.dto.response.PatientResponse;
import com.Reserveit.v1.entity.Patient;
import com.Reserveit.v1.exception.ForbiddenActionException;
import com.Reserveit.v1.repository.PatientRepository;
import com.Reserveit.v1.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Patient self-service (the "My profile" page). Kept in its own service —
 * matching ClinicManagementService / DoctorManagementService — instead of
 * talking to the repository directly from the controller, so the read (which
 * touches the lazy Patient -> User association) and the write both run
 * inside a defined transaction rather than depending on Open Session In View.
 */
@Service
@RequiredArgsConstructor
public class PatientManagementService {

    private final PatientRepository patientRepository;
    private final Mapper mapper;

    @Transactional(readOnly = true)
    public PatientResponse getMyProfile() {
        return mapper.toPatientResponse(findMyPatientEntity());
    }

    @Transactional
    public PatientResponse updateMyProfile(PatientUpdateRequest req) {
        Patient patient = findMyPatientEntity();
        var user = patient.getUser();
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setMobile(req.mobile());
        patient.setDateOfBirth(req.dateOfBirth());
        return mapper.toPatientResponse(patient);
    }

    private Patient findMyPatientEntity() {
        Long userId = SecurityUtils.currentUserId();
        return patientRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ForbiddenActionException("No patient profile is linked to this account."));
    }
}
