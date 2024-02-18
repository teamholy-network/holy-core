package de.teamholy.core.api.entities.friend.entry;

import de.teamholy.core.api.utility.PlayerRank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
public class Friend {
    private String value, signature;
    private UUID uuid;
    private boolean isOnline;
    private long lastJoin;
    private String name;
    private PlayerRank playerRank;
    private String currentServer;
}
