package com.military.mams.service;

import com.military.mams.dto.response.DashboardMetricsResponse;
import com.military.mams.dto.response.NetMovementDetailResponse;
import com.military.mams.entity.EquipmentType;
import com.military.mams.entity.Purchase;
import com.military.mams.entity.Transfer;
import com.military.mams.repository.*;
import com.military.mams.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for computing military asset balance ledgers,
 * net movements, stock reconciliation, and category breakdowns.
 */
@Service
public class DashboardService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private EquipmentTypeRepository equipmentTypeRepository;

    /**
     * Calculates current unassigned available stock in the armory for a specific base and equipment.
     * Formula: Purchases + Transfers In - Transfers Out - Expended - Active Assignments.
     */
    @Transactional(readOnly = true)
    public long getCurrentAvailableStock(Long baseId, Long equipmentId) {
        if (baseId == null || equipmentId == null) {
            return 0;
        }

        long totalPurchases = purchaseRepository.sumQuantityBeforeDate(baseId, null, equipmentId, null);
        long totalTransfersIn = transferRepository.sumTransfersInBeforeDate(baseId, null, equipmentId, null);
        long totalTransfersOut = transferRepository.sumTransfersOutBeforeDate(baseId, null, equipmentId, null);
        long totalExpended = expenditureRepository.sumExpendedBeforeDate(baseId, null, equipmentId, null);
        long activeAssigned = assignmentRepository.sumActiveAssignedQuantity(baseId, null, equipmentId);

        long closing = totalPurchases + totalTransfersIn - totalTransfersOut - totalExpended;
        return Math.max(0, closing - activeAssigned);
    }

    /**
     * Calculates dashboard metrics adhering to the exact mathematical formulas:
     * Net Movement = Purchases + Transfer In - Transfer Out
     * Closing Balance = Opening Balance + Net Movement - Expended
     */
    @Transactional(readOnly = true)
    public DashboardMetricsResponse getDashboardMetrics(Long baseId, Long equipmentTypeId,
                                                        LocalDate startDate, LocalDate endDate,
                                                        UserPrincipal currentUser) {
        Long targetBaseId = baseId;
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole())) {
            targetBaseId = currentUser.getBaseId();
        }

        DashboardMetricsResponse response = computeMetricsForFilter(targetBaseId, equipmentTypeId, startDate, endDate);

        // Compute breakdown across equipment categories (Vehicles, Weapons, Ammunition, etc.)
        List<EquipmentType> allTypes = equipmentTypeRepository.findAll();
        List<DashboardMetricsResponse.CategorySummary> categoryBreakdown = new ArrayList<>();

        for (EquipmentType type : allTypes) {
            DashboardMetricsResponse typeMetrics = computeMetricsForFilter(targetBaseId, type.getId(), startDate, endDate);
            categoryBreakdown.add(new DashboardMetricsResponse.CategorySummary(
                    type.getName(),
                    typeMetrics.getOpeningBalance(),
                    typeMetrics.getPurchases(),
                    typeMetrics.getTransfersIn(),
                    typeMetrics.getTransfersOut(),
                    typeMetrics.getNetMovement(),
                    typeMetrics.getAssigned(),
                    typeMetrics.getExpended(),
                    typeMetrics.getClosingBalance(),
                    typeMetrics.getAvailableStock()
            ));
        }

        response.setCategoryBreakdown(categoryBreakdown);
        return response;
    }

    private DashboardMetricsResponse computeMetricsForFilter(Long baseId, Long equipmentTypeId,
                                                             LocalDate startDate, LocalDate endDate) {
        DashboardMetricsResponse metrics = new DashboardMetricsResponse();

        // 1. Opening Balance (Stock prior to startDate)
        long priorPurchases = 0;
        long priorTransfersIn = 0;
        long priorTransfersOut = 0;
        long priorExpended = 0;

        if (startDate != null) {
            priorPurchases = purchaseRepository.sumQuantityBeforeDate(baseId, equipmentTypeId, null, startDate);
            if (baseId != null) {
                priorTransfersIn = transferRepository.sumTransfersInBeforeDate(baseId, equipmentTypeId, null, startDate);
                priorTransfersOut = transferRepository.sumTransfersOutBeforeDate(baseId, equipmentTypeId, null, startDate);
            }
            priorExpended = expenditureRepository.sumExpendedBeforeDate(baseId, equipmentTypeId, null, startDate);
        }

        long openingBalance = Math.max(0, priorPurchases + priorTransfersIn - priorTransfersOut - priorExpended);
        metrics.setOpeningBalance(openingBalance);

        // 2. Current period movements
        long currentPurchases = purchaseRepository.sumQuantityBetweenDates(baseId, equipmentTypeId, null, startDate, endDate);
        long currentTransfersIn = 0;
        long currentTransfersOut = 0;

        if (baseId != null) {
            currentTransfersIn = transferRepository.sumTransfersInBetweenDates(baseId, equipmentTypeId, null, startDate, endDate);
            currentTransfersOut = transferRepository.sumTransfersOutBetweenDates(baseId, equipmentTypeId, null, startDate, endDate);
        }

        metrics.setPurchases(currentPurchases);
        metrics.setTransfersIn(currentTransfersIn);
        metrics.setTransfersOut(currentTransfersOut);

        // Net Movement = Purchases + Transfer In - Transfer Out
        long netMovement = currentPurchases + currentTransfersIn - currentTransfersOut;
        metrics.setNetMovement(netMovement);

        // 3. Expended & Assigned
        long currentExpended = expenditureRepository.sumExpendedBetweenDates(baseId, equipmentTypeId, null, startDate, endDate);
        long activeAssigned = assignmentRepository.sumActiveAssignedQuantity(baseId, equipmentTypeId, null);

        metrics.setExpended(currentExpended);
        metrics.setAssigned(activeAssigned);

        // 4. Closing Balance = Opening Balance + Net Movement - Expended
        long closingBalance = Math.max(0, openingBalance + netMovement - currentExpended);
        metrics.setClosingBalance(closingBalance);

        // 5. Available Stock in armory = Closing Balance - Assigned
        metrics.setAvailableStock(Math.max(0, closingBalance - activeAssigned));

        return metrics;
    }

    /**
     * Bonus Requirement: Returns detailed line items for Purchases, Transfers In,
     * and Transfers Out for the Net Movement popup modal.
     */
    @Transactional(readOnly = true)
    public NetMovementDetailResponse getNetMovementDetails(Long baseId, Long equipmentTypeId,
                                                           LocalDate startDate, LocalDate endDate,
                                                           UserPrincipal currentUser) {
        Long targetBaseId = baseId;
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole())) {
            targetBaseId = currentUser.getBaseId();
        }

        NetMovementDetailResponse response = new NetMovementDetailResponse();

        // 1. Purchases list
        List<Purchase> purchases = purchaseRepository.findWithFilters(targetBaseId, equipmentTypeId, null, startDate, endDate);
        List<NetMovementDetailResponse.PurchaseItem> purchaseItems = purchases.stream().map(p ->
            new NetMovementDetailResponse.PurchaseItem(
                    p.getId(),
                    p.getReferenceNumber(),
                    p.getBase().getName(),
                    p.getEquipment().getName(),
                    p.getEquipment().getEquipmentType().getName(),
                    p.getQuantity(),
                    p.getPurchaseDate(),
                    p.getSupplier()
            )
        ).collect(Collectors.toList());

        // 2. Transfers In list
        List<Transfer> transfersIn = new ArrayList<>();
        if (targetBaseId != null) {
            transfersIn = transferRepository.findTransfersIn(targetBaseId, equipmentTypeId, null, startDate, endDate);
        }
        List<NetMovementDetailResponse.TransferItem> transferInItems = transfersIn.stream().map(t ->
            new NetMovementDetailResponse.TransferItem(
                    t.getId(),
                    t.getReferenceNumber(),
                    t.getFromBase().getName(),
                    t.getToBase().getName(),
                    t.getEquipment().getName(),
                    t.getEquipment().getEquipmentType().getName(),
                    t.getQuantity(),
                    t.getTransferDate(),
                    t.getStatus()
            )
        ).collect(Collectors.toList());

        // 3. Transfers Out list
        List<Transfer> transfersOut = new ArrayList<>();
        if (targetBaseId != null) {
            transfersOut = transferRepository.findTransfersOut(targetBaseId, equipmentTypeId, null, startDate, endDate);
        }
        List<NetMovementDetailResponse.TransferItem> transferOutItems = transfersOut.stream().map(t ->
            new NetMovementDetailResponse.TransferItem(
                    t.getId(),
                    t.getReferenceNumber(),
                    t.getFromBase().getName(),
                    t.getToBase().getName(),
                    t.getEquipment().getName(),
                    t.getEquipment().getEquipmentType().getName(),
                    t.getQuantity(),
                    t.getTransferDate(),
                    t.getStatus()
            )
        ).collect(Collectors.toList());

        long totalP = purchaseItems.stream().mapToLong(p -> p.getQuantity()).sum();
        long totalIn = transferInItems.stream().mapToLong(t -> t.getQuantity()).sum();
        long totalOut = transferOutItems.stream().mapToLong(t -> t.getQuantity()).sum();

        response.setPurchases(purchaseItems);
        response.setTransfersIn(transferInItems);
        response.setTransfersOut(transferOutItems);
        response.setTotalPurchases(totalP);
        response.setTotalTransfersIn(totalIn);
        response.setTotalTransfersOut(totalOut);
        response.setNetMovement(totalP + totalIn - totalOut);

        return response;
    }
}
