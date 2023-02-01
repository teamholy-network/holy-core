package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StaffManager {

    CoreAPI coreAPI;

    public boolean canNotify(UUID uuid) {
        return coreAPI.getStaffService().getRedisCache().get(uuid).isNotify();
    }

    public boolean exists(UUID uuid) {
        return coreAPI.getStaffService().getEntity(uuid, () -> coreAPI.getStaffService().getRepository().findFirstById(uuid)).isNotify();
    }
}
