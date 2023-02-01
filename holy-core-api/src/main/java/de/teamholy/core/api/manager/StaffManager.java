package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;

import java.util.UUID;

/* copyright by Yassino */
public class StaffManager {

    private final CoreAPI coreAPI;


    public StaffManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
    }

    public boolean canNotify(UUID uuid) {
        return coreAPI.getStaffService().getRedisCache().get(uuid).isNotify();
    }

    public boolean exists(UUID uuid) {
        return coreAPI.getStaffService().getEntity(uuid, () -> coreAPI.getStaffService().getRepository().findFirstById(uuid)).isNotify();
    }


}
