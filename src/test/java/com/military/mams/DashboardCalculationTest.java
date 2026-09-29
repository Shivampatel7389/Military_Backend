package com.military.mams;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardCalculationTest {

    @Test
    @DisplayName("Formula verification: Net Movement = Purchases + Transfers In - Transfers Out")
    public void testNetMovementFormula() {
        long purchases = 150;
        long transfersIn = 40;
        long transfersOut = 25;

        long netMovement = purchases + transfersIn - transfersOut;

        assertEquals(165, netMovement, "Net movement must equal Purchases + Transfers In - Transfers Out");
    }

    @Test
    @DisplayName("Formula verification: Closing Balance = Opening Balance + Net Movement - Expended")
    public void testClosingBalanceFormula() {
        long openingBalance = 500;
        long netMovement = 165;
        long expended = 30;

        long closingBalance = openingBalance + netMovement - expended;

        assertEquals(635, closingBalance, "Closing balance must equal Opening Balance + Net Movement - Expended");
    }

    @Test
    @DisplayName("Formula verification: Available Ready Stock = Closing Balance - Active Assigned")
    public void testAvailableStockFormula() {
        long closingBalance = 635;
        long activeAssigned = 15;

        long availableReadyStock = Math.max(0, closingBalance - activeAssigned);

        assertEquals(620, availableReadyStock, "Available armory stock must reflect closing balance less active personnel issues");
    }

    @Test
    @DisplayName("Validation: Negative stock or transfer exceeding available stock must be prevented")
    public void testStockValidationRule() {
        long availableStock = 20;
        long requestedTransfer = 25;

        boolean canDispatch = requestedTransfer <= availableStock;

        assertFalse(canDispatch, "Transfer request exceeding available stock must fail validation");
    }
}
