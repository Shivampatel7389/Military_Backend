package com.military.mams.service;

import com.military.mams.dto.request.PurchaseRequest;
import com.military.mams.entity.Base;
import com.military.mams.entity.Equipment;
import com.military.mams.entity.Purchase;
import com.military.mams.entity.User;
import com.military.mams.exception.ForbiddenException;
import com.military.mams.exception.ResourceNotFoundException;
import com.military.mams.repository.BaseRepository;
import com.military.mams.repository.EquipmentRepository;
import com.military.mams.repository.PurchaseRepository;
import com.military.mams.repository.UserRepository;
import com.military.mams.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditService auditService;

    @Transactional(readOnly = true)
    public List<Purchase> getPurchases(Long baseId, Long equipmentTypeId, Long equipmentId,
                                       LocalDate startDate, LocalDate endDate, UserPrincipal currentUser) {
        Long targetBaseId = baseId;
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole())) {
            targetBaseId = currentUser.getBaseId();
        }

        return purchaseRepository.findWithFilters(targetBaseId, equipmentTypeId, equipmentId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Purchase getPurchaseById(Long id, UserPrincipal currentUser) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));

        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole()) &&
                !purchase.getBase().getId().equals(currentUser.getBaseId())) {
            throw new ForbiddenException("Access denied: Purchase order belongs to another base installation.");
        }

        return purchase;
    }

    @Transactional
    public Purchase createPurchase(PurchaseRequest request, UserPrincipal currentUser, String ipAddress) {
        // Enforce base scoping for Base Commander
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole()) &&
                !currentUser.getBaseId().equals(request.getBaseId())) {
            throw new ForbiddenException("Base Commanders can only record procurements for their assigned base.");
        }

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base installation not found with id: " + request.getBaseId()));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment asset not found with id: " + request.getEquipmentId()));

        User user = userRepository.findById(currentUser.getId()).orElse(null);

        String refNum = request.getReferenceNumber();
        if (refNum == null || refNum.trim().isEmpty()) {
            refNum = "PO-" + LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        }

        Purchase purchase = new Purchase(
                refNum,
                base,
                equipment,
                request.getQuantity(),
                request.getPurchaseDate(),
                request.getSupplier(),
                request.getRemarks(),
                user
        );

        Purchase saved = purchaseRepository.save(purchase);

        auditService.logAction(
                currentUser.getUsername(),
                currentUser.getRole(),
                "PURCHASE_CREATED",
                "Purchase",
                saved.getId(),
                null,
                "Recorded purchase of " + saved.getQuantity() + "x " + equipment.getName() + " for " + base.getName() + " (Ref: " + refNum + ")",
                ipAddress
        );

        return saved;
    }
}
