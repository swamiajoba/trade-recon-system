package com.bank.trading.controller;

import com.bank.trading.domain.AuditLog;
import com.bank.trading.dto.AuditLogRequest;
import com.bank.trading.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // React HOC posts here
    @PostMapping
    public ResponseEntity<AuditLog> log(
            @RequestBody AuditLogRequest request) {

        return ResponseEntity.ok(
                auditLogService.save(request)
        );
    }

    // Optional — query audit trail for a specific break
    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<?> getAuditTrail(
            @PathVariable String entityType,
            @PathVariable String entityId) {

        return ResponseEntity.ok(
                auditLogService.findByEntity(entityType, entityId)
        );
    }
}
