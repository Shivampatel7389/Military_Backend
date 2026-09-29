package com.military.mams.service;

import com.military.mams.dto.request.BaseRequest;
import com.military.mams.entity.Base;
import com.military.mams.exception.BadRequestException;
import com.military.mams.exception.ResourceNotFoundException;
import com.military.mams.repository.BaseRepository;
import com.military.mams.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class BaseService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AuditService auditService;

    @Transactional(readOnly = true)
    public List<Base> getAllBases(UserPrincipal currentUser) {
        // Base Commander sees only their assigned base
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole()) && currentUser.getBaseId() != null) {
            return baseRepository.findById(currentUser.getBaseId())
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        }
        return baseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Base getBaseById(Long id, UserPrincipal currentUser) {
        if ("ROLE_BASE_COMMANDER".equals(currentUser.getRole()) && !currentUser.getBaseId().equals(id)) {
            throw new BadRequestException("Clearance restriction: Access denied to other base records.");
        }
        return baseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base installation not found with id: " + id));
    }

    @Transactional
    public Base createBase(BaseRequest request, UserPrincipal currentUser, String ipAddress) {
        if (baseRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Base with code '" + request.getCode() + "' already exists.");
        }

        Base base = new Base(request.getName(), request.getCode(), request.getLocation());
        if (request.getStatus() != null) {
            base.setStatus(request.getStatus());
        }
        Base saved = baseRepository.save(base);

        auditService.logAction(
                currentUser.getUsername(),
                currentUser.getRole(),
                "BASE_CREATED",
                "Base",
                saved.getId(),
                null,
                "Created base: " + saved.getName() + " (" + saved.getCode() + ")",
                ipAddress
        );

        return saved;
    }

    @Transactional
    public Base updateBase(Long id, BaseRequest request, UserPrincipal currentUser, String ipAddress) {
        Base base = baseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with id: " + id));

        String oldVal = "Name: " + base.getName() + ", Code: " + base.getCode() + ", Loc: " + base.getLocation();

        base.setName(request.getName());
        base.setLocation(request.getLocation());
        if (request.getStatus() != null) {
            base.setStatus(request.getStatus());
        }

        Base saved = baseRepository.save(base);

        auditService.logAction(
                currentUser.getUsername(),
                currentUser.getRole(),
                "BASE_UPDATED",
                "Base",
                saved.getId(),
                oldVal,
                "Updated base: " + saved.getName(),
                ipAddress
        );

        return saved;
    }
}
