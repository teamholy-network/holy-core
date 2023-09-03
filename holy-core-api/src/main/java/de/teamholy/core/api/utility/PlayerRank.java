package de.teamholy.core.api.utility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum PlayerRank {

    ADMIN("Admin", "§4Admin §8┃ §4", "§4Admin §8┃ §4", "§4", 7100,
        153, 0, 0),
    MANAGER("Manager", "§4Manager §8┃ §4", "§4Manager §8┃ §4", "§4", 7200,
        153, 0, 0),
    YOUTUBERPLUS("YoutuberPlus", "§4Youtuber+ §8┃ §4", "§4YT+ §8┃ §4", "§4", 7300,
        153, 0, 0),
    SRDEVELOPER("SrDeveloper", "§bSrDeveloper §8┃ §b", "§bSrDev §8┃ §b", "§b", 7340,
        0, 204, 255),
    LIVEPLUS("LivePlus", "§4TTV+ §8┃ §4", "§4TTV+ §8┃ §4", "§4", 7350,
        153, 0, 0),
    DEVELOPER("Developer", "§bDeveloper §8┃ §b", "§bDev §8┃ §b", "§b", 7400,
        0, 204, 255),
    SRMODERATOR("SrModerator", "§cSrModerator §8┃ §c", "§cSrMod §8┃ §c", "§c", 7500,
        255, 0, 0),
    SRBUILDER("SrBuilder", "§eSrBuilder §8┃ §e", "§eSrBuild §8┃ §e", "§e", 7550,
        255, 255, 0),
    SRCONTENT("SrContent", "§9SrContent §8┃ §9", "§9SrCon §8┃ §9", "§9", 7600,
        51, 51, 255),
    SRDESIGNER("SrDesigner", "§9SrDesigner §8┃ §9", "§9SrDes §8┃ §9", "§9", 7650,
        0, 102, 255),
    MODERATOR("Moderator", "§cModerator §8┃ §c", "§cMod §8┃ §c", "§c", 7700,
        255, 0, 0),
    CONTENT("Content", "§9Content §8┃ §9", "§9Con §8┃ §9", "§9", 7800,
        51, 51, 255),
    SUPPORTER("Supporter", "§aSupporter §8┃ §a", "§aSup §8┃ §a", "§a", 7900,
        0, 255, 0),
    JRDEVELOPER("JrDeveloper", "§bJrDeveloper §8┃ §b", "§bJrDev §8┃ §b", "§b", 7950,
        0, 204, 255),
    JRSUPPORTER("JrSupporter", "§aJrSupporter §8┃ §a", "§aJrSup §8┃ §a", "§a", 8000,
        0, 255, 0),
    BUILDER("Builder", "§eBuilder §8┃ §e", "§eBuild §8┃ §e", "§e", 8100,
        255, 255, 0),
    DESIGNER("Designer", "§9Designer §8┃ §9", "§9Design §8┃ §9", "§9", 8200,
        0, 102, 255),
    HOLY("Holy", "§6Holy §8┃ §f", "§6Holy §8┃ §f", "§f", 8250,
        255, 255, 255),
    YOUTUBER("Youtuber", "§5Youtuber §8┃ §5", "§5YT §8┃ §5", "§5", 8300,
        102, 0, 102),
    CHAMPION("Champion", "§3Champion §8┃ §3", "§3Champ §8┃ §3", "§3", 8400,
        51, 51, 255),
    VIP("VIP", "§dVIP §8┃ §d", "§dVIP §8┃ §d", "§d", 8500,
        204, 0, 153),
    PREMIUMPLUS("PremiumPlus", "§ePremium+ §8┃ §e", "§ePrem+ §8┃ §e", "§e", 8600,
        255, 255, 0),
    PREMIUM("Premium", "§6Premium §8┃ §6", "§6Premium §8┃ §6", "§6", 8700,
        204, 102, 0),
    //PLAYER("Player", "§7", "§7", "§7", 9000,
      //  80, 80, 80);

    PLAYER("Player", "§6Premium §8┃ §6", "§6Premium §8┃ §6", "§6", 8700,
                204, 102, 0);

    String name;
    String chatPrefix;
    String tabPrefix;
    String colorCode;
    int sortId;
    int red, green, blue;
}
