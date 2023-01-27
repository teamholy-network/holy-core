package de.teamholy.core.api.entities.clan;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("clans_collection")
public interface ClanRepository extends Repository<Clan, UUID> {
}
