package de.teamholy.core.api.entities.punishhistory;

import de.teamholy.core.api.entities.punish.PunishProfile;
import de.teamholy.core.api.entities.punish.PunishType;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;
import eu.koboo.en2do.repository.methods.transform.Transform;

import java.util.List;
import java.util.UUID;

@Collection("punishhistory_profile_collection")
public interface PunishHistoryRepository extends Repository<PunishHistoryProfile, UUID> {

    @Transform("findManyByPlayerIdAndPunishType")
    List<PunishProfile> findAllOfPlayer(UUID playerId, PunishType punishType);

    @Transform("findbyPlayerIdAnd")
    PunishProfile findPunishOfPlayer(UUID playerId, String punishId);
}
