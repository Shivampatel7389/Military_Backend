package com.military.mams.controller;

import com.military.mams.dto.request.PersonnelRequest;
import com.military.mams.dto.response.ApiResponse;
import com.military.mams.entity.Personnel;
import com.military.mams.security.UserPrincipal;
import com.military.mams.service.PersonnelService;
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
@RequestMapping("/api/personnel")
public class PersonnelController {

    @Autowired
    private PersonnelService personnelService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Personnel>>> getPersonnel(
            @RequestParam(required = false) Long baseId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<Personnel> personnelList = personnelService.getPersonnel(baseId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(personnelList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Personnel>> getPersonnelById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Personnel personnel = personnelService.getPersonnelById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(personnel));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ApiResponse<Personnel>> createPersonnel(
            @Valid @RequestBody PersonnelRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getRemoteAddr();
        Personnel personnel = personnelService.createPersonnel(request, currentUser, ipAddress);
        return new ResponseEntity<>(ApiResponse.success("Personnel enrolled successfully", personnel), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ApiResponse<Personnel>> updatePersonnel(
            @PathVariable Long id,
            @Valid @RequestBody PersonnelRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser,
            HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getRemoteAddr();
        Personnel personnel = personnelService.updatePersonnel(id, request, currentUser, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Personnel updated", personnel));
    }
}
