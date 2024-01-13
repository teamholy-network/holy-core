package de.teamholy.core.bungee.util;

import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.mute.MuteProfile;
import de.teamholy.core.api.utility.TimeUtil;
import de.teamholy.core.bungee.BungeeCore;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
@UtilityClass
public class BanUtil {


    public TextComponent generateBanMessage(BanProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId()) + authorName + "§7 has banned " + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7 for §6" + punishProfile.getReason() + "§7.";
        return generateLookUpComponent(playerName, message);
    }

    public TextComponent generateUnbanMessage(String unbanner, BanProfile punishProfile) {
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX
            + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId())
            + unbanner + "§7 has unbanned "
            + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7.";
        return generateLookUpComponent(playerName, message);
    }

    public TextComponent generateUnbanMessage(String unbanner, UUID unbannerId, BanProfile punishProfile) {
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX
            + BungeeCore.getAPI().getCloudManager().getColor(unbannerId)
            + unbanner + "§7 has unbanned "
            + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7.";
        return generateLookUpComponent(playerName, message);
    }

    public TextComponent generateMuteMessage(MuteProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId()) + authorName + "§7 has muted " + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7 for §6" + punishProfile.getReason() + "§7.";
        return generateLookUpComponent(playerName, message);
    }

    public TextComponent generateCustomMuteMessage(MuteProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId()) + authorName + "§7 has §ecustom §7muted " + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7 for §6" + punishProfile.getReason() + "§7. " +
            "\n§7Time§8: §e" + TimeUtil.beautifyTime(punishProfile.getMillisLeft(), TimeUnit.MILLISECONDS, true);
        return generateLookUpComponent(playerName, message);
    }

    public TextComponent generateCustomBanMessage(BanProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId()) + authorName + "§7 has §ecustom §7banned " + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7 for §6" + punishProfile.getReason() + "§7. " +
            "\n§7Time§8: §e" + TimeUtil.beautifyTime(punishProfile.getMillisLeft(), TimeUnit.MILLISECONDS, true);
        return generateLookUpComponent(playerName, message);
    }

    public TextComponent generateUnmuteMessage(String unmuter, MuteProfile punishProfile) {
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId()) + unmuter + "§7 has unmuted " + BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7.";
        return generateLookUpComponent(playerName, message);
    }

    public String generateBanScreen(BanProfile punishProfile) {

        String timeString = TimeUtil.beautifyTime(punishProfile.getMillisLeft(), TimeUnit.MILLISECONDS);
        String dateString = BungeeUtil.parseDate(punishProfile.getValidUntilDate());
        if (punishProfile.getDuration() == -1L) {
            timeString = "permanent";
            dateString = null;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(Message.LINE)
            .append("\n\n")
            .append("§cYou're banned!\n\n")
            .append("§7Reason§8: §c")
            .append(punishProfile.getReason())
            .append("\n\n")
            .append("§7Time remaining§8: §e")
            .append(timeString)
            .append("\n");


        if (dateString != null)
            builder.append("§7Date§8: §e")
                .append(dateString)
                .append("\n\n");

        builder.append("§7You can make an unban appeal at §ehttps://forum.teamholy.de/\n")
            .append("\n")
            .append(Message.LINE)
            .append("\n");

        return builder.toString();
    }

    public String generateMuteChatMessage(MuteProfile punishProfile) {
        String time = punishProfile.getDuration() != -1 ? TimeUtil.beautifyTime(punishProfile.getMillisLeft(), TimeUnit.MILLISECONDS) : "Permanent";
        return Message.PUNISH_PREFIX + "§7You're muted for §6" + punishProfile.getReason() + " §8(§7" + time + "§8)";
    }

    private TextComponent generateLookUpComponent(String name, String message) {
        TextComponent component = new ChatAction().text("§8[§eLOOKUP§8]").hover("§7Click to lookup " + name).execute("lookup " + name).component();
        TextComponent mainComp = new TextComponent(message);
        mainComp.addExtra(component);
        return mainComp;
    }

}
