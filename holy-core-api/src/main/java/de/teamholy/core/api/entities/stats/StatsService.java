package de.teamholy.core.api.entities.stats;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.util.UUID;

public class StatsService extends AbstractService<StatsProfile, UUID, StatsRepository> {

    public StatsService(CoreAPI coreAPI) {
        super(coreAPI, StatsRepository.class, false);
    }
}
