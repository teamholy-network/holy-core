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

import java.security.SecureRandom;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
@UtilityClass
public class BanUtil {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public TextComponent generateBanMessage(ProxiedPlayer staffmember, BanProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeTranslateAPI.translatePlaceholder(staffmember, "{} has banned {} for {}.", BungeeCore.getInstance().getPlayerColor(punishProfile.getAuthorId()) + authorName + "§7", BungeeCore.getInstance().getPlayerColor(punishProfile.getPlayerId()) + playerName + "§7", "§6" + punishProfile.getReason() + "§7");
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateUnbanMessage(ProxiedPlayer staffmember, String unbanner, BanProfile punishProfile) {
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX
            + BungeeTranslateAPI.translatePlaceholder(staffmember, "{} has unbanned {}.", (unbanner.equalsIgnoreCase("console") ? "§4§l" : BungeeCore.getInstance().getPlayerColor(punishProfile.getAuthorId()))
            + unbanner + "§7", BungeeCore.getInstance().getPlayerColor(punishProfile.getPlayerId()) + playerName + "§7");
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateUnbanMessage(ProxiedPlayer staffmember, String unbanner, UUID unbannerId, BanProfile punishProfile) {
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX
            + BungeeTranslateAPI.translatePlaceholder(staffmember, "{} has unbanned {}.", (unbanner.equalsIgnoreCase("console") ? "§4§l" : BungeeCore.getInstance().getPlayerColor(unbannerId))
            + unbanner + "§7", BungeeCore.getInstance().getPlayerColor(punishProfile.getPlayerId()) + playerName + "§7");
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateMuteMessage(ProxiedPlayer staffmember, MuteProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeTranslateAPI.translatePlaceholder(staffmember, "{} has muted {} for {}.", BungeeCore.getInstance().getPlayerColor(punishProfile.getAuthorId()) + authorName + "§7", BungeeCore.getInstance().getPlayerColor(punishProfile.getPlayerId()) + playerName + "§7", "§6" + punishProfile.getReason() + "§7");
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateCustomMuteMessage(ProxiedPlayer staffmember, MuteProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeTranslateAPI.translatePlaceholder(staffmember, "{} has custom muted {} for {}.{} Time: §e{}", BungeeCore.getInstance().getPlayerColor(punishProfile.getAuthorId()) + authorName + "§7", BungeeCore.getInstance().getPlayerColor(punishProfile.getPlayerId()) + playerName + "§7", "§6" + punishProfile.getReason() + "§7", "\n§7", TimeUtil.beautifyTime(punishProfile.getMillisLeft(), TimeUnit.MILLISECONDS, true));
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateCustomBanMessage(ProxiedPlayer staffmember, BanProfile punishProfile) {
        String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeTranslateAPI.translatePlaceholder(staffmember, "{} has custom banned {} for {}. " + "{} Time: {}", BungeeCore.getInstance().getPlayerColor(punishProfile.getAuthorId()) + authorName + "§7", BungeeCore.getInstance().getPlayerColor(punishProfile.getPlayerId()) + playerName + "§7", "§6" + punishProfile.getReason() + "§7", "\n§7", "§e" + TimeUtil.beautifyTime(punishProfile.getMillisLeft(), TimeUnit.MILLISECONDS, true));
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public TextComponent generateUnmuteMessage(ProxiedPlayer staffmember, String unmuter, MuteProfile punishProfile) {
        String playerName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getPlayerId());
        String message = Message.PUNISH_PREFIX + BungeeTranslateAPI.translatePlaceholder(staffmember, "{} has unmuted {}.", BungeeCore.getInstance().getPlayerColor(punishProfile.getAuthorId()) + unmuter + "§7", BungeeCore.getInstance().getPlayerColor(punishProfile.getPlayerId()) + playerName + "§7");
        return generateLookUpComponent(staffmember, playerName, message);
    }

    public boolean isFilteredCommand(String message) {
        message = message.toLowerCase(Locale.ROOT);
        return message.startsWith("/r ") ||
            message.startsWith("/msg ") ||
            message.startsWith("/party chat") ||
            message.startsWith("/cc ") ||
            message.startsWith("/pc ") ||
            message.startsWith("/pchat ") ||
            message.startsWith("/teaming chat") ||
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

        builder.append(Message.LINE_DOWN)
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

        builder.append("§7Want to get unbanned? create an appeal on §ehttps://teamholy.de/ocelot\n")
            .append("\n")
            .append(Message.LINE_DOWN)
            .append("\n\n")
            .append("§7If you are §cnot linked or logged in §7to the §awebsite§7, use the code §e" + punishProfile.getWebLinkId() + "§7 to link your account.")
            .append("\n")
            .append("§ahttps://teamholy.de/link")
            .append("\n\n");

        return builder.toString();
    }

    public String generateMuteChatMessage(ProxiedPlayer player, MuteProfile punishProfile) {
        String time = punishProfile.getDuration() != -1 ? TimeUtil.beautifyTime(punishProfile.getMillisLeft(), TimeUnit.MILLISECONDS) : BungeeTranslateAPI.translate(player, "Permanent");
        return Message.PUNISH_PREFIX + "§7" + BungeeTranslateAPI.translatePlaceholder(player, "You're muted for {} ({})", "§6" + punishProfile.getReason() + "§8", "§7" + time + "§8");
    }

    private TextComponent generateLookUpComponent(ProxiedPlayer player, String name, String message) {
        TextComponent component = new ChatAction().text("§8[§eLOOKUP§8]").hover("§7" + BungeeTranslateAPI.translate(player, "Click to lookup") + " " + name).execute("lookup " + name).component();
        TextComponent mainComp = new TextComponent(message);
        mainComp.addExtra(component);
        return mainComp;
    }

    public String generateBanWebLinkId() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            if (i > 0 && i % 4 == 0) {
                sb.append('-');
            }
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public boolean addBanWebLinkIdToProfile(BanProfile banProfile) {
        String webLinkId = generateBanWebLinkId();
        banProfile.setWebLinkId(webLinkId);
        BungeeCore.getAPI().getBanService().saveEntity(banProfile, false, true);
        return true;
    }

}
