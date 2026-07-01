package com.bank.trading.service;
import com.bank.trading.domain.AuditLog;
import com.bank.trading.dto.AuditLogRequest;
import com.bank.trading.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public AuditLog save(AuditLogRequest request) {

        AuditLog log = new AuditLog();
        log.setAction(request.getAction());
        log.setEntityType(request.getEntityType());
        log.setEntityId(request.getEntityId());
        log.setOldValue(request.getOldValue());
        log.setNewValue(request.getNewValue());
        log.setPerformedBy(request.getPerformedBy());

        // Use frontend timestamp if provided, else server time
        log.setTimestamp(
                request.getTimestamp() != null
                        ? request.getTimestamp()
                        : Instant.now()
        );

        return repository.save(log);
    }

    public Object findByEntity(String entityType, String entityId) {
        return null;
    }
}