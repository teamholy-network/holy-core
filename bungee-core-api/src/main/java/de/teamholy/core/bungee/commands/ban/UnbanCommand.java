package de.teamholy.core.bungee.commands.ban;

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
            String target = args[0];
            try {
                UUID uuid = BungeeUtil.parseTargetArgument(target);

                if (uuid == null) {
                    sender.sendMessage(Message.PUNISH_PREFIX + "§7Error while fetching UUID from §c" + target + "§c!");
                    return;
                }

                UUID author = BungeeUtil.parseAuthorUUID(sender);

                UUID finalUuid = uuid;
                BanProfile punishProfile = BungeeCore.getAPI().getBanService().getEntity(uuid, () -> BungeeCore.getAPI().getBanService().getRepository().findFirstById(finalUuid));

                if (punishProfile == null) {
                    sender.sendMessage(Message.PUNISH_PREFIX + "§cThe player §e" + target + "§c is isn't banned!");
                    return;
                }

                PunishHistoryProfile punishHistoryProfile = BungeeCore.getAPI().getPunishHistoryService().getEntity(uuid, () -> BungeeCore.getAPI().getPunishHistoryService().getRepository().findFirstById(finalUuid));


                BungeeCore.getAPI().getBanService().deleteEntity(punishProfile);
                punishHistoryProfile.getBanProfileMap().put(UUID.randomUUID().toString(), punishProfile);

                BungeeCore.getAPI().getPunishHistoryService().saveEntity(punishHistoryProfile, false, true);


                BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(BanUtil.generateUnbanMessage(sender.getName(), punishProfile));

                String authorName = BungeeCore.getAPI().getUuidManager().getName(author);

                DiscordWebhook discordWebhook = new DiscordWebhook(DiscordWebhookLink.BAN_URL);
                discordWebhook.setUsername("UNBAN");
                discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(Color.GREEN)
                    .setDescription(authorName + " has unbanned " + target + ".")
                );

                BungeeCore.getAPI().getExecutor().execute(discordWebhook::execute);
            } catch (NumberFormatException e) {
                printUsage(sender);
            }
        } else {
            printUsage(sender);
        }
    }

    public void printUsage(CommandSender commandSender) {
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7/unban (name)");
    }

}
