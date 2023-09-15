package de.teamholy.core.api.entities.stats;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.util.UUID;

public class StatsProfileService extends AbstractService<StatsProfile, UUID, StatsProfileRepository> {
    public StatsProfileService(CoreAPI coreAPI) {
        super(coreAPI, StatsProfileRepository.class, false);
    }
}
