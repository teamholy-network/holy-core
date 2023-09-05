package de.teamholy.core.api.entities.banner;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

@Collection("banners_collection")
public interface BannerRepository extends Repository<Banner, String> {
}
