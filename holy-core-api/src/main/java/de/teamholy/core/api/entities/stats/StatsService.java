package de.teamholy.core.api.entities.stats;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;
import de.teamholy.core.api.entities.punish.PunishProfile;
import de.teamholy.core.api.entities.punish.PunishRepository;

import java.time.Duration;
import java.util.UUID;

public class StatsService extends AbstractService<StatsProfile, UUID, StatsRepository> {

    public StatsService(CoreAPI coreAPI) {
        super(coreAPI, StatsRepository.class, Duration.ofMinutes(15), false);
    }
}
