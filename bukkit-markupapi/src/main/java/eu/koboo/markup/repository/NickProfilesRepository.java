package eu.koboo.markup.repository;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;
import eu.koboo.markup.util.PlayerPreset;
import org.bukkit.craftbukkit.libs.org.ibex.nestedvm.util.Platform;

import java.util.UUID;

/* copyright by Yassino */
@Collection("nickpreset_profile_collection")
public interface NickProfilesRepository extends Repository<PlayerPreset, UUID> {
}
