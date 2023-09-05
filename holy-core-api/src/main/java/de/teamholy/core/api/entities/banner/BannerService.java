package de.teamholy.core.api.entities.banner;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.AbstractService;

public class BannerService extends AbstractService<Banner, String, BannerRepository> {

    public BannerService(CoreAPI coreAPI) {
        super(coreAPI, BannerRepository.class, false);
    }
}
