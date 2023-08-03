package de.teamholy.core.bungee.commands.punish;

import de.teamholy.core.api.constants.DiscordWebhookLink;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.mute.MuteProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.api.utility.TimeUtil;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.SenderCommand;
import de.teamholy.core.bungee.util.BanUtil;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.util.UUID;


/* copyright by Yassino */
public class CustomPunishCommand extends SenderCommand {

    public CustomPunishCommand() {
        super(new String[]{"custompunish", "cpunish", "cmute", "cban"}, "teamholy.custompunish");
    }

    public void execute(CommandSender sender, String[] args) {
        if (!BungeeUtil.hasPermission(sender, "teamholy.custompunish")) {
            BungeeUtil.sendNoPermission(sender);
            return;
        }
        if (args.length == 4 || args.length == 5) {
            String target = args[0];


            try {


                UUID uuid = BungeeUtil.parseTargetArgument(target);

                if (uuid == null) {
                    UUID nickUUID = BungeeCore.getAPI().getNickManager().getUUIDFromNick(target);
                    if (nickUUID == null) {
                        sender.sendMessage(Message.PUNISH_PREFIX + "§7Error while fetching UUID from §c" + target + "§c!");
                        return;
                    }
                    uuid = nickUUID;
                    target = BungeeCore.getAPI().getUuidManager().getName(uuid);
                }

                boolean isBan = args[1].equalsIgnoreCase("ban");
                long duration = TimeUtil.getTimeInMilli(args[3]);
                String reason = args[2];


                if (!BungeeUtil.hasPermission(sender, "*") && !BungeeCore.getAPI().getCloudManager().isPunishable(uuid)) {
                    sender.sendMessage(Message.PUNISH_PREFIX + "§cYou don't have permissions to ban/mute this player!");
                    return;
                }

                String evidence = "No evidence";
                if (args.length == 5) {
                    evidence = args[4];
                }


                UUID author = BungeeUtil.parseAuthorUUID(sender);

                UUID finalUuid = uuid;


                ProxiedPlayer authorPlayer = ProxyServer.getInstance().getPlayer(author);
                if (authorPlayer != null && BungeeCore.getAPI().getReportManager().getAllReports().values().stream().anyMatch(report -> report.getViewer() != null && report.getViewer().equals(author))) {
                    authorPlayer.chat("/reports finish");
                }


                if (isBan) {
                    BanProfile punishProfile = BungeeCore.getAPI().getBanService().getEntity(uuid, () -> BungeeCore.getAPI().getBanService().getRepository().findFirstById(finalUuid));

                    if (punishProfile != null) {
                        sender.sendMessage(Message.PUNISH_PREFIX + "§cThe player §e" + target + "§c is already banned!");
                        return;
                    }

                    punishProfile = new BanProfile();
                    punishProfile.setPlayerId(uuid);
                    punishProfile.setAuthorId(author);
                    punishProfile.setReason(reason);
                    punishProfile.setDuration(duration);
                    punishProfile.setCreateDate(System.currentTimeMillis());
                    punishProfile.setEvidence(evidence);

                    BungeeCore.getAPI().getBanService().saveEntity(punishProfile, false, true);

                    StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(author, () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(author));
                    if (staffProfile != null) {
                        staffProfile.getBanProfileList().add(punishProfile);
                        BungeeCore.getAPI().getStaffService().saveEntity(staffProfile, true, true);
                    }


                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(target);
                    if (player != null && player.isConnected())
                        player.disconnect(BanUtil.generateBanScreen(punishProfile));

                    BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(BanUtil.generateCustomBanMessage(punishProfile));

                    String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());

                    DiscordWebhook discordWebhook = new DiscordWebhook(DiscordWebhookLink.BAN_URL);
                    discordWebhook.setUsername("BAN");
                    discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(Color.RED)
                        .setDescription(authorName + " has banned " + target + " for " + reason + ". [CUSTOM]")
                        .setDescription("Evidence -> " + evidence)
                    );

                    BungeeCore.getAPI().getExecutor().execute(discordWebhook::execute);
                } else {
                    MuteProfile punishProfile = BungeeCore.getAPI().getMuteService().getEntity(uuid, () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(finalUuid));

                    if (punishProfile != null) {
                        sender.sendMessage(Message.PUNISH_PREFIX + "§cThe player §e" + target + "§c is already muted!");
                        return;
                    }


                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                    boolean isOnline = player != null && player.isConnected();


                    punishProfile = new MuteProfile();
                    punishProfile.setPlayerId(uuid);
                    punishProfile.setAuthorId(author);
                    punishProfile.setReason(reason);
                    punishProfile.setDuration(duration);
                    punishProfile.setCreateDate(System.currentTimeMillis());
                    punishProfile.setEvidence(evidence);

                    BungeeCore.getAPI().getMuteService().saveEntity(punishProfile, isOnline, true);

                    StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(author, () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(author));
                    staffProfile.getMuteProfileList().add(punishProfile);
                    if (staffProfile != null) {
                        BungeeCore.getAPI().getStaffService().saveEntity(staffProfile, true, true);
                    }


                    BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(BanUtil.generateCustomMuteMessage(punishProfile));

                    String authorName = BungeeCore.getAPI().getUuidManager().getName(punishProfile.getAuthorId());

                    DiscordWebhook discordWebhook = new DiscordWebhook(DiscordWebhookLink.BAN_URL);
                    discordWebhook.setUsername("MUTE");
                    discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(Color.RED)
                        .setDescription(authorName + " has muted " + target + " for " + punishProfile.getReason() + ". [Custom], Evidence -> " + evidence)
                    );

                    BungeeCore.getAPI().getExecutor().execute(discordWebhook::execute);
                }

            } catch (NumberFormatException e) {
                printUsage(sender);
            }
        } else {
            printUsage(sender);
        }
    }

    public void printUsage(CommandSender commandSender) {
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7Format §8» §e1s, 2m, 3h, 4d, 5w, §c-1 §7= §4Perma");
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7/cpunish (name) (ban | mute) (reason) (format) [evidence]");
    }

}
