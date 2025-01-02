package de.teamholy.core.api.utility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum PlayerRank {

    ADMIN("Admin", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7100,
        153, 0, 0),
    MANAGER("Manager", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7200,
        153, 0, 0),
    YOUTUBERPLUS("YoutuberPlus", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7300,
        153, 0, 0),
    SRDEVELOPER("SrDeveloper", "§§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7340,
        153, 0, 0),
    LIVEPLUS("LivePlus", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7350,
        153, 0, 0),
    DEVELOPER("Developer", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7400,
        153, 0, 0),
    SRMODERATOR("SrModerator", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7500,
        153, 0, 0),
    SRBUILDER("SrBuilder", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7550,
        153, 0, 0),
    SRCONTENT("SrContent", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7600,
        153, 0, 0),
    SRDESIGNER("SrDesigner", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7650,
        153, 0, 0),
    MODERATOR("Moderator", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7700,
        153, 0, 0),
    CONTENT("Content", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7800,
        153, 0, 0),
    SUPPORTER("Supporter", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7900,
        153, 0, 0),
    JRDEVELOPER("JrDeveloper", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 7950,
        153, 0, 0),
    JRSUPPORTER("JrSupporter", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 8000,
        153, 0, 0),
    BUILDER("Builder", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 8100,
        153, 0, 0),
    DESIGNER("Designer", "§4§lStaff §8┃ §4", "§4§lStaff §8┃ §4", "§4", 8200,
        153, 0, 0),
    HOLY("Holy", "§4Admin §8┃ §4", "§4Admin §8┃ §4", "§4", 8250,
        153, 0, 0),
    YOUTUBER("Youtuber", "§4Admin §8┃ §4", "§4Admin §8┃ §4", "§4", 8300,
        153, 0, 0),
    CHAMPION("Champion", "§4Admin §8┃ §4", "§4Admin §8┃ §4", "§4", 8400,
        153, 0, 0),
    VIP("VIP", "§4Admin §8┃ §4", "§4Admin §8┃ §4", "§4", 8500,
        153, 0, 0),
    PREMIUMPLUS("PremiumPlus", "§4Admin §8┃ §4", "§4Admin §8┃ §4", "§4", 8600,
        153, 0, 0),
    PREMIUM("Premium", "§4Admin §8┃ §4", "§4Admin §8┃ §4", "§4", 8700,
        153, 0, 0),
    PLAYER("Player", "§4Admin §8┃ §4", "§4Admin §8┃ §4", "§4", 9000,
        153, 0, 0);

    String name;
    String chatPrefix;
    String tabPrefix;
    String colorCode;
    int sortId;
    int red, green, blue;
}