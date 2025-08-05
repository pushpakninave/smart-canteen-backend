package com.smartcanteen.utility.enums;

public enum ERole {
    STUDENT,
    CANTEEN_MANAGER,
    ADMIN,
    NGO;

    public static ERole fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty.");
        }
        String normalizedName = name.trim().toUpperCase();

        for (ERole role : ERole.values()) {
            if (role.name().equals(normalizedName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant " + ERole.class.getCanonicalName() + " for name: " + name);
    }
}