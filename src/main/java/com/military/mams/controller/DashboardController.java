package com.military.mams.controller;

import com.military.mams.dto.response.ApiResponse;
import com.military.mams.dto.response.DashboardMetricsResponse;
import com.military.mams.dto.response.NetMovementDetailResponse;
import com.military.mams.security.UserPrincipal;
import com.military.mams.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardMetricsResponse>> getDashboard(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DashboardMetricsResponse metrics = dashboardService.getDashboardMetrics(baseId, equipmentTypeId, startDate, endDate, currentUser);
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    @GetMapping("/net-movement-details")
    public ResponseEntity<ApiResponse<NetMovementDetailResponse>> getNetMovementDetails(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        NetMovementDetailResponse details = dashboardService.getNetMovementDetails(baseId, equipmentTypeId, startDate, endDate, currentUser);
        return ResponseEntity.ok(ApiResponse.success(details));
    }
}
