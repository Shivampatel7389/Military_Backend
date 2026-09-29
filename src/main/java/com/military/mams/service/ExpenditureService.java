package com.military.mams.service;

import com.military.mams.dto.request.ExpenditureRequest;
import com.military.mams.entity.Base;
import com.military.mams.entity.Equipment;
import com.military.mams.entity.Expenditure;
import com.military.mams.entity.User;
import com.military.mams.exception.BadRequestException;
import com.military.mams.exception.ForbiddenException;
import com.military.mams.exception.InsufficientStockException;
import com.military.mams.exception.ResourceNotFoundException;
import com.military.mams.repository.BaseRepository;
import com.military.mams.repository.EquipmentRepository;
import com.military.mams.repository.ExpenditureRepository;
import com.military.mams.repository.UserRepository;
import com.military.mams.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenditureService {

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuditService auditService;

    @Transactional(readOnly = true)
    public List<Expenditure> getExpenditures(Long baseId, Long equipmentTypeId, Long equipmentId,
                                            String reason, LocalDate startDate, LocalDate endDate,
                                            UserPrincipal currentUser) {
        Long targetBaseId = baseId;
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole())) {
            targetBaseId = currentUser.getBaseId();
        }

        return expenditureRepository.findWithFilters(targetBaseId, equipmentTypeId, equipmentId, reason, startDate, endDate);
    }

    @Transactional
    public Expenditure recordExpenditure(ExpenditureRequest request, UserPrincipal currentUser, String ipAddress) {
        if ("ROLE_LOGISTICS_OFFICER".equals(currentUser.getRole())) {
            throw new ForbiddenException("Logistics Officers are restricted from authorizing asset expenditures.");
        }

        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole()) &&
                !currentUser.getBaseId().equals(request.getBaseId())) {
            throw new ForbiddenException("Base Commanders can only record expenditures for their assigned base.");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Expenditure quantity must be greater than zero.");
        }

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with id: " + request.getBaseId()));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + request.getEquipmentId()));

        // Validate stock availability in the base armory
        long availableStock = dashboardService.getCurrentAvailableStock(base.getId(), equipment.getId());
        if (availableStock < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient asset balance for expenditure at " + base.getName() +
                    ". Available in armory: " + availableStock + ", Requested: " + request.getQuantity()
            );
        }

        User user = userRepository.findById(currentUser.getId()).orElse(null);

        String code = "EXP-" + LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Expenditure expenditure = new Expenditure(
                code,
                base,
                equipment,
                request.getQuantity(),
                request.getExpenditureDate(),
                request.getReason(),
                request.getRemarks(),
                user
        );

        Expenditure saved = expenditureRepository.save(expenditure);

        auditService.logAction(
                currentUser.getUsername(),
                currentUser.getRole(),
                "EXPENDITURE_RECORDED",
                "Expenditure",
                saved.getId(),
                "Prior armory stock: " + availableStock,
                "Expended " + saved.getQuantity() + "x " + equipment.getName() + " at " + base.getName() + " [Reason: " + request.getReason() + "] (Code: " + code + ")",
                ipAddress
        );

        return saved;
    }
}
