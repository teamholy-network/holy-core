package de.teamholy.core.api.entities.player;

import de.teamholy.core.api.entities.clan.Clan;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.List;
import java.util.UUID;

@Collection("player_profile_collection")
public interface PlayerRepository extends Repository<PlayerProfile, UUID> {

    List<PlayerProfile> findManyByIp(String Ip);

    PlayerProfile findFirstByPlayerName(String name);

}
