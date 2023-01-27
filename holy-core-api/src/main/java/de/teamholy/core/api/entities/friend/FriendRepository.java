package de.teamholy.core.api.entities.friend;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("friend_profile_collection")
public interface FriendRepository extends Repository<FriendProfile, UUID> {
}
