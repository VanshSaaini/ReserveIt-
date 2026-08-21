package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.response.ClinicUserHierarchyResponse;
import com.Reserveit.v1.dto.response.UserResponse;
import com.Reserveit.v1.entity.Appointment;
import com.Reserveit.v1.entity.Clinic;
import com.Reserveit.v1.entity.Doctor;
import com.Reserveit.v1.entity.Patient;
import com.Reserveit.v1.entity.User;
import com.Reserveit.v1.exception.ResourceNotFoundException;
import com.Reserveit.v1.repository.AppointmentRepository;
import com.Reserveit.v1.repository.ClinicRepository;
import com.Reserveit.v1.repository.ClinicSubscriptionRepository;
import com.Reserveit.v1.repository.DoctorRepository;
import com.Reserveit.v1.repository.PatientRepository;
import com.Reserveit.v1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Platform-wide operations available only to SUPER_ADMIN. */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClinicSubscriptionRepository subscriptionRepository;
    private final Mapper mapper;

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(mapper::toUserResponse).toList();
    }

    /**
     * Returns the platform account structure as Clinic -> Doctor -> Patients.
     * A patient is shown under a doctor when the patient has an appointment with
     * that doctor; this preserves the relationship represented by the current
     * domain model without inventing a direct patient-to-clinic relationship.
     */
    @Transactional(readOnly = true)
    public List<ClinicUserHierarchyResponse> userHierarchy() {
        List<Clinic> clinics = clinicRepository.findAll();
        List<Doctor> doctors = doctorRepository.findAll();
        List<Appointment> appointments = appointmentRepository.findAll();

        Map<Long, List<Doctor>> doctorsByClinic = doctors.stream()
                .collect(Collectors.groupingBy(d -> d.getClinic().getId(), LinkedHashMap::new, Collectors.toList()));

        Map<Long, Map<Long, Patient>> patientsByDoctor = new LinkedHashMap<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDoctor() == null || appointment.getPatient() == null) continue;
            patientsByDoctor
                    .computeIfAbsent(appointment.getDoctor().getId(), ignored -> new LinkedHashMap<>())
                    .put(appointment.getPatient().getId(), appointment.getPatient());
        }

        return clinics.stream().map(clinic -> {
            List<Doctor> clinicDoctors = doctorsByClinic.getOrDefault(clinic.getId(), List.of());
            List<ClinicUserHierarchyResponse.DoctorNode> doctorNodes = clinicDoctors.stream().map(doctor -> {
                List<ClinicUserHierarchyResponse.PatientNode> patients = patientsByDoctor
                        .getOrDefault(doctor.getId(), Map.of())
                        .values().stream()
                        .map(this::patientNode)
                        .toList();
                User user = doctor.getUser();
                return new ClinicUserHierarchyResponse.DoctorNode(
                        doctor.getId(), user.getId(), user.getFullName(), doctor.getSpecialization(),
                        user.getEmail(), user.isActive(), patients.size(), patients);
            }).toList();

            long patientCount = doctorNodes.stream().mapToLong(ClinicUserHierarchyResponse.DoctorNode::patientCount).sum();
            var subscription = subscriptionRepository.findByClinic_Id(clinic.getId()).orElse(null);

            return new ClinicUserHierarchyResponse(
                    clinic.getId(), clinic.getName(), clinic.isActive(),
                    subscription == null ? null : subscription.getSubscriptionPlan().getName(),
                    subscription == null ? null : subscription.getStatus().name(),
                    clinicDoctors.size(), patientCount, clinic.getCreatedAt(), doctorNodes);
        }).toList();
    }

    private ClinicUserHierarchyResponse.PatientNode patientNode(Patient patient) {
        User user = patient.getUser();
        return new ClinicUserHierarchyResponse.PatientNode(
                patient.getId(), user.getId(), user.getFullName(), user.getEmail(), user.getMobile(),
                user.isActive(), patient.getDateOfBirth(), user.getCreatedAt());
    }

    @Transactional
    public UserResponse setUserActive(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.setActive(active);
        return mapper.toUserResponse(user);
    }
}
