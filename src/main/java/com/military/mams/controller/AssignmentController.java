package com.military.mams.controller;

import com.military.mams.dto.request.AssignmentRequest;
import com.military.mams.dto.response.ApiResponse;
import com.military.mams.entity.Assignment;
import com.military.mams.security.UserPrincipal;
import com.military.mams.service.AssignmentService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ApiResponse<List<Assignment>>> getAssignments(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) Long personnelId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<Assignment> assignments = assignmentService.getAssignments(baseId, equipmentTypeId, equipmentId, personnelId, status, startDate, endDate, currentUser);
        return ResponseEntity.ok(ApiResponse.success(assignments));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ApiResponse<Assignment>> createAssignment(@Valid @RequestBody AssignmentRequest request,
                                                                    @AuthenticationPrincipal UserPrincipal currentUser,
                                                                    HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getRemoteAddr();
        Assignment assignment = assignmentService.createAssignment(request, currentUser, ipAddress);
        return new ResponseEntity<>(ApiResponse.success("Asset issued to personnel successfully", assignment), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}/return", method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ApiResponse<Assignment>> returnAssignment(@PathVariable Long id,
                                                                    @RequestBody(required = false) Map<String, String> body,
                                                                    @AuthenticationPrincipal UserPrincipal currentUser,
                                                                    HttpServletRequest servletRequest) {
        String remarks = body != null ? body.get("remarks") : null;
        String ipAddress = servletRequest.getRemoteAddr();
        Assignment assignment = assignmentService.returnAssignment(id, remarks, currentUser, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Asset returned and checked back into armory ready stock", assignment));
    }
}
