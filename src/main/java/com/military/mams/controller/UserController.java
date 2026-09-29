package com.military.mams.controller;

import com.military.mams.dto.request.UserCreateRequest;
import com.military.mams.dto.response.ApiResponse;
import com.military.mams.entity.User;
import com.military.mams.security.UserPrincipal;
import com.military.mams.service.UserService;
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
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody UserCreateRequest request,
                                                        @AuthenticationPrincipal UserPrincipal currentUser,
                                                        HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getRemoteAddr();
        User created = userService.createUser(request, currentUser, ipAddress);
        return new ResponseEntity<>(ApiResponse.success("User account created successfully", created), HttpStatus.CREATED);
    }
}
