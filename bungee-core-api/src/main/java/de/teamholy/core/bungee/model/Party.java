package de.teamholy.core.bungee.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;

/* copyright by Yassino */
@Getter
@Setter
public class Party {

    private final ArrayList<UUID> partyPlayers;
    private final ArrayList<UUID> invitedPlayers;
    private int maxSize;
    @Setter
    private boolean isPublic;

    public Party(int maxSize) {
        partyPlayers = new ArrayList<>();
        invitedPlayers = new ArrayList<>();
        this.maxSize = maxSize;
        isPublic = false;
    }

}
