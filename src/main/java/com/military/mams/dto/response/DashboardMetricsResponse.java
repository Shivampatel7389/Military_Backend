package com.military.mams.dto.response;

import java.util.List;

public class DashboardMetricsResponse {

    private long openingBalance;
    private long purchases;
    private long transfersIn;
    private long transfersOut;
    private long netMovement;
    private long assigned;
    private long expended;
    private long closingBalance;
    private long availableStock;

    private List<CategorySummary> categoryBreakdown;

    public DashboardMetricsResponse() {}

    public static class CategorySummary {
        private String category;
        private long openingBalance;
        private long purchases;
        private long transfersIn;
        private long transfersOut;
        private long netMovement;
        private long assigned;
        private long expended;
        private long closingBalance;
        private long availableStock;

        public CategorySummary() {}

        public CategorySummary(String category, long openingBalance, long purchases,
                               long transfersIn, long transfersOut, long netMovement,
                               long assigned, long expended, long closingBalance, long availableStock) {
            this.category = category;
            this.openingBalance = openingBalance;
            this.purchases = purchases;
            this.transfersIn = transfersIn;
            this.transfersOut = transfersOut;
            this.netMovement = netMovement;
            this.assigned = assigned;
            this.expended = expended;
            this.closingBalance = closingBalance;
            this.availableStock = availableStock;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public long getOpeningBalance() {
            return openingBalance;
        }

        public void setOpeningBalance(long openingBalance) {
            this.openingBalance = openingBalance;
        }

        public long getPurchases() {
            return purchases;
        }

        public void setPurchases(long purchases) {
            this.purchases = purchases;
        }

        public long getTransfersIn() {
            return transfersIn;
        }

        public void setTransfersIn(long transfersIn) {
            this.transfersIn = transfersIn;
        }

        public long getTransfersOut() {
            return transfersOut;
        }

        public void setTransfersOut(long transfersOut) {
            this.transfersOut = transfersOut;
        }

        public long getNetMovement() {
            return netMovement;
        }

        public void setNetMovement(long netMovement) {
            this.netMovement = netMovement;
        }

        public long getAssigned() {
            return assigned;
        }

        public void setAssigned(long assigned) {
            this.assigned = assigned;
        }

        public long getExpended() {
            return expended;
        }

        public void setExpended(long expended) {
            this.expended = expended;
        }

        public long getClosingBalance() {
            return closingBalance;
        }

        public void setClosingBalance(long closingBalance) {
            this.closingBalance = closingBalance;
        }

        public long getAvailableStock() {
            return availableStock;
        }

        public void setAvailableStock(long availableStock) {
            this.availableStock = availableStock;
        }
    }

    public long getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(long openingBalance) {
        this.openingBalance = openingBalance;
    }

    public long getPurchases() {
        return purchases;
    }

    public void setPurchases(long purchases) {
        this.purchases = purchases;
    }

    public long getTransfersIn() {
        return transfersIn;
    }

    public void setTransfersIn(long transfersIn) {
        this.transfersIn = transfersIn;
    }

    public long getTransfersOut() {
        return transfersOut;
    }

    public void setTransfersOut(long transfersOut) {
        this.transfersOut = transfersOut;
    }

    public long getNetMovement() {
        return netMovement;
    }

    public void setNetMovement(long netMovement) {
        this.netMovement = netMovement;
    }

    public long getAssigned() {
        return assigned;
    }

    public void setAssigned(long assigned) {
        this.assigned = assigned;
    }

    public long getExpended() {
        return expended;
    }

    public void setExpended(long expended) {
        this.expended = expended;
    }

    public long getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(long closingBalance) {
        this.closingBalance = closingBalance;
    }

    public long getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(long availableStock) {
        this.availableStock = availableStock;
    }

    public List<CategorySummary> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(List<CategorySummary> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }
}
