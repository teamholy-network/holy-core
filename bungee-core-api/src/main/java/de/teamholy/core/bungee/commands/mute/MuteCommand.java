package de.teamholy.core.bungee.commands.mute;

//import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.constants.DiscordWebhookLink;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.mute.MuteProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.api.utility.Punish;
import de.teamholy.core.api.utility.Report;
import de.teamholy.core.api.utility.TimeUtil;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.SenderCommand;
import de.teamholy.core.bungee.model.ChatLog;
import de.teamholy.core.bungee.util.BanUtil;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class MuteCommand extends SenderCommand {

    public MuteCommand() {
        super(new String[]{"mute", "tempmute"}, "teamholy.mute");
    }

    public void execute(CommandSender sender, String[] args) {
        UUID author = BungeeUtil.parseAuthorUUID(sender);
        if (!BungeeUtil.hasPermission(sender, "teamholy.mute")) {
            BungeeUtil.sendNoPermission(sender);
            return;
        }
        if (args.length == 2 || args.length == 3) {

            BungeeCore.getInstance().getExecutorService().execute(() -> {
                try {
                    executeMuteCommand(sender, args, author);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else {
            printUsage(sender, author);
        }
    }

    private void executeMuteCommand(CommandSender sender, String[] args, UUID author) {
        String target = args[0];

        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);
        ProxiedPlayer senderPlayer = sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null;


        try {


            UUID uuid = BungeeUtil.parseTargetArgument(target);

            if (uuid == null) {
                UUID nickUUID = BungeeCore.getAPI().getNickManager().getUUIDFromNick(target);
                if (nickUUID == null) {
                    sender.sendMessage(Message.PUNISH_PREFIX + "§7" + "Error while fetching UUID from" + " §c" + target + "§c!");
                    return;
                }
                uuid = nickUUID;
                target = BungeeCore.getAPI().getUuidManager().getName(uuid);
            }

            int reasonId = Integer.parseInt(args[1]);

            Punish.MuteReason banReason = Punish.parseMuteReasonById(reasonId);
            if (banReason == null) {
                sender.sendMessage(Message.PUNISH_PREFIX + "§c" + BungeeUtil.format("Error while fetching reason with id {}", "§e" + reasonId) + "§c!");
                return;
            }

            if (!BungeeUtil.hasPermission(sender, "teamholy.mute.perma") && banReason.getDuration() == -1) {
                sender.sendMessage(Message.PUNISH_PREFIX + "§c" + "You don't have permission to use this mute-reason!");
                return;
            }

            if (!BungeeUtil.hasPermission(sender, "*") && !BungeeCore.getAPI().getCloudManager().isPunishable(uuid)) {
                sender.sendMessage(Message.PUNISH_PREFIX + "§c" + "You don't have permissions to ban this player!");
                return;
            }

            String evidence = "no evidence";
            if (args.length == 3) {
                evidence = args[2];
            } else if (targetPlayer != null) {
                ChatLog chatLog = BungeeCore.getInstance().getChatLogManager().createChatlog((senderPlayer != null ? senderPlayer.getUniqueId() : UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670")), targetPlayer);
                evidence = (chatLog != null) ? "https://teamholy.de/chatlog/" + chatLog.getChatLogId() : "No evidence";
            }

            UUID finalUuid = uuid;
            MuteProfile punishProfile = BungeeCore.getAPI().getMuteService().getEntity(uuid, () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(finalUuid));

            if (punishProfile != null) {
                sender.sendMessage(Message.PUNISH_PREFIX + "§c" + BungeeUtil.format("The player {} is already muted!", "§e" + target + "§c"));
                return;
            }

            long duration = banReason.getDuration();


            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            boolean isOnline = player != null && player.isConnected();


            punishProfile = new MuteProfile();
            punishProfile.setPlayerId(uuid);
            punishProfile.setAuthorId(author);
            punishProfile.setReason(banReason.getEnglishText());
            punishProfile.setDuration(duration);
            punishProfile.setCreateDate(System.currentTimeMillis());
            punishProfile.setEvidence(evidence);


            StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(author, () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(author));
            if (staffProfile != null) {
                staffProfile.getMuteProfileList().add(punishProfile);
                BungeeCore.getAPI().getStaffService().saveEntity(staffProfile, true, true);
            }


            ProxiedPlayer authorPlayer = ProxyServer.getInstance().getPlayer(author);
            if (authorPlayer != null && BungeeCore.getAPI().getReportManager().getAllReports().values().stream().anyMatch(report -> report.getViewer() != null && report.getViewer().equals(author))) {
                if (evidence.equalsIgnoreCase("No evidence")) {
                    Report report = BungeeCore.getAPI().getReportManager().getReport(uuid);
                    if (report.getChatlogID() != null)
                        punishProfile.setEvidence("https:/teamholy.de/chatlog/" + report.getChatlogID());
                }
                BungeeCore.getInstance().getProxy().getPluginManager().dispatchCommand(authorPlayer, "reports finish");
            }


            BungeeCore.getAPI().getMuteService().saveEntity(punishProfile, isOnline, true);
            //BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(BanUtil.generateMuteMessage(punishProfile));
            for (ProxiedPlayer staffNotifyPlayer : BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers()) {
                staffNotifyPlayer.sendMessage(BanUtil.generateMuteMessage(staffNotifyPlayer, punishProfile));
            }

            String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());

            DiscordWebhook discordWebhook = new DiscordWebhook(DiscordWebhookLink.BAN_URL);
            discordWebhook.setUsername("MUTE");
            discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(Color.RED)
                .setDescription(authorName + " has muted " + target + " for " + banReason.getEnglishText() + ", Evidence -> " + evidence)
            );

            discordWebhook.execute();
        } catch (NumberFormatException e) {
            printUsage(sender, author);
        }
    }

    public void printUsage(CommandSender commandSender, UUID author) {
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7" + "Reasons" + " §8» ");
        for (Punish.MuteReason reason : Punish.getMuteValues()) {
            String time = reason.getDuration() != -1 ? TimeUtil.beautifyTime(reason.getDuration(), TimeUnit.MILLISECONDS) : "Permanent";
            commandSender.sendMessage(" §6" + reason.getEnglishText() + " §7- §c" + time + " §7- §c" + reason.getId());
        }
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7/mute (" + "name" + ") (id)");
    }

}
