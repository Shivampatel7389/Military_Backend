package com.military.mams.dto.response;

import java.time.LocalDate;
import java.util.List;

public class NetMovementDetailResponse {

    private long totalPurchases;
    private long totalTransfersIn;
    private long totalTransfersOut;
    private long netMovement;

    private List<PurchaseItem> purchases;
    private List<TransferItem> transfersIn;
    private List<TransferItem> transfersOut;

    public static class PurchaseItem {
        private Long id;
        private String referenceNumber;
        private String baseName;
        private String equipmentName;
        private String equipmentType;
        private Integer quantity;
        private LocalDate purchaseDate;
        private String supplier;

        public PurchaseItem() {}

        public PurchaseItem(Long id, String referenceNumber, String baseName, String equipmentName,
                            String equipmentType, Integer quantity, LocalDate purchaseDate, String supplier) {
            this.id = id;
            this.referenceNumber = referenceNumber;
            this.baseName = baseName;
            this.equipmentName = equipmentName;
            this.equipmentType = equipmentType;
            this.quantity = quantity;
            this.purchaseDate = purchaseDate;
            this.supplier = supplier;
        }

        public Long getId() { return id; }
        public String getReferenceNumber() { return referenceNumber; }
        public String getBaseName() { return baseName; }
        public String getEquipmentName() { return equipmentName; }
        public String getEquipmentType() { return equipmentType; }
        public Integer getQuantity() { return quantity; }
        public LocalDate getPurchaseDate() { return purchaseDate; }
        public String getSupplier() { return supplier; }
    }

    public static class TransferItem {
        private Long id;
        private String referenceNumber;
        private String fromBaseName;
        private String toBaseName;
        private String equipmentName;
        private String equipmentType;
        private Integer quantity;
        private LocalDate transferDate;
        private String status;

        public TransferItem() {}

        public TransferItem(Long id, String referenceNumber, String fromBaseName, String toBaseName,
                            String equipmentName, String equipmentType, Integer quantity,
                            LocalDate transferDate, String status) {
            this.id = id;
            this.referenceNumber = referenceNumber;
            this.fromBaseName = fromBaseName;
            this.toBaseName = toBaseName;
            this.equipmentName = equipmentName;
            this.equipmentType = equipmentType;
            this.quantity = quantity;
            this.transferDate = transferDate;
            this.status = status;
        }

        public Long getId() { return id; }
        public String getReferenceNumber() { return referenceNumber; }
        public String getFromBaseName() { return fromBaseName; }
        public String getToBaseName() { return toBaseName; }
        public String getEquipmentName() { return equipmentName; }
        public String getEquipmentType() { return equipmentType; }
        public Integer getQuantity() { return quantity; }
        public LocalDate getTransferDate() { return transferDate; }
        public String getStatus() { return status; }
    }

    public NetMovementDetailResponse() {}

    public long getTotalPurchases() { return totalPurchases; }
    public void setTotalPurchases(long totalPurchases) { this.totalPurchases = totalPurchases; }

    public long getTotalTransfersIn() { return totalTransfersIn; }
    public void setTotalTransfersIn(long totalTransfersIn) { this.totalTransfersIn = totalTransfersIn; }

    public long getTotalTransfersOut() { return totalTransfersOut; }
    public void setTotalTransfersOut(long totalTransfersOut) { this.totalTransfersOut = totalTransfersOut; }

    public long getNetMovement() { return netMovement; }
    public void setNetMovement(long netMovement) { this.netMovement = netMovement; }

    public List<PurchaseItem> getPurchases() { return purchases; }
    public void setPurchases(List<PurchaseItem> purchases) { this.purchases = purchases; }

    public List<TransferItem> getTransfersIn() { return transfersIn; }
    public void setTransfersIn(List<TransferItem> transfersIn) { this.transfersIn = transfersIn; }

    public List<TransferItem> getTransfersOut() { return transfersOut; }
    public void setTransfersOut(List<TransferItem> transfersOut) { this.transfersOut = transfersOut; }
}
