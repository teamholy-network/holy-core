package de.teamholy.core.bungee.commands.ban;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.constants.DiscordWebhookLink;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.punishhistory.PunishHistoryProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.api.utility.Punish;
import de.teamholy.core.api.utility.TimeUtil;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.SenderCommand;
import de.teamholy.core.bungee.util.BanUtil;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class BanCommand extends SenderCommand {

    public BanCommand() {
        super(new String[]{"ban", "tempban"}, "teamholy.ban");
    }

    public void execute(CommandSender sender, String[] args) {
        if (!BungeeUtil.hasPermission(sender, "teamholy.ban")) {
            BungeeUtil.sendNoPermission(sender);
            return;
        }
        if (args.length == 2 || args.length == 3) {

            BungeeCore.getInstance().getExecutorService().execute(() -> {
                try {
                    executeBanCommand(sender, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else {
            printUsage(sender);
        }
    }

    private void executeBanCommand(CommandSender sender, String[] args) {
        String target = args[0];


        try {
            UUID author = BungeeUtil.parseAuthorUUID(sender);

            UUID uuid = BungeeUtil.parseTargetArgument(target);

            if (uuid == null) {
                UUID nickUUID = BungeeCore.getAPI().getNickManager().getUUIDFromNick(target);
                if (nickUUID == null) {
                    sender.sendMessage(Message.PUNISH_PREFIX + "§7"+ BungeeTranslateAPI.translate(author,"Error while fetching UUID from ")+"§c" + target + "§c!");
                    return;
                }
                uuid = nickUUID;
                target = BungeeCore.getAPI().getUuidManager().getName(uuid);
            }

            int reasonId = Integer.parseInt(args[1]);

            Punish.BanReason banReason = Punish.parseBanReasonById(reasonId);
            if (banReason == null) {
                sender.sendMessage(Message.PUNISH_PREFIX + "§c"+BungeeTranslateAPI.translate(author,"Error while fetching reason with ")+"§eid " + reasonId + "§c!");
                return;
            }

            if (!BungeeUtil.hasPermission(sender, "teamholy.ban.perma") && banReason.getDuration() == -1) {
                sender.sendMessage(Message.PUNISH_PREFIX + "§c"+BungeeTranslateAPI.translate(author,"You don't have permission to use this ban-reason!"));
                return;
            }

            if (!BungeeUtil.hasPermission(sender, "*") && !BungeeCore.getAPI().getCloudManager().isPunishable(uuid)) {
                sender.sendMessage(Message.PUNISH_PREFIX + "§c"+BungeeTranslateAPI.translate(author,"You don't have permissions to ban this player!"));
                return;
            }

            String evidence = "No evidence";
            if (args.length == 3) {
                evidence = args[2];
            }

            UUID finalUuid = uuid;
            BanProfile punishProfile = BungeeCore.getAPI().getBanService().getEntity(uuid, () -> BungeeCore.getAPI().getBanService().getRepository().findFirstById(finalUuid));

            if (punishProfile != null) {
                sender.sendMessage(Message.PUNISH_PREFIX + "§c"+BungeeTranslateAPI.translate(author,"The player")+" §e" + target + "§c "+BungeeTranslateAPI.translate(author,"is already banned!"));
                return;
            }

            long duration = banReason.getDuration();
            PunishHistoryProfile punishHistoryProfile = BungeeCore.getAPI().getPunishHistoryService().getEntity(uuid, () -> BungeeCore.getAPI().getPunishHistoryService().getRepository().findFirstById(finalUuid));

            if (punishHistoryProfile.getBanProfileMap().size() != 0) {
                for (Map.Entry<String, BanProfile> banProfileEntry : punishHistoryProfile.getBanProfileMap().entrySet()) {
                    if (banProfileEntry.getValue().getReason().equalsIgnoreCase(Punish.BanReason.HACKING.getEnglishText()) && banReason == Punish.BanReason.HACKING) {
                        duration = -1;
                        break;
                    }
                }
            }

            punishProfile = new BanProfile();
            punishProfile.setPlayerId(uuid);
            punishProfile.setAuthorId(author);
            punishProfile.setReason(banReason.getEnglishText());
            punishProfile.setDuration(duration);
            punishProfile.setCreateDate(System.currentTimeMillis());
            punishProfile.setEvidence(evidence);

            BungeeCore.getAPI().getBanService().saveEntity(punishProfile, true, true);

            StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(author, () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(author));
            if (staffProfile != null) {
                staffProfile.getBanProfileList().add(punishProfile);
                BungeeCore.getAPI().getStaffService().saveEntity(staffProfile, true, true);
            }


            ProxiedPlayer authorPlayer = ProxyServer.getInstance().getPlayer(author);
            if (authorPlayer != null && BungeeCore.getAPI().getReportManager().getAllReports().values().stream().anyMatch(report -> report.getViewer() != null && report.getViewer().equals(author))) {
                BungeeCore.getInstance().getProxy().getPluginManager().dispatchCommand(authorPlayer, "reports finish");
            }

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(target);
            if (player != null && player.isConnected())
                player.disconnect(BanUtil.generateBanScreen(punishProfile));

            //BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(BanUtil.generateBanMessage(punishProfile));
            for (ProxiedPlayer staffNotifyPlayer : BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers()) {
                staffNotifyPlayer.sendMessage(BanUtil.generateBanMessage(staffNotifyPlayer, punishProfile));
            }

            String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());

            DiscordWebhook discordWebhook = new DiscordWebhook(DiscordWebhookLink.BAN_URL);
            discordWebhook.setUsername("BAN");
            //
            discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(Color.RED)
                .setDescription(authorName + " has banned " + target + " for " + banReason.getEnglishText() + ", Evidence -> " + evidence)
            );

            discordWebhook.execute();
        } catch (NumberFormatException e) {
            printUsage(sender);
        }
    }

    public void printUsage(CommandSender commandSender) {
        UUID author = BungeeUtil.parseAuthorUUID(commandSender);
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7"+BungeeTranslateAPI.translate(author,"Reasons")+" §8» ");
        for (Punish.BanReason reason : Punish.getBanValues()) {
            String time = reason.getDuration() != -1 ? TimeUtil.beautifyTime(reason.getDuration(), TimeUnit.MILLISECONDS) : BungeeTranslateAPI.translate(author,"Permanent");
            commandSender.sendMessage(" §6" + reason.getEnglishText() + " §7- §c" + time + " §7- §c" + reason.getId());
        }
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7/ban ["+BungeeTranslateAPI.translate(author,"name")+"] [id]");
    }

}
