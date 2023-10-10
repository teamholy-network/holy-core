package de.teamholy.core.api.entities.stats;

import eu.koboo.en2do.repository.entity.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class StatsProfile {

    @Id
    private UUID id;
    private String playerName;
    private UUID playerUUID;
    private String playerHash;
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
    private Streak profileStreak;

    @Getter
    @Setter
    public static class PlayerProfileSocial {
        private String profileMessage;
        private List<String> profileComments;
        private List<ProfileLikes> profileLikes;
    }

    @Getter
    @Setter
    public static class ProfileViews {
        private int total;
        private List<String> ips;
    }

    @Getter
    @Setter
    public static class ProfileLikes {
        private String name;
        private UUID uuid;
    }

    @Getter
    @Setter
    public static class Streak {
        private int current;
        private int best;
        private long next;
        private long limit;
    }
}
