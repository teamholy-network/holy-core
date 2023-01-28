package de.teamholy.core.api.entities.ban;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("ban_profile_collection")
public interface BanRepository extends Repository<BanProfile, UUID> {
}
