package de.teamholy.core.api.entities.punishhistory;

import de.teamholy.core.api.entities.ban.BanProfile;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;
import eu.koboo.en2do.repository.methods.transform.Transform;

import java.util.List;
import java.util.UUID;

@Collection("punishHistory_profile_collection")
public interface PunishHistoryRepository extends Repository<PunishHistoryProfile, UUID> {
}
