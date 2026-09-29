package com.military.mams.service;

import com.military.mams.dto.request.UserCreateRequest;
import com.military.mams.entity.Base;
import com.military.mams.entity.Role;
import com.military.mams.entity.RoleName;
import com.military.mams.entity.User;
import com.military.mams.exception.BadRequestException;
import com.military.mams.exception.ResourceNotFoundException;
import com.military.mams.repository.BaseRepository;
import com.military.mams.repository.RoleRepository;
import com.military.mams.repository.UserRepository;
import com.military.mams.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditService auditService;

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found with id: " + id));
    }

    @Transactional
    public User createUser(UserCreateRequest request, UserPrincipal currentUser, String ipAddress) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("User with email '" + request.getEmail() + "' already registered.");
        }

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(request.getRole().startsWith("ROLE_") ? request.getRole() : "ROLE_" + request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role specified. Permitted: ADMIN, BASE_COMMANDER, LOGISTICS_OFFICER");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        Base base = null;
        if (roleName == RoleName.ROLE_BASE_COMMANDER) {
            if (request.getBaseId() == null) {
                throw new BadRequestException("Base Commander must be assigned to an installation base.");
            }
            base = baseRepository.findById(request.getBaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Base installation not found with id: " + request.getBaseId()));
        } else if (request.getBaseId() != null) {
            base = baseRepository.findById(request.getBaseId()).orElse(null);
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                role,
                base
        );

        User saved = userRepository.save(user);

        auditService.logAction(
                currentUser != null ? currentUser.getUsername() : "SYSTEM",
                currentUser != null ? currentUser.getRole() : "ROLE_ADMIN",
                "USER_CREATED",
                "User",
                saved.getId(),
                null,
                "Provisioned user account: " + saved.getEmail() + " (" + roleName.name() + ")" + (base != null ? " at " + base.getName() : ""),
                ipAddress
        );

        return saved;
    }
}
