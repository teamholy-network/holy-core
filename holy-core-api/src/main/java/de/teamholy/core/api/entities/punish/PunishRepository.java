package de.teamholy.core.api.entities.punish;

import de.teamholy.core.api.entities.player.PlayerProfile;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;
import eu.koboo.en2do.repository.methods.transform.Transform;

import java.util.List;
import java.util.UUID;

@Collection("punish_profile_collection")
public interface PunishRepository extends Repository<PunishProfile, UUID> {

    @Transform("findManyByPlayerIdAndPunishType")
    List<PunishProfile> findAllOfPlayer(UUID playerId, PunishType punishType);
}
