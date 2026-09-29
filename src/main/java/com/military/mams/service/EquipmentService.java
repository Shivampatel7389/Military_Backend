package com.military.mams.service;

import com.military.mams.dto.request.EquipmentRequest;
import com.military.mams.entity.Equipment;
import com.military.mams.entity.EquipmentType;
import com.military.mams.exception.BadRequestException;
import com.military.mams.exception.ResourceNotFoundException;
import com.military.mams.repository.EquipmentRepository;
import com.military.mams.repository.EquipmentTypeRepository;
import com.military.mams.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentTypeRepository equipmentTypeRepository;

    @Autowired
    private AuditService auditService;

    @Transactional(readOnly = true)
    public List<EquipmentType> getAllEquipmentTypes() {
        return equipmentTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Equipment> getAllEquipment(Long equipmentTypeId) {
        if (equipmentTypeId != null) {
            return equipmentRepository.findByEquipmentTypeId(equipmentTypeId);
        }
        return equipmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment asset not found with id: " + id));
    }

    @Transactional
    public Equipment createEquipment(EquipmentRequest request, UserPrincipal currentUser, String ipAddress) {
        if (equipmentRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Equipment with code '" + request.getCode() + "' already exists.");
        }

        EquipmentType type = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with id: " + request.getEquipmentTypeId()));

        Equipment equipment = new Equipment(
                request.getName(),
                request.getCode(),
                type,
                request.getDescription(),
                request.getUnitOfMeasure()
        );

        if (request.getStatus() != null) {
            equipment.setStatus(request.getStatus());
        }

        Equipment saved = equipmentRepository.save(equipment);

        auditService.logAction(
                currentUser.getUsername(),
                currentUser.getRole(),
                "EQUIPMENT_CREATED",
                "Equipment",
                saved.getId(),
                null,
                "Registered equipment: " + saved.getName() + " (" + saved.getCode() + ")",
                ipAddress
        );

        return saved;
    }

    @Transactional
    public Equipment updateEquipment(Long id, EquipmentRequest request, UserPrincipal currentUser, String ipAddress) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));

        EquipmentType type = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment type not found with id: " + request.getEquipmentTypeId()));

        String oldVal = "Name: " + equipment.getName() + ", Type: " + equipment.getEquipmentType().getName();

        equipment.setName(request.getName());
        equipment.setEquipmentType(type);
        equipment.setDescription(request.getDescription());
        if (request.getUnitOfMeasure() != null) {
            equipment.setUnitOfMeasure(request.getUnitOfMeasure());
        }
        if (request.getStatus() != null) {
            equipment.setStatus(request.getStatus());
        }

        Equipment saved = equipmentRepository.save(equipment);

        auditService.logAction(
                currentUser.getUsername(),
                currentUser.getRole(),
                "EQUIPMENT_UPDATED",
                "Equipment",
                saved.getId(),
                oldVal,
                "Updated equipment: " + saved.getName(),
                ipAddress
        );

        return saved;
    }
}
