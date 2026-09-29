package com.military.mams.controller;

import com.military.mams.dto.request.PurchaseRequest;
import com.military.mams.dto.response.ApiResponse;
import com.military.mams.entity.Purchase;
import com.military.mams.security.UserPrincipal;
import com.military.mams.service.PurchaseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Purchase>>> getPurchases(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<Purchase> purchases = purchaseService.getPurchases(baseId, equipmentTypeId, equipmentId, startDate, endDate, currentUser);
        return ResponseEntity.ok(ApiResponse.success(purchases));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Purchase>> getPurchaseById(@PathVariable Long id,
                                                                 @AuthenticationPrincipal UserPrincipal currentUser) {
        Purchase purchase = purchaseService.getPurchaseById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(purchase));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<Purchase>> createPurchase(@Valid @RequestBody PurchaseRequest request,
                                                                @AuthenticationPrincipal UserPrincipal currentUser,
                                                                HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getRemoteAddr();
        Purchase purchase = purchaseService.createPurchase(request, currentUser, ipAddress);
        return new ResponseEntity<>(ApiResponse.success("Purchase order successfully recorded", purchase), HttpStatus.CREATED);
    }
}
