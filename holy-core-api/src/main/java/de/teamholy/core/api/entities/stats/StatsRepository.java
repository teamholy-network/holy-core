package de.teamholy.core.api.entities.stats;

import de.teamholy.core.api.entities.punish.PunishProfile;
import de.teamholy.core.api.entities.punish.PunishType;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;
import eu.koboo.en2do.repository.methods.transform.Transform;

import java.util.List;
import java.util.UUID;

@Collection("stats_profile_collection")
public interface StatsRepository extends Repository<StatsProfile, UUID> {

}
