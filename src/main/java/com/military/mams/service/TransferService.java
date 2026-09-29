package com.military.mams.service;

import com.military.mams.dto.request.TransferRequest;
import com.military.mams.entity.Base;
import com.military.mams.entity.Equipment;
import com.military.mams.entity.Transfer;
import com.military.mams.entity.User;
import com.military.mams.exception.BadRequestException;
import com.military.mams.exception.ForbiddenException;
import com.military.mams.exception.InsufficientStockException;
import com.military.mams.exception.ResourceNotFoundException;
import com.military.mams.repository.BaseRepository;
import com.military.mams.repository.EquipmentRepository;
import com.military.mams.repository.TransferRepository;
import com.military.mams.repository.UserRepository;
import com.military.mams.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransferService {

    @Autowired
    private TransferRepository transferRepository;

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
    public List<Transfer> getTransfers(Long baseId, Long fromBaseId, Long toBaseId,
                                       Long equipmentTypeId, Long equipmentId,
                                       LocalDate startDate, LocalDate endDate,
                                       UserPrincipal currentUser) {
        Long targetBaseId = baseId;
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole())) {
            targetBaseId = currentUser.getBaseId();
        }

        return transferRepository.findWithFilters(targetBaseId, fromBaseId, toBaseId, equipmentTypeId, equipmentId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Transfer getTransferById(Long id, UserPrincipal currentUser) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer order not found with id: " + id));

        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole())) {
            Long cmdBaseId = currentUser.getBaseId();
            if (!transfer.getFromBase().getId().equals(cmdBaseId) && !transfer.getToBase().getId().equals(cmdBaseId)) {
                throw new ForbiddenException("Access denied: Transfer does not involve your designated installation.");
            }
        }

        return transfer;
    }

    /**
     * Executes inter-base asset transfer transactionally.
     * Enforces stock validation, prevents identical origin/destination, and writes audit trail.
     */
    @Transactional
    public Transfer createTransfer(TransferRequest request, UserPrincipal currentUser, String ipAddress) {
        if (request.getFromBaseId().equals(request.getToBaseId())) {
            throw new BadRequestException("Transfer violation: Origin base and Destination base cannot be the same installation.");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Transfer quantity must be greater than zero.");
        }

        // Base Commander clearance validation: can only transfer assets OUT from their own base
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole()) &&
                !currentUser.getBaseId().equals(request.getFromBaseId())) {
            throw new ForbiddenException("Base Commanders can only dispatch transfers originating from their assigned base.");
        }

        Base fromBase = baseRepository.findById(request.getFromBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Source base installation not found with id: " + request.getFromBaseId()));

        Base toBase = baseRepository.findById(request.getToBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination base installation not found with id: " + request.getToBaseId()));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment asset not found with id: " + request.getEquipmentId()));

        // Crucial validation: Source base must have sufficient unassigned available quantity
        long availableStock = dashboardService.getCurrentAvailableStock(request.getFromBaseId(), request.getEquipmentId());
        if (availableStock < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient asset stock at " + fromBase.getName() + ". Available ready in armory: " +
                    availableStock + ", Requested transfer: " + request.getQuantity()
            );
        }

        User user = userRepository.findById(currentUser.getId()).orElse(null);

        String refNum = "TO-" + LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Transfer transfer = new Transfer(
                refNum,
                fromBase,
                toBase,
                equipment,
                request.getQuantity(),
                request.getTransferDate(),
                "COMPLETED",
                request.getRemarks(),
                user
        );

        Transfer saved = transferRepository.save(transfer);

        auditService.logAction(
                currentUser.getUsername(),
                currentUser.getRole(),
                "TRANSFER_EXECUTED",
                "Transfer",
                saved.getId(),
                "Origin " + fromBase.getName() + " balance: " + availableStock,
                "Transferred " + saved.getQuantity() + "x " + equipment.getName() + " from " + fromBase.getCode() + " to " + toBase.getCode() + " (Ref: " + refNum + ")",
                ipAddress
        );

        return saved;
    }
}
