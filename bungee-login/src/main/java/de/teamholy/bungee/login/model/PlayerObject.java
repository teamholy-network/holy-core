package de.teamholy.bungee.login.model;


import eu.koboo.en2do.repository.entity.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PlayerObject {
    boolean premium;
    boolean bedrock;
    @Id
    String name;
    UUID uuid;
    HashMap<String, Long> ips = new HashMap<String, Long>();
    String passwordhash;
    String hostname;
}
