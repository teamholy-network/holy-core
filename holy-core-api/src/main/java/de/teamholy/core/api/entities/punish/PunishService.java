package de.teamholy.core.api.entities.punish;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.player.PlayerRepository;

import java.time.Duration;
import java.util.UUID;

public class PunishService extends AbstractService<PunishProfile, UUID, PunishRepository> {

    public PunishService(CoreAPI coreAPI) {
        super(coreAPI, PunishRepository.class, false);
    }
}
