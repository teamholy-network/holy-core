package de.teamholy.core.ping;

import lombok.Getter;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
public enum HealthStatus {

    ONLINE,
    OFFLINE,
    SHUTDOWN,
    UNKNOWN;

    public static HealthStatus fromBoolean(boolean online) {
        return online ? ONLINE : OFFLINE;
    }

    public static HealthStatus fromString(String string) {
        if (string == null) return UNKNOWN;
        return switch (string.toLowerCase()) {
            case "online" -> ONLINE;
            case "offline" -> OFFLINE;
            case "shutdown" -> SHUTDOWN;
            default -> UNKNOWN;
        };
    }

    public boolean isOnline() {
        return this == ONLINE;
    }

    public boolean isOffline() {
        return this == OFFLINE;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    public String toString() {
        return name().toLowerCase();
    }

}
