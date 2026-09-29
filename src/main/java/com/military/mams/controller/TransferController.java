package com.military.mams.controller;

import com.military.mams.dto.request.TransferRequest;
import com.military.mams.dto.response.ApiResponse;
import com.military.mams.entity.Transfer;
import com.military.mams.security.UserPrincipal;
import com.military.mams.service.TransferService;
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
@RequestMapping("/api/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Transfer>>> getTransfers(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long fromBaseId,
            @RequestParam(required = false) Long toBaseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<Transfer> transfers = transferService.getTransfers(baseId, fromBaseId, toBaseId, equipmentTypeId, equipmentId, startDate, endDate, currentUser);
        return ResponseEntity.ok(ApiResponse.success(transfers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transfer>> getTransferById(@PathVariable Long id,
                                                                 @AuthenticationPrincipal UserPrincipal currentUser) {
        Transfer transfer = transferService.getTransferById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(transfer));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<Transfer>> createTransfer(@Valid @RequestBody TransferRequest request,
                                                                @AuthenticationPrincipal UserPrincipal currentUser,
                                                                HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getRemoteAddr();
        Transfer transfer = transferService.createTransfer(request, currentUser, ipAddress);
        return new ResponseEntity<>(ApiResponse.success("Transfer order dispatched and verified", transfer), HttpStatus.CREATED);
    }
}
