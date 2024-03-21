package de.teamholy.core.api.utility;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/* copyright by Yassino */
@UtilityClass
public class UUIDUtility {

    public boolean isCracked(UUID uuid, String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8)).equals(uuid);
    }

    public boolean isPremium(UUID uuid, String username) {
        return !isCracked(uuid, username);
    }

    public UUIDType getUUIDType(UUID uuid, String username) {
        if (isCracked(uuid, username)) {
            return UUIDType.CRACKED;
        }
        return UUIDType.PREMIUM;
    }

    public enum UUIDType {
        PREMIUM, CRACKED
    }

}
