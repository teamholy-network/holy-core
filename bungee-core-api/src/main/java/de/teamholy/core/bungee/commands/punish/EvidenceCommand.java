package de.teamholy.core.bungee.commands.punish;

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
        if (!BungeeUtil.hasPermission(sender, "teamholy.evidence")) {
            BungeeUtil.sendNoPermission(sender);
            return;
        }
        if (args.length == 3) {
            String target = args[0];
            UUID uuid = BungeeUtil.parseTargetArgument(target);
            if (uuid == null) {
                sender.sendMessage(Message.PUNISH_PREFIX + "§cError while fetching UUID from §e" + target + "§c!");
                return;
            }
            boolean isBan = args[1].equalsIgnoreCase("ban");

            if (isBan) {
                BanProfile banProfile = BungeeCore.getAPI().getBanService().getEntity(uuid, () -> BungeeCore.getAPI().getBanService().getRepository().findFirstById(uuid));
                if (banProfile == null) {
                    sender.sendMessage(Message.PUNISH_PREFIX + "§cThe player §e" + target + "§c isn't banned!");
                    return;
                }
                banProfile.setEvidence(args[2]);
                BungeeCore.getAPI().getBanService().saveEntity(banProfile, false, true);
                sender.sendMessage(Message.PUNISH_PREFIX + "§7You changed the ban-evidence of §e" + target + "§7!");

            } else {
                MuteProfile muteProfile = BungeeCore.getAPI().getMuteService().getEntity(uuid, () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(uuid));
                if (muteProfile == null) {
                    sender.sendMessage(Message.PUNISH_PREFIX + "§cThe player §e" + target + "§c isn't muted!");
                    return;
                }
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                boolean isOnline = player != null && player.isConnected();
                muteProfile.setEvidence(args[2]);
                BungeeCore.getAPI().getMuteService().saveEntity(muteProfile, isOnline, true);
                sender.sendMessage(Message.PUNISH_PREFIX + "§7You changed the mute-evidence of §e" + target + "§7!");

            }
        } else {
            printUsage(sender);
        }
    }

    public void printUsage(CommandSender commandSender) {
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7/evidence (name) (ban | mute) (evidence)");
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7evidence can be a screenshot or yt link!");
    }

}
