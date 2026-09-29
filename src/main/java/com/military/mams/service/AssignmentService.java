package com.military.mams.service;

import com.military.mams.dto.request.AssignmentRequest;
import com.military.mams.entity.Assignment;
import com.military.mams.entity.Base;
import com.military.mams.entity.Equipment;
import com.military.mams.entity.Personnel;
import com.military.mams.entity.User;
import com.military.mams.exception.BadRequestException;
import com.military.mams.exception.ForbiddenException;
import com.military.mams.exception.InsufficientStockException;
import com.military.mams.exception.ResourceNotFoundException;
import com.military.mams.repository.AssignmentRepository;
import com.military.mams.repository.BaseRepository;
import com.military.mams.repository.EquipmentRepository;
import com.military.mams.repository.PersonnelRepository;
import com.military.mams.repository.UserRepository;
import com.military.mams.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuditService auditService;

    @Transactional(readOnly = true)
    public List<Assignment> getAssignments(Long baseId, Long equipmentTypeId, Long equipmentId,
                                          Long personnelId, String status,
                                          LocalDate startDate, LocalDate endDate,
                                          UserPrincipal currentUser) {
        Long targetBaseId = baseId;
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole())) {
            targetBaseId = currentUser.getBaseId();
        }

        return assignmentRepository.findWithFilters(targetBaseId, equipmentTypeId, equipmentId, personnelId, status, startDate, endDate);
    }

    @Transactional
    public Assignment createAssignment(AssignmentRequest request, UserPrincipal currentUser, String ipAddress) {
        if ("ROLE_LOGISTICS_OFFICER".equals(currentUser.getRole())) {
            throw new ForbiddenException("Logistics Officers are restricted from issuing personnel assignments.");
        }

        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole()) &&
                !currentUser.getBaseId().equals(request.getBaseId())) {
            throw new ForbiddenException("Base Commanders can only assign assets within their designated installation.");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Assignment quantity must be greater than zero.");
        }

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with id: " + request.getBaseId()));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + request.getEquipmentId()));

        Personnel personnel = personnelRepository.findById(request.getPersonnelId())
                .orElseThrow(() -> new ResourceNotFoundException("Personnel not found with id: " + request.getPersonnelId()));

        // Validate personnel belongs to the base or armory
        if (!personnel.getBase().getId().equals(base.getId())) {
            throw new BadRequestException("Recipient personnel is stationed at " + personnel.getBase().getName() + ", not " + base.getName());
        }

        // Validate available armory inventory
        long availableStock = dashboardService.getCurrentAvailableStock(base.getId(), equipment.getId());
        if (availableStock < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient unassigned assets at " + base.getName() + ". Available ready in armory: " +
                    availableStock + ", Requested assignment: " + request.getQuantity()
            );
        }

        User user = userRepository.findById(currentUser.getId()).orElse(null);

        String code = "ASG-" + LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Assignment assignment = new Assignment(
                code,
                personnel,
                base,
                equipment,
                request.getQuantity(),
                request.getAssignmentDate(),
                request.getRemarks(),
                user
        );

        Assignment saved = assignmentRepository.save(assignment);

        auditService.logAction(
                currentUser.getUsername(),
                currentUser.getRole(),
                "ASSIGNMENT_CREATED",
                "Assignment",
                saved.getId(),
                null,
                "Assigned " + saved.getQuantity() + "x " + equipment.getName() + " to " + personnel.getRank() + " " + personnel.getName() + " (Code: " + code + ")",
                ipAddress
        );

        return saved;
    }

    @Transactional
    public Assignment returnAssignment(Long id, String remarks, UserPrincipal currentUser, String ipAddress) {
        if ("ROLE_LOGISTICS_OFFICER".equals(currentUser.getRole())) {
            throw new ForbiddenException("Logistics Officers are restricted from checking in personnel assignments.");
        }

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment record not found with id: " + id));

        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole()) &&
                !assignment.getBase().getId().equals(currentUser.getBaseId())) {
            throw new ForbiddenException("Access denied: Assignment belongs to another installation.");
        }

        if ("RETURNED".equals(assignment.getStatus())) {
            throw new BadRequestException("Asset has already been checked back into the armory.");
        }

        assignment.setStatus("RETURNED");
        assignment.setReturnDate(LocalDate.now());
        if (remarks != null && !remarks.trim().isEmpty()) {
            assignment.setRemarks(assignment.getRemarks() != null ? assignment.getRemarks() + " | Check-in: " + remarks : remarks);
        }

        Assignment updated = assignmentRepository.save(assignment);

        auditService.logAction(
                currentUser.getUsername(),
                currentUser.getRole(),
                "ASSIGNMENT_RETURNED",
                "Assignment",
                updated.getId(),
                "Status: ACTIVE",
                "Checked in " + updated.getQuantity() + "x " + updated.getEquipment().getName() + " from " + updated.getPersonnel().getName(),
                ipAddress
        );

        return updated;
    }
}
