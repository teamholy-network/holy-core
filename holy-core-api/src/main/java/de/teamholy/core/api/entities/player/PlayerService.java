package de.teamholy.core.api.entities.player;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.util.UUID;

public class PlayerService extends AbstractService<PlayerProfile, UUID, PlayerRepository> {

    public PlayerService(CoreAPI coreAPI) {
        super(coreAPI, PlayerRepository.class, false);
    }
}
