package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.request.AvailabilityRequest;
import com.Reserveit.v1.dto.response.AvailabilityResponse;
import com.Reserveit.v1.dto.response.SlotResponse;
import com.Reserveit.v1.entity.Appointment;
import com.Reserveit.v1.entity.AppointmentStatus;
import com.Reserveit.v1.entity.Doctor;
import com.Reserveit.v1.entity.DoctorAvailability;
import com.Reserveit.v1.exception.BadRequestException;
import com.Reserveit.v1.exception.ForbiddenActionException;
import com.Reserveit.v1.exception.ResourceNotFoundException;
import com.Reserveit.v1.repository.AppointmentRepository;
import com.Reserveit.v1.repository.DoctorAvailabilityRepository;
import com.Reserveit.v1.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityManagementService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorManagementService doctorManagementService;
    private final Mapper mapper;

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> listMine() {
        Doctor doctor = doctorManagementService.findMyDoctorEntity();
        return availabilityRepository.findByDoctor_Id(doctor.getId())
                .stream().map(mapper::toAvailabilityResponse).toList();
    }

    @Transactional
    public AvailabilityResponse create(AvailabilityRequest req) {
        Doctor doctor = doctorManagementService.findMyDoctorEntity();

        if (!req.startTime().isBefore(req.endTime())) {
            throw new BadRequestException("startTime must be before endTime.");
        }

        DoctorAvailability availability = DoctorAvailability.builder()
                .doctor(doctor)
                .dayOfWeek(req.dayOfWeek())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .slotDurationMinutes(req.slotDurationMinutes() != null ? req.slotDurationMinutes() : doctor.getDefaultSlotMinutes())
                .active(true)
                .build();

        return mapper.toAvailabilityResponse(availabilityRepository.save(availability));
    }

    @Transactional
    public void delete(Long availabilityId) {
        DoctorAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability window not found."));
        Doctor doctor = doctorManagementService.findMyDoctorEntity();
        if (!doctor.getId().equals(availability.getDoctor().getId())) {
            throw new ForbiddenActionException("This availability window does not belong to you.");
        }
        availabilityRepository.delete(availability);
    }

    /** Computes the bookable slots for a doctor on a given date, factoring out already-booked ones. */
    public List<SlotResponse> getAvailableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found."));

        List<DoctorAvailability> windows = availabilityRepository
                .findByDoctor_IdAndDayOfWeekAndActiveTrue(doctorId, date.getDayOfWeek());

        List<Appointment> booked = appointmentRepository
                .findByDoctor_IdAndAppointmentDateAndStatusNot(doctorId, date, AppointmentStatus.CANCELLED);

        List<SlotResponse> slots = new ArrayList<>();

        for (DoctorAvailability window : windows) {
            int stepMinutes = window.getSlotDurationMinutes();
            LocalTime cursor = window.getStartTime();

            while (!cursor.plusMinutes(stepMinutes).isAfter(window.getEndTime())) {
                LocalTime slotEnd = cursor.plusMinutes(stepMinutes);
                LocalTime slotStart = cursor;

                boolean overlapsExisting = booked.stream().anyMatch(a ->
                        slotStart.isBefore(a.getEndTime()) && a.getStartTime().isBefore(slotEnd));

                slots.add(new SlotResponse(slotStart, slotEnd, !overlapsExisting));
                cursor = slotEnd;
            }
        }

        return slots;
    }
}
