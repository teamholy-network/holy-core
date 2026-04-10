package de.teamholy.core.bungee.commands.ban;

import de.teamholy.core.translation.BungeeTranslateAPI;
import de.teamholy.core.api.constants.DiscordWebhookLink;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.punishhistory.PunishHistoryProfile;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.SenderCommand;
import de.teamholy.core.bungee.util.BanUtil;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.util.UUID;

/* copyright by Yassino */
public class UnbanCommand extends SenderCommand {


    public UnbanCommand() {
        super(new String[]{"unban"}, "teamholy.unban");
    }

    public void execute(CommandSender sender, String[] args) {
        if (!BungeeUtil.hasPermission(sender, "teamholy.ban")) {
            BungeeUtil.sendNoPermission(sender);
            return;
        }
        if (args.length == 1) {
            BungeeCore.getInstance().getExecutorService().execute(() -> {
                String target = args[0];
                try {
                    UUID uuid = BungeeUtil.parseTargetArgument(target);

                    UUID author = BungeeUtil.parseAuthorUUID(sender);

                    if (uuid == null) {
                        sender.sendMessage(Message.PUNISH_PREFIX + "§7" + BungeeTranslateAPI.translate(author, "Error while fetching UUID from") + " §c" + target + "§c!");
                        return;
                    }


                    UUID finalUuid = uuid;
                    BanProfile punishProfile = BungeeCore.getAPI().getBanService().getEntity(uuid, () -> BungeeCore.getAPI().getBanService().getRepository().findFirstById(finalUuid));

                    if (punishProfile == null) {
                        sender.sendMessage(Message.PUNISH_PREFIX + BungeeTranslateAPI.translatePlaceholder(author, "§cThe player §e{}§c is isn't banned!", target));
                        return;
                    }

                    PunishHistoryProfile punishHistoryProfile = BungeeCore.getAPI().getPunishHistoryService().getEntity(uuid, () -> BungeeCore.getAPI().getPunishHistoryService().getRepository().findFirstById(finalUuid));


                    BungeeCore.getAPI().getBanService().deleteEntity(punishProfile);
                    punishHistoryProfile.getBanProfileMap().put(UUID.randomUUID().toString(), punishProfile);

                    BungeeCore.getAPI().getPunishHistoryService().saveEntity(punishHistoryProfile, false, true);


                    if (sender instanceof ProxiedPlayer player) {
                        // BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(BanUtil.generateUnbanMessage(sender.getName(), player.getUniqueId(), punishProfile));
                        for (ProxiedPlayer staffNotifyPlayer : BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers()) {
                            staffNotifyPlayer.sendMessage(BanUtil.generateUnbanMessage(staffNotifyPlayer, sender.getName(), player.getUniqueId(), punishProfile));
                        }
                    } else {
                        //BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(BanUtil.generateUnbanMessage(sender.getName(), punishProfile));
                        for (ProxiedPlayer staffNotifyPlayer : BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers()) {
                            staffNotifyPlayer.sendMessage(BanUtil.generateUnbanMessage(staffNotifyPlayer, sender.getName(), punishProfile));
                        }
                    }

                    String authorName = BungeeCore.getAPI().getUuidManager().getName(author);

                    DiscordWebhook discordWebhook = new DiscordWebhook(DiscordWebhookLink.BAN_URL);
                    discordWebhook.setUsername("UNBAN");
                    discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(Color.GREEN)
                        .setDescription(authorName + " has unbanned " + target + ".")
                    );

                    discordWebhook.execute();
                } catch (NumberFormatException e) {
                    printUsage(sender);
                }
            });
        } else {
            printUsage(sender);
        }
    }

    public void printUsage(CommandSender commandSender) {
        UUID author = BungeeUtil.parseAuthorUUID(commandSender);
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7/unban (" + BungeeTranslateAPI.translate(author, "name") + ")");
    }

}
