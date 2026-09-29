package com.military.mams.service;

import com.military.mams.dto.request.LoginRequest;
import com.military.mams.dto.response.AuthResponse;
import com.military.mams.security.JwtTokenProvider;
import com.military.mams.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuditService auditService;

    public AuthResponse authenticateUser(LoginRequest loginRequest, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail().trim(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        auditService.logAction(
                userPrincipal.getUsername(),
                userPrincipal.getRole(),
                "USER_LOGIN",
                "User",
                userPrincipal.getId(),
                null,
                "User successfully authenticated into MAMS command console",
                ipAddress
        );

        return new AuthResponse(
                jwt,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getFullName(),
                userPrincipal.getRole(),
                userPrincipal.getBaseId(),
                userPrincipal.getBaseName(),
                userPrincipal.getBaseCode()
        );
    }
}
