package de.teamholy.core.api.utility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum PlayerRank {

    ADMIN("Admin", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7100,
        153, 0, 0),
    MANAGER("Manager", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7200,
        153, 0, 0),
    YOUTUBERPLUS("YoutuberPlus", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7300,
        153, 0, 0),
    SRDEVELOPER("SrDeveloper", "§§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7340,
        153, 0, 0),
    LIVEPLUS("LivePlus", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7350,
        153, 0, 0),
    DEVELOPER("Developer", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7400,
        153, 0, 0),
    SRMODERATOR("SrModerator", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7500,
        153, 0, 0),
    SRBUILDER("SrBuilder", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7550,
        153, 0, 0),
    SRCONTENT("SrContent", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7600,
        153, 0, 0),
    SRDESIGNER("SrDesigner", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7650,
        153, 0, 0),
    MODERATOR("Moderator", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7700,
        153, 0, 0),
    CONTENT("Content", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7800,
        153, 0, 0),
    SUPPORTER("Supporter", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7900,
        153, 0, 0),
    JRDEVELOPER("JrDeveloper", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 7950,
        153, 0, 0),
    JRSUPPORTER("JrSupporter", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 8000,
        153, 0, 0),
    BUILDER("Builder", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 8100,
        153, 0, 0),
    DESIGNER("Designer", "§f§lStaff §8┃ §f", "§f§lStaff §8┃ §f", "§f§l", 8200,
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