package de.teamholy.core.api;

import de.teamholy.core.api.entities.friend.entry.FriendEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
@AllArgsConstructor
public class CachedFriendEntry {

    private long expiredAt;

    private final FriendEntry friendEntry;

}
