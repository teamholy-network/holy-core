package de.teamholy.core.bungee.commands.punish;

import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.mute.MuteProfile;
import de.teamholy.core.api.utility.TimeUtil;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.SenderCommand;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class PunishReduceCommand extends SenderCommand {


    public PunishReduceCommand() {
        super(new String[]{"punishreduce"}, "teamholy.punishreduce");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        UUID author = BungeeUtil.parseAuthorUUID(sender);
        if (!BungeeUtil.hasPermission(sender, "teamholy.punishreduce")) {
            BungeeUtil.sendNoPermission(sender);
            return;
        }
        if (args.length == 3) {
            if (!args[1].equalsIgnoreCase("ban") && !args[1].equalsIgnoreCase("mute")) {
                printUsage(sender, author);
                return;
            }

            String target = args[0];
            UUID uuid = BungeeUtil.parseTargetArgument(target);
            if (uuid == null) {
                sender.sendMessage(Message.PUNISH_PREFIX + "§c" + "Error while fetching UUID from" + " §e" + target + "§c!");
                return;
            }


            boolean isBan = args[1].equalsIgnoreCase("ban");


            try {

                long reduceBy = TimeUtil.getTimeInMilli(args[2]);

                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                boolean isOnline = player != null && player.isConnected();

                if (isBan) {
                    BanProfile banProfile = BungeeCore.getAPI().getBanService().getEntity(uuid, () -> BungeeCore.getAPI().getBanService().getRepository().findFirstById(uuid));
                    if (banProfile == null) {
                        sender.sendMessage(Message.PUNISH_PREFIX + "§c" + ("The player §e" + (target) + "§c isn't banned!"));
                        return;
                    }


                    long banTime = banProfile.getDuration();

                    long newDuration = banTime == -1 ? reduceBy : banTime - reduceBy;
                    if (newDuration < 0) {
                        sender.sendMessage(Message.PUNISH_PREFIX + "§c" + "The new ban-time would be less than zero!");
                        return;
                    }
                    banProfile.setDuration(newDuration);
                    BungeeCore.getAPI().getBanService().saveEntity(banProfile, false, true);
                    String timeLeft = TimeUtil.beautifyTime(banProfile.getMillisLeft(), TimeUnit.MILLISECONDS);
                    sender.sendMessage(Message.PUNISH_PREFIX + "§7" + ("You changed the ban-time of §e" + (target) + "§7 to §c" + (timeLeft) + "§7!"));

                } else {
                    MuteProfile muteProfile = BungeeCore.getAPI().getMuteService().getEntity(uuid, () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(uuid));
                    if (muteProfile == null) {
                        sender.sendMessage(Message.PUNISH_PREFIX + "§c" + ("The player §e" + (target) + "§c isn't muted!"));
                        return;
                    }


                    long banTime = muteProfile.getDuration();

                    long newDuration = banTime == -1 ? reduceBy : banTime - reduceBy;
                    if (newDuration < 0) {
                        sender.sendMessage(Message.PUNISH_PREFIX + "§c" + "The new mute-time would be less than zero!");
                        return;
                    }
                    muteProfile.setDuration(newDuration);
                    BungeeCore.getAPI().getMuteService().saveEntity(muteProfile, isOnline, true);
                    String timeLeft = TimeUtil.beautifyTime(muteProfile.getMillisLeft(), TimeUnit.MILLISECONDS);
                    sender.sendMessage(Message.PUNISH_PREFIX + "§7" + ("You changed the mute-time of §e" + (target) + "§7 to §c" + (timeLeft) + "§7!"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            printUsage(sender, author);
        }
    }

    public void printUsage(CommandSender commandSender, UUID author) {
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7Format §8» §e1s, 2m, 3h, 4d, 5w");
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7/punishreduce (" + "name" + ") (ban | mute) (format)");
    }

}
