package com.military.mams.controller;

import com.military.mams.dto.request.EquipmentRequest;
import com.military.mams.dto.response.ApiResponse;
import com.military.mams.entity.Equipment;
import com.military.mams.entity.EquipmentType;
import com.military.mams.security.UserPrincipal;
import com.military.mams.service.EquipmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Equipment>>> getAllEquipment(
            @RequestParam(required = false) Long equipmentTypeId) {
        List<Equipment> equipmentList = equipmentService.getAllEquipment(equipmentTypeId);
        return ResponseEntity.ok(ApiResponse.success(equipmentList));
    }

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<EquipmentType>>> getEquipmentTypes() {
        List<EquipmentType> types = equipmentService.getAllEquipmentTypes();
        return ResponseEntity.ok(ApiResponse.success(types));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Equipment>> getEquipmentById(@PathVariable Long id) {
        Equipment equipment = equipmentService.getEquipmentById(id);
        return ResponseEntity.ok(ApiResponse.success(equipment));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<Equipment>> createEquipment(@Valid @RequestBody EquipmentRequest request,
                                                                  @AuthenticationPrincipal UserPrincipal currentUser,
                                                                  HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getRemoteAddr();
        Equipment equipment = equipmentService.createEquipment(request, currentUser, ipAddress);
        return new ResponseEntity<>(ApiResponse.success("Equipment registered successfully", equipment), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Equipment>> updateEquipment(@PathVariable Long id,
                                                                  @Valid @RequestBody EquipmentRequest request,
                                                                  @AuthenticationPrincipal UserPrincipal currentUser,
                                                                  HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getRemoteAddr();
        Equipment equipment = equipmentService.updateEquipment(id, request, currentUser, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Equipment updated", equipment));
    }
}
