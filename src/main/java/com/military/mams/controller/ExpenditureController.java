package com.military.mams.controller;

import com.military.mams.dto.request.ExpenditureRequest;
import com.military.mams.dto.response.ApiResponse;
import com.military.mams.entity.Expenditure;
import com.military.mams.security.UserPrincipal;
import com.military.mams.service.ExpenditureService;
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
@RequestMapping("/api/expenditures")
public class ExpenditureController {

    @Autowired
    private ExpenditureService expenditureService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ApiResponse<List<Expenditure>>> getExpenditures(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<Expenditure> expenditures = expenditureService.getExpenditures(baseId, equipmentTypeId, equipmentId, reason, startDate, endDate, currentUser);
        return ResponseEntity.ok(ApiResponse.success(expenditures));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ApiResponse<Expenditure>> recordExpenditure(@Valid @RequestBody ExpenditureRequest request,
                                                                      @AuthenticationPrincipal UserPrincipal currentUser,
                                                                      HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getRemoteAddr();
        Expenditure expenditure = expenditureService.recordExpenditure(request, currentUser, ipAddress);
        return new ResponseEntity<>(ApiResponse.success("Expenditure recorded and armory stock updated", expenditure), HttpStatus.CREATED);
    }
}
