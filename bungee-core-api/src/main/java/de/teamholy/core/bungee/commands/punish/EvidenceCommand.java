package de.teamholy.core.bungee.commands.punish;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.mute.MuteProfile;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.SenderCommand;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/* copyright by Yassino */
public class EvidenceCommand extends SenderCommand {


    public EvidenceCommand() {
        super(new String[]{"evidence", "beweis"}, "teamholy.evidence");
    }

    public void execute(CommandSender sender, String[] args) {
        UUID author = BungeeUtil.parseAuthorUUID(sender);
        if (!BungeeUtil.hasPermission(sender, "teamholy.evidence")) {
            BungeeUtil.sendNoPermission(sender);
            return;
        }
        if (args.length == 3) {
            String target = args[0];
            UUID uuid = BungeeUtil.parseTargetArgument(target);
            if (uuid == null) {
                sender.sendMessage(Message.PUNISH_PREFIX + "§c" + BungeeTranslateAPI.translate(author, "Error while fetching UUID from") + " §e" + target + "§c!");
                return;
            }
            boolean isBan = args[1].equalsIgnoreCase("ban");

            if (isBan) {
                BanProfile banProfile = BungeeCore.getAPI().getBanService().getEntity(uuid, () -> BungeeCore.getAPI().getBanService().getRepository().findFirstById(uuid));
                if (banProfile == null) {
                    sender.sendMessage(Message.PUNISH_PREFIX + "§c" + BungeeTranslateAPI.translatePlaceholder(author, "The player §e{}§c isn't banned!", target));
                    return;
                }
                banProfile.setEvidence(args[2]);
                BungeeCore.getAPI().getBanService().saveEntity(banProfile, false, true);
                sender.sendMessage(Message.PUNISH_PREFIX + "§7" + BungeeTranslateAPI.translatePlaceholder(author, "You changed the ban-evidence of §e{}§7!", target));

            } else {
                MuteProfile muteProfile = BungeeCore.getAPI().getMuteService().getEntity(uuid, () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(uuid));
                if (muteProfile == null) {
                    sender.sendMessage(Message.PUNISH_PREFIX + "§c" + BungeeTranslateAPI.translatePlaceholder(author, "The player §e{}§c isn't muted!", target));
                    return;
                }
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                boolean isOnline = player != null && player.isConnected();
                muteProfile.setEvidence(args[2]);
                BungeeCore.getAPI().getMuteService().saveEntity(muteProfile, isOnline, true);
                sender.sendMessage(Message.PUNISH_PREFIX + "§7" + BungeeTranslateAPI.translatePlaceholder(author, "You changed the mute-evidence of §e{}§7!", target));

            }
        } else {
            printUsage(sender, author);
        }
    }

    public void printUsage(CommandSender commandSender, UUID author) {
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7/evidence (" + BungeeTranslateAPI.translate(author, "name") + ") (ban | mute) (" + BungeeTranslateAPI.translate(author, "evidence") + ")");
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7" + BungeeTranslateAPI.translate(author, "evidence can be a screenshot or yt link!"));
    }

}
