package de.teamholy.core.api.entities.ban;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.util.UUID;

public class BanService extends AbstractService<BanProfile, UUID, BanRepository> {

    public BanService(CoreAPI coreAPI) {
        super(coreAPI, BanRepository.class, false);
    }
}
