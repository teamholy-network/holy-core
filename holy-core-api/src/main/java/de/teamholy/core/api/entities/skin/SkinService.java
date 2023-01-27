package de.teamholy.core.api.entities.skin;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.time.Duration;
import java.util.UUID;

public class SkinService extends AbstractService<SkinProfile, UUID, SkinRepository> {

    public SkinService(CoreAPI coreAPI) {
        super(coreAPI, SkinRepository.class,  false);
    }
}
