package de.teamholy.core.bungee.model;

import lombok.Getter;

/* copyright by Yassino */
@Getter
public class JoinME {

    private Long cooldown;
    private String server;

    public JoinME(Long cooldown, String server) {
        this.cooldown = cooldown;
        this.server = server;
    }

}
