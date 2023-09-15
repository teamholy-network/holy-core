package de.teamholy.core.api.entities.stats;

import eu.koboo.en2do.repository.entity.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class StatsProfile {

    // TODO: 'playerProfile' before every field is redundant, remove it later. Refactor the database too. Im too lazy to do it now.

    @Id
    private UUID id;
    private String playerName;
    private UUID playerUUID;
    private boolean playerProfileLinked;
    private boolean playerProfileVoted;
    private boolean playerProfileFirstTimeLinked;
    private long playerProfileLastTimeLinked;
    private String playerProfileLinkCode;
    private String playerProfileCookie;
    private String playerProfileDiscordLinkCode;
    private boolean playerProfileDiscordLinked;

    private long playerProfileLastTimeLoggedIn;

    private PlayerProfileSocial playerProfileSocial;
    private ProfileViews profileViews;




    @Getter
    @Setter
    public static class PlayerProfileSocial {
        private String profileMessage;
        private List<String> profileComments;
    }

    @Getter
    @Setter
    public static class ProfileViews {
        private int total;
        private List<String> ips;
    }


}


