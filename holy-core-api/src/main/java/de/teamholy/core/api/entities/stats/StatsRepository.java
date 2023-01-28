package de.teamholy.core.api.entities.stats;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("stats_profile_collection")
public interface StatsRepository extends Repository<StatsProfile, UUID> {

}
