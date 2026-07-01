import { useCallback } from "react";
import apiClient from "../services/apiClient";

// withAuditLog.js — get username from JWT payload
function getUsernameFromToken() {
    const token = localStorage.getItem("jwtToken");
    if (!token) return "unknown";
    try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        return payload.sub ?? "unknown";   // "sub" = username in your JwtService
    } catch {
        return "unknown";
    }
}


// HOC — Higher Order Component
// Takes any component and returns it wrapped with audit logging

function withAuditLog(WrappedComponent) {

    // The wrapper component
    function AuditLoggedComponent(props) {

        // logAction is passed DOWN to the wrapped component as a prop
        // The component calls it whenever a user action happens
        const logAction = useCallback(async ({
            action,        // "RESOLVE", "BULK_RESOLVE", "FILTER", "LOGIN"
            entityType,    // "RECON_BREAK", "TRADE"
            entityId,      // the breakId or tradeId
            oldValue,      // optional — state before action
            newValue       // optional — state after action
        }) => {

            const entry = {
                action,
                entityType,
                entityId:    String(entityId),
                oldValue:    oldValue ? JSON.stringify(oldValue) : null,
                newValue:    newValue ? JSON.stringify(newValue) : null,
                performedBy: getUsernameFromToken(),
                timestamp:   new Date().toISOString()
            };

            // Console log for workshop demo
            console.log(
                `[AUDIT] ${entry.timestamp} | ${entry.performedBy} | ` +
                `${entry.action} | ${entry.entityType} #${entry.entityId}`
            );

            // POST to backend — fire and forget
            // Don't await — don't block the UI for audit logging
            apiClient
                .post("/audit", entry)
                .catch(err =>
                    console.error("Audit log failed:", err)
                );

        }, []);

        // Render the wrapped component, pass logAction as a prop
        return (
            <WrappedComponent
                {...props}
                logAction={logAction}
            />
        );
    }

    // displayName for React DevTools
    // Shows as "withAuditLog(BreaksPage)" not just "AuditLoggedComponent"
    AuditLoggedComponent.displayName =
        `withAuditLog(${WrappedComponent.displayName || WrappedComponent.name})`;

    return AuditLoggedComponent;
}

export default withAuditLog;