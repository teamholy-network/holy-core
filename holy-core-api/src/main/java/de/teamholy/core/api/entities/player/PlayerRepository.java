package de.teamholy.core.api.entities.player;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("player_profile_collection")
public interface PlayerRepository extends Repository<PlayerProfile, UUID> {
}
