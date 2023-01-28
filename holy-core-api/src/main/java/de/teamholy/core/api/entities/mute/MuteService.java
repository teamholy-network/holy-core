package de.teamholy.core.api.entities.mute;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.util.UUID;

public class MuteService extends AbstractService<MuteProfile, UUID, MuteRepository> {

    public MuteService(CoreAPI coreAPI) {
        super(coreAPI, MuteRepository.class, false);
    }
}
