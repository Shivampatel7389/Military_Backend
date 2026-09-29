package com.military.mams.controller;

import com.military.mams.dto.request.BaseRequest;
import com.military.mams.dto.response.ApiResponse;
import com.military.mams.entity.Base;
import com.military.mams.security.UserPrincipal;
import com.military.mams.service.BaseService;
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
@RequestMapping("/api/bases")
public class BaseController {

    @Autowired
    private BaseService baseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Base>>> getAllBases(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<Base> bases = baseService.getAllBases(currentUser);
        return ResponseEntity.ok(ApiResponse.success(bases));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Base>> getBaseById(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserPrincipal currentUser) {
        Base base = baseService.getBaseById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(base));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Base>> createBase(@Valid @RequestBody BaseRequest request,
                                                        @AuthenticationPrincipal UserPrincipal currentUser,
                                                        HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getRemoteAddr();
        Base base = baseService.createBase(request, currentUser, ipAddress);
        return new ResponseEntity<>(ApiResponse.success("Base installation created successfully", base), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Base>> updateBase(@PathVariable Long id,
                                                        @Valid @RequestBody BaseRequest request,
                                                        @AuthenticationPrincipal UserPrincipal currentUser,
                                                        HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getRemoteAddr();
        Base base = baseService.updateBase(id, request, currentUser, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Base installation updated", base));
    }
}
