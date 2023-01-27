package de.teamholy.core.api.entities.clanplayer;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("clan_profile_collection")
public interface ClanPlayerRepository extends Repository<ClanPlayerProfile, UUID> {
}
