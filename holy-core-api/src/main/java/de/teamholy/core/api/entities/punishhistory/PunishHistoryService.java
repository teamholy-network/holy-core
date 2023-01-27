package de.teamholy.core.api.entities.punishhistory;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.time.Duration;
import java.util.UUID;

public class PunishHistoryService extends AbstractService<PunishHistoryProfile, UUID, PunishHistoryRepository> {

    public PunishHistoryService(CoreAPI coreAPI) {
        super(coreAPI, PunishHistoryRepository.class,  false);
    }
}
