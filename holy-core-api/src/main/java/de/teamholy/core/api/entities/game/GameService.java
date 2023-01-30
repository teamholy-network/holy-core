package de.teamholy.core.api.entities.game;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.util.UUID;

public class GameService extends AbstractService<GameProfile, UUID, GameRepository> {

    public GameService(CoreAPI coreAPI) {
        super(coreAPI, GameRepository.class, true);
    }
}
