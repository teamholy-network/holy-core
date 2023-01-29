package de.teamholy.core.api.entities.perkplayer;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.util.UUID;

public class PerkPlayerService extends AbstractService<PerkPlayerProfile, UUID, PerkPlayerRepository> {

    public PerkPlayerService(CoreAPI coreAPI) {
        super(coreAPI, PerkPlayerRepository.class, false);
    }
}
