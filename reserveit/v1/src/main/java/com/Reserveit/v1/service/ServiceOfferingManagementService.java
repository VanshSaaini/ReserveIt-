package com.Reserveit.v1.service;

import com.Reserveit.v1.dto.request.ServiceOfferingRequest;
import com.Reserveit.v1.dto.response.ServiceOfferingResponse;
import com.Reserveit.v1.entity.Clinic;
import com.Reserveit.v1.entity.ServiceOffering;
import com.Reserveit.v1.exception.ForbiddenActionException;
import com.Reserveit.v1.exception.ResourceNotFoundException;
import com.Reserveit.v1.repository.ServiceOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceOfferingManagementService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ClinicManagementService clinicManagementService;
    private final Mapper mapper;

    @Transactional(readOnly = true)
    public List<ServiceOfferingResponse> listByClinic(Long clinicId) {
        return serviceOfferingRepository.findByClinic_IdAndActiveTrue(clinicId)
                .stream().map(mapper::toServiceResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceOfferingResponse> listMine() {
        Clinic clinic = clinicManagementService.findMyClinicEntity();
        return serviceOfferingRepository.findByClinic_IdAndActiveTrue(clinic.getId())
                .stream().map(mapper::toServiceResponse).toList();
    }

    @Transactional
    public ServiceOfferingResponse create(ServiceOfferingRequest req) {
        Clinic clinic = clinicManagementService.findMyClinicEntity();
        ServiceOffering offering = ServiceOffering.builder()
                .clinic(clinic)
                .name(req.name())
                .description(req.description())
                .durationMinutes(req.durationMinutes())
                .price(req.price())
                .active(true)
                .build();
        return mapper.toServiceResponse(serviceOfferingRepository.save(offering));
    }

    @Transactional
    public ServiceOfferingResponse update(Long id, ServiceOfferingRequest req) {
        ServiceOffering offering = findOwnedOrThrow(id);
        offering.setName(req.name());
        offering.setDescription(req.description());
        offering.setDurationMinutes(req.durationMinutes());
        offering.setPrice(req.price());
        return mapper.toServiceResponse(offering);
    }

    @Transactional
    public void delete(Long id) {
        ServiceOffering offering = findOwnedOrThrow(id);
        offering.setActive(false);
    }

    private ServiceOffering findOwnedOrThrow(Long id) {
        ServiceOffering offering = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found."));
        Clinic myClinic = clinicManagementService.findMyClinicEntity();
        if (!myClinic.getId().equals(offering.getClinic().getId())) {
            throw new ForbiddenActionException("This service does not belong to your clinic.");
        }
        return offering;
    }
}
