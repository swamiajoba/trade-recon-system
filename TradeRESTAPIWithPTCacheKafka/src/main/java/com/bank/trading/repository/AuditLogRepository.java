package com.bank.trading.repository;

import com.bank.trading.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    // Find all actions for a specific break or trade
    List<AuditLog> findByEntityTypeAndEntityId(
            String entityType, String entityId);

    // Find all actions by a specific user
    List<AuditLog> findByPerformedByOrderByTimestampDesc(
            String performedBy);
}
