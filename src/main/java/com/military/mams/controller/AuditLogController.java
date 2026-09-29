package com.military.mams.controller;

import com.military.mams.dto.response.ApiResponse;
import com.military.mams.entity.AuditLog;
import com.military.mams.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
public class AuditLogController {

    @Autowired
    private AuditService auditService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        // Normalize empty/blank strings to null
        String cleanUser = (username != null && !username.trim().isEmpty()) ? username.trim() : null;
        String cleanAction = (action != null && !action.trim().isEmpty()) ? action.trim() : null;
        String cleanEntity = (entityType != null && !entityType.trim().isEmpty()) ? entityType.trim() : null;

        LocalDateTime startDateTime = parseDateTime(startDate, false);
        LocalDateTime endDateTime = parseDateTime(endDate, true);

        List<AuditLog> logs = auditService.getAuditLogs(cleanUser, cleanAction, cleanEntity, startDateTime, endDateTime);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    private LocalDateTime parseDateTime(String dateStr, boolean isEndOfDay) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            String trimmed = dateStr.trim();
            if (trimmed.contains("T")) {
                return LocalDateTime.parse(trimmed);
            }
            LocalDate localDate = LocalDate.parse(trimmed);
            return isEndOfDay ? localDate.atTime(LocalTime.MAX) : localDate.atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }
}
