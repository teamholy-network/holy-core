package de.teamholy.core.api.entities.player;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;
import eu.koboo.en2do.repository.methods.transform.Transform;

import java.util.List;
import java.util.UUID;

@Collection("player_profile_collection")
public interface PlayerRepository extends Repository<PlayerProfile, UUID> {

    List<PlayerProfile> findManyByIp(String Ip);

    @Transform("findFirstByPlayerNameIgn")
    PlayerProfile findFirstByPlayerName(String name);

}
