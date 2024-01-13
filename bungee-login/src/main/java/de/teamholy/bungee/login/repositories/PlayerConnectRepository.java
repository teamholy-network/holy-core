package de.teamholy.bungee.login.repositories;

import de.teamholy.bungee.login.model.PlayerObject;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

@Collection("player_connect_repository")
public interface PlayerConnectRepository extends Repository<PlayerObject, String> {
}
