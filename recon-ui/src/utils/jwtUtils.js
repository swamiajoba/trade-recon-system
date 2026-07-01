// src/utils/jwtUtils.js

export function getRoleFromToken(token) {

    if (!token) return null;

    // JWT = header.payload.signature
    // payload is base64 encoded — decode it
    const payload = token.split(".")[1];

    const decoded = JSON.parse(atob(payload));

    // Your JwtService puts roles as ["ROLE_ADMIN"] or ["ROLE_OPS"]
    const roles = decoded.roles || [];

    if (roles.includes("ROLE_ADMIN")) return "ADMIN";
    if (roles.includes("ROLE_OPS"))   return "OPS";

    return null;
}