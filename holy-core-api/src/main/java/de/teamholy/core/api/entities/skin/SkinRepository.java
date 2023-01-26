package de.teamholy.core.api.entities.skin;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("skin_profile_collection")
public interface SkinRepository extends Repository<SkinProfile, UUID> {
}
