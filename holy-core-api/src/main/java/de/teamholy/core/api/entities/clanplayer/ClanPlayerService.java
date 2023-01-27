package de.teamholy.core.api.entities.clanplayer;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.time.Duration;
import java.util.UUID;

public class ClanPlayerService extends AbstractService<ClanPlayerProfile, UUID, ClanPlayerRepository> {

    public ClanPlayerService(CoreAPI coreAPI) {
        super(coreAPI, ClanPlayerRepository.class, Duration.ofMinutes(15), false);
    }
}
