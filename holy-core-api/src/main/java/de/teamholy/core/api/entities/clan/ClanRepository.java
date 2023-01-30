package de.teamholy.core.api.entities.clan;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;
import eu.koboo.en2do.repository.methods.transform.Transform;

import java.util.UUID;

@Collection("clans_collection")
public interface ClanRepository extends Repository<Clan, UUID> {

    @Transform("findFirstByTagIgn")
    Clan findFirstByTag(String tag);

    @Transform("findFirstByNameIgn")
    Clan findFirstByName(String name);

}
