package de.teamholy.core.bungee.commands.mute;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.constants.DiscordWebhookLink;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.mute.MuteProfile;
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
public class UnmuteCommand extends SenderCommand {


    public UnmuteCommand() {
        super(new String[]{"unmute"}, "teamholy.unmute");
    }

    public void execute(CommandSender sender, String[] args) {
        UUID author = BungeeUtil.parseAuthorUUID(sender);
        if (!BungeeUtil.hasPermission(sender, "teamholy.unmute")) {
            BungeeUtil.sendNoPermission(sender);
            return;
        }
        if (args.length == 1) {
            String target = args[0];
            try {
                UUID uuid = BungeeUtil.parseTargetArgument(target);

                if (uuid == null) {
                    sender.sendMessage(Message.PUNISH_PREFIX + "§7"+ BungeeTranslateAPI.translate(author,"Error while fetching UUID from")+" §c" + target + "§c!");
                    return;
                }

                UUID finalUuid = uuid;
                MuteProfile punishProfile = BungeeCore.getAPI().getMuteService().getEntity(uuid, () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(finalUuid));

                if (punishProfile == null) {
                    sender.sendMessage(Message.PUNISH_PREFIX + "§c"+BungeeTranslateAPI.translatePlaceholder(author,"The player {} is isn't banned!", "§e" + target + "§c"));
                    return;
                }

                PunishHistoryProfile punishHistoryProfile = BungeeCore.getAPI().getPunishHistoryService().getEntity(uuid, () -> BungeeCore.getAPI().getPunishHistoryService().getRepository().findFirstById(finalUuid));


                BungeeCore.getAPI().getMuteService().deleteEntity(punishProfile);
                punishHistoryProfile.getMuteProfileMap().put(UUID.randomUUID().toString(), punishProfile);

                BungeeCore.getAPI().getPunishHistoryService().saveEntity(punishHistoryProfile, false, true);


                //BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(BanUtil.generateUnmuteMessage(sender.getName(), punishProfile));
                BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers().forEach(staffmember -> {
                    staffmember.sendMessage(BanUtil.generateUnmuteMessage(staffmember, sender.getName(), punishProfile));
                });

                String authorName = BungeeCore.getAPI().getUuidManager().getName(author);

                DiscordWebhook discordWebhook = new DiscordWebhook(DiscordWebhookLink.BAN_URL);
                discordWebhook.setUsername("UNMUTE");
                discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(Color.GREEN)
                    .setDescription(authorName + " has unmuted " + target + ".")
                );

                BungeeCore.getAPI().getExecutor().execute(discordWebhook::execute);
            } catch (NumberFormatException e) {
                printUsage(sender, author);
            }
        } else {
            printUsage(sender, author);
        }
    }

    public void printUsage(CommandSender commandSender, UUID author) {
        commandSender.sendMessage(Message.PUNISH_PREFIX + "§7/unmute ("+BungeeTranslateAPI.translate(author,"name")+")");
    }
}
