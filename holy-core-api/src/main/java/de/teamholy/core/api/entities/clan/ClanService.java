package de.teamholy.core.api.entities.clan;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.util.UUID;

public class ClanService extends AbstractService<Clan, UUID, ClanRepository> {

    public ClanService(CoreAPI coreAPI) {
        super(coreAPI, ClanRepository.class, false);
    }
}
