package de.teamholy.core.api.entities.friend;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.util.UUID;

public class FriendService extends AbstractService<FriendProfile, UUID, FriendRepository> {

    public FriendService(CoreAPI coreAPI) {
        super(coreAPI, FriendRepository.class, false);
    }
}
