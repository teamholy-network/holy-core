package de.teamholy.core.api.entities.punishhistory;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("punishHistory_profile_collection")
public interface PunishHistoryRepository extends Repository<PunishHistoryProfile, UUID> {
}
