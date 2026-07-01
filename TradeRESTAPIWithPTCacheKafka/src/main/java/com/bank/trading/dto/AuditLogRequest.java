package com.bank.trading.dto;

import java.time.Instant;

public class AuditLogRequest {

    private String  action;
    private String  entityType;
    private String  entityId;
    private String  oldValue;
    private String  newValue;
    private String  performedBy;
    private Instant timestamp;

    // Getters + setters
    public String  getAction()      { return action; }
    public String  getEntityType()  { return entityType; }
    public String  getEntityId()    { return entityId; }
    public String  getOldValue()    { return oldValue; }
    public String  getNewValue()    { return newValue; }
    public String  getPerformedBy() { return performedBy; }
    public Instant getTimestamp()   { return timestamp; }

    public void setAction(String action)           { this.action = action; }
    public void setEntityType(String entityType)   { this.entityType = entityType; }
    public void setEntityId(String entityId)       { this.entityId = entityId; }
    public void setOldValue(String oldValue)       { this.oldValue = oldValue; }
    public void setNewValue(String newValue)       { this.newValue = newValue; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    public void setTimestamp(Instant timestamp)    { this.timestamp = timestamp; }
}