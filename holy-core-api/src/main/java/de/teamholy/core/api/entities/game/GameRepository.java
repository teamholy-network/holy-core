package de.teamholy.core.api.entities.game;

import eu.koboo.en2do.repository.AsyncRepository;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("game_profile_collection")
public interface GameRepository extends Repository<GameProfile, UUID> {

}
