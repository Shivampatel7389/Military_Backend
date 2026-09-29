package com.military.mams.service;

import com.military.mams.entity.AuditLog;
import com.military.mams.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional
    public void logAction(String username, String userRole, String action, String entityType,
                          Long entityId, String oldValue, String newValue, String ipAddress) {
        AuditLog log = new AuditLog(username, userRole, action, entityType, entityId, oldValue, newValue, ipAddress);
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogs(String username, String action, String entityType,
                                       LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findWithFilters(username, action, entityType, startDate, endDate);
    }
}
