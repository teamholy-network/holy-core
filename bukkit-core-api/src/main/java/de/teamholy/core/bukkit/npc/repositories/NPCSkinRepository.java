package de.teamholy.core.bukkit.npc.repositories;

import de.teamholy.core.bukkit.npc.models.SkinEntry;
import eu.koboo.en2do.repository.AsyncRepository;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("npcskin_collection")
public interface NPCSkinRepository extends Repository<SkinEntry, UUID>, AsyncRepository<SkinEntry, UUID> {
}
