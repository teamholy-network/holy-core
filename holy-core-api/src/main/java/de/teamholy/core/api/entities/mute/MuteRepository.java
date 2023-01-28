package de.teamholy.core.api.entities.mute;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("mute_profile_collection")
public interface MuteRepository extends Repository<MuteProfile, UUID> {
}
