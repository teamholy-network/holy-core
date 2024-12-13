package de.teamholy.core.bungee.util;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.mute.MuteProfile;
import de.teamholy.core.api.utility.TimeUtil;
import de.teamholy.core.bungee.BungeeCore;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
@UtilityClass
public class BanUtil {


    public TextComponent generateBanMessage(ProxiedPlayer staffmember, BanProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeTranslateAPI.translatePlaceholder(staffmember,  "{} has banned {} for {}.",BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId()) + authorName+"§7",BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName+"§7","§6"+punishProfile.getReason()+"§7");
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateUnbanMessage(ProxiedPlayer staffmember, String unbanner, BanProfile punishProfile) {
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX
            + BungeeTranslateAPI.translatePlaceholder(staffmember, "{} has unbanned {}.", (unbanner.equalsIgnoreCase("console") ? "§4§l" : BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId()))
            + unbanner + "§7", BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7");
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateUnbanMessage(ProxiedPlayer staffmember, String unbanner, UUID unbannerId, BanProfile punishProfile) {
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX
            + BungeeTranslateAPI.translatePlaceholder(staffmember,"{} has unbanned {}.", (unbanner.equalsIgnoreCase("console") ? "§4§l" : BungeeCore.getAPI().getCloudManager().getColor(unbannerId))
            + unbanner + "§7", BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7");
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateMuteMessage(ProxiedPlayer staffmember, MuteProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeTranslateAPI.translatePlaceholder(staffmember,"{} has muted {} for {}.",BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId()) + authorName + "§7",BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7","§6" + punishProfile.getReason() + "§7");
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateCustomMuteMessage(ProxiedPlayer staffmember, MuteProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeTranslateAPI.translatePlaceholder(staffmember,"{} has custom muted {} for {}.{} Time: §e{}", BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId()) + authorName + "§7", BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7", "§6" + punishProfile.getReason() + "§7", "\n§7", TimeUtil.beautifyTime(punishProfile.getMillisLeft(), TimeUnit.MILLISECONDS, true));
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateCustomBanMessage(ProxiedPlayer staffmember, BanProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeTranslateAPI.translatePlaceholder(staffmember,"{} has custom banned {} for {}. " + "{} Time: {}", BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId()) + authorName + "§7",BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName + "§7", "§6" + punishProfile.getReason() + "§7", "\n§7","§e" + TimeUtil.beautifyTime(punishProfile.getMillisLeft(), TimeUnit.MILLISECONDS, true));
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateUnmuteMessage(ProxiedPlayer staffmember, String unmuter, MuteProfile punishProfile) {
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeTranslateAPI.translatePlaceholder(staffmember, "{} has unmuted {}.", BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getAuthorId()) + unmuter + "§7", BungeeCore.getAPI().getCloudManager().getColor(punishProfile.getPlayerId()) + playerName+"§7");
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public boolean isFilteredCommand(String message) {
        return message.startsWith("/r") ||
            message.startsWith("/msg") ||
            message.startsWith("/party chat") ||
            message.startsWith("/clan chat");
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

        builder.append("§7You can make an unban appeal by opening a ticket on §ehttps://dc.teamholy.de/\n")
            .append("\n")
            .append(Message.LINE)
            .append("\n");

        return builder.toString();
    }

    public String generateMuteChatMessage(ProxiedPlayer player, MuteProfile punishProfile) {
        String time = punishProfile.getDuration() != -1 ? TimeUtil.beautifyTime(punishProfile.getMillisLeft(), TimeUnit.MILLISECONDS) : BungeeTranslateAPI.translate(player,"Permanent");
        return Message.PUNISH_PREFIX + "§7" + BungeeTranslateAPI.translatePlaceholder(player, "You're muted for {} ({})", "§6"+punishProfile.getReason()+"§8", "§7"+time+"§8");
    }

    private TextComponent generateLookUpComponent(ProxiedPlayer player, String name, String message) {
        TextComponent component = new ChatAction().text("§8[§eLOOKUP§8]").hover("§7"+BungeeTranslateAPI.translate(player,"Click to lookup")+" " + name).execute("lookup " + name).component();
        TextComponent mainComp = new TextComponent(message);
        mainComp.addExtra(component);
        return mainComp;
    }

}
