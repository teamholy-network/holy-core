package de.teamholy.core.api.entities.staff;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

import java.util.UUID;

public class StaffService extends AbstractService<StaffProfile, UUID, StaffRepository> {

    public StaffService(CoreAPI coreAPI) {
        super(coreAPI, StaffRepository.class, false);
    }
}
