package com.military.mams.controller;

import com.military.mams.dto.request.LoginRequest;
import com.military.mams.dto.response.ApiResponse;
import com.military.mams.dto.response.AuthResponse;
import com.military.mams.security.UserPrincipal;
import com.military.mams.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest,
                                                           HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        AuthResponse authResponse = authService.authenticateUser(loginRequest, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", authResponse));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        AuthResponse response = new AuthResponse(
                null,
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getFullName(),
                currentUser.getRole(),
                currentUser.getBaseId(),
                currentUser.getBaseName(),
                currentUser.getBaseCode()
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
