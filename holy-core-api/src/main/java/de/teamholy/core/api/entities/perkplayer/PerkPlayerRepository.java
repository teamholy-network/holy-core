package de.teamholy.core.api.entities.perkplayer;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.List;
import java.util.UUID;

@Collection("perk_profile_collection")
public interface PerkPlayerRepository extends Repository<PerkPlayerProfile, UUID> {
}
