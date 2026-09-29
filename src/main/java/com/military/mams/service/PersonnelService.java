package com.military.mams.service;

import com.military.mams.dto.request.PersonnelRequest;
import com.military.mams.entity.Base;
import com.military.mams.entity.Personnel;
import com.military.mams.exception.BadRequestException;
import com.military.mams.exception.ForbiddenException;
import com.military.mams.exception.ResourceNotFoundException;
import com.military.mams.repository.BaseRepository;
import com.military.mams.repository.PersonnelRepository;
import com.military.mams.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonnelService {

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AuditService auditService;

    @Transactional(readOnly = true)
    public List<Personnel> getPersonnel(Long baseId, UserPrincipal currentUser) {
        Long targetBaseId = baseId;
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole())) {
            targetBaseId = currentUser.getBaseId();
        }

        if (targetBaseId != null) {
            return personnelRepository.findByBaseId(targetBaseId);
        }
        return personnelRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Personnel getPersonnelById(Long id, UserPrincipal currentUser) {
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel not found with id: " + id));

        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole()) &&
                !personnel.getBase().getId().equals(currentUser.getBaseId())) {
            throw new ForbiddenException("Access denied: Personnel belongs to another base installation.");
        }

        return personnel;
    }

    @Transactional
    public Personnel createPersonnel(PersonnelRequest request, UserPrincipal currentUser, String ipAddress) {
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole()) &&
                !currentUser.getBaseId().equals(request.getBaseId())) {
            throw new ForbiddenException("Base Commanders can only register personnel for their assigned base.");
        }

        if (personnelRepository.existsByServiceNumber(request.getServiceNumber())) {
            throw new BadRequestException("Personnel with service number '" + request.getServiceNumber() + "' already exists.");
        }

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base installation not found with id: " + request.getBaseId()));

        Personnel personnel = new Personnel(
                request.getName(),
                request.getServiceNumber(),
                base,
                request.getDesignation(),
                request.getRank()
        );

        if (request.getStatus() != null) {
            personnel.setStatus(request.getStatus());
        }

        Personnel saved = personnelRepository.save(personnel);

        auditService.logAction(
                currentUser.getUsername(),
                currentUser.getRole(),
                "PERSONNEL_CREATED",
                "Personnel",
                saved.getId(),
                null,
                "Enrolled personnel: " + saved.getRank() + " " + saved.getName() + " (" + saved.getServiceNumber() + ")",
                ipAddress
        );

        return saved;
    }

    @Transactional
    public Personnel updatePersonnel(Long id, PersonnelRequest request, UserPrincipal currentUser, String ipAddress) {
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel not found with id: " + id));

        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole()) &&
                !personnel.getBase().getId().equals(currentUser.getBaseId())) {
            throw new ForbiddenException("Access denied: You can only update personnel from your assigned installation.");
        }

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base installation not found with id: " + request.getBaseId()));

        String oldVal = "Name: " + personnel.getName() + ", Rank: " + personnel.getRank() + ", Base: " + personnel.getBase().getName();

        personnel.setName(request.getName());
        personnel.setBase(base);
        personnel.setDesignation(request.getDesignation());
        personnel.setRank(request.getRank());
        if (request.getStatus() != null) {
            personnel.setStatus(request.getStatus());
        }

        Personnel saved = personnelRepository.save(personnel);

        auditService.logAction(
                currentUser.getUsername(),
                currentUser.getRole(),
                "PERSONNEL_UPDATED",
                "Personnel",
                saved.getId(),
                oldVal,
                "Updated personnel: " + saved.getName(),
                ipAddress
        );

        return saved;
    }
}
