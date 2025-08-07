package com.smartcanteen.login.enity;

public enum ERole {
    ROLE_STUDENT, // <<< MODIFIED: Added ROLE_ prefix
    ROLE_CANTEEN_MANAGER, // <<< MODIFIED: Added ROLE_ prefix
    ROLE_ADMIN, // <<< MODIFIED: Added ROLE_ prefix
    ROLE_NGO; // <<< MODIFIED: Added ROLE_ prefix

    // The fromName method is updated to handle both prefixed and non-prefixed input strings
    public static ERole fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty.");
        }
        String normalizedName = name.trim().toUpperCase();

        // Try direct match (e.g., "ROLE_NGO" -> ERole.ROLE_NGO)
        for (ERole role : ERole.values()) {
            if (role.name().equals(normalizedName)) {
                return role;
            }
        }
        // Try match without "ROLE_" prefix (e.g., "NGO" -> ERole.ROLE_NGO) - useful for frontend registration
        if (!normalizedName.startsWith("ROLE_")) { // Only try if it doesn't already have ROLE_
            String prefixedName = "ROLE_" + normalizedName;
            for (ERole role : ERole.values()) {
                if (role.name().equals(prefixedName)) {
                    return role;
                }
            }
        }
        throw new IllegalArgumentException("No enum constant " + ERole.class.getCanonicalName() + " for name: " + name);
    }
}