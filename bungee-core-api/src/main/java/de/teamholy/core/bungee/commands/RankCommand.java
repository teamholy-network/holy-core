package de.teamholy.core.bungee.commands;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.driver.permission.PermissionCheckResult;
import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BungeeUtil;
import lombok.SneakyThrows;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/* copyright by Yassino */
public class RankCommand extends Command {

    private String prefix = "§cRank §8× §7";

    public RankCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }


    @SneakyThrows
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer proxiedPlayer) {
            if (!sender.hasPermission("teamholy.rang")) {
                sendRank(proxiedPlayer);
                return;
            }
        }

        UUID author = BungeeUtil.parseAuthorUUID(sender);

        if (args.length == 4) {
            UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);
            if (uuid == null) {
                sender.sendMessage(prefix + BungeeTranslateAPI.translate(author, "This player does not exist"));
                return;
            }

            IPermissionGroup permissionGroup = CloudNetDriver.getInstance().getPermissionManagement().getGroup(args[2]);
            if (permissionGroup == null) {
                sender.sendMessage(prefix + BungeeTranslateAPI.translate(author, "This group does not exist"));
                return;
            }

            if (!sender.hasPermission("teamholy.rang." + args[2].toLowerCase())) {
                sender.sendMessage(prefix + BungeeTranslateAPI.translate(author, "You cant give away this rank!"));
                return;
            }

            IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);
            if (permissionGroup.getGroups().contains(permissionGroup.getName())) {
                sender.sendMessage(prefix + BungeeTranslateAPI.translate(author, "This player is already in the group"));
                return;
            }

            if (permissionUser.hasPermission("teamholy.team") == PermissionCheckResult.ALLOWED && !sender.hasPermission("*")) {
                sender.sendMessage(prefix + BungeeTranslateAPI.translate(author, "You are not allowed to give this player a rank!"));
                return;
            }


            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
            if (target != null) {
                target.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(target, " You now have the {}§7 rank!", permissionGroup.getDisplay() + permissionGroup.getName()));
                if (permissionGroup.isDefaultGroup() || args[3].equalsIgnoreCase("-1")) {
                    target.sendMessage(prefix + "§8(§4" + BungeeTranslateAPI.translate(target, "Lifetime") + "§8)");
                } else {
                    target.sendMessage(prefix + "§8(§c" + args[3] + " " + BungeeTranslateAPI.translate(target, "days") + "§8)");
                }
            }
            BungeeCore.getAPI().getCloudManager().sendCloudMessage("bukkit", "rank_update", JsonDocument.newDocument("uuid", uuid));

            PlayerRank playerRank = Arrays.stream(PlayerRank.values()).filter(playerRank1 -> playerRank1.getName().equalsIgnoreCase(permissionGroup.getName())).toList().get(0);

            DiscordWebhook discordWebhook = new DiscordWebhook("https://discord.com/api/webhooks/1061719912730603530/zb7iKpRkLfk0Th9EcfTiBhd1LJ5gI5AV99s3n9aIuOQGrCdalU7QsIjj_YXdtgYpE8Jn");
            discordWebhook.setUsername("Rang update");
            if (permissionGroup.isDefaultGroup() || args[3].equalsIgnoreCase("-1")) {
                if (args[0].equalsIgnoreCase("set")) permissionUser.getGroups().clear();
                else if (!args[0].equalsIgnoreCase("add")) {
                    sendHelp(sender);
                    return;
                }
                permissionUser.addGroup(permissionGroup.getName());
                discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(new Color(playerRank.getRed(), playerRank.getGreen(), playerRank.getBlue()))
                    .setDescription(sender.getName() + " hat " + args[1] + " den Rang " + args[2] + " LIFETIME gegeben")
                );
                sender.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(author, "you gave {} the rank {} ", args[1], permissionGroup.getDisplay() + permissionGroup.getName()) + " §8(§4" + BungeeTranslateAPI.translate(author, "Lifetime") + "§8)");
            } else {
                permissionUser.getGroups().clear();
                permissionUser.addGroup(permissionGroup.getName(), Long.parseLong(args[3]), TimeUnit.DAYS);
                discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(new Color(playerRank.getRed(), playerRank.getGreen(), playerRank.getBlue()))
                    .setDescription(sender.getName() + " hat " + args[1] + " den Rang " + args[2] + " für " + args[3] + " Tage gegeben")
                );
                sender.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(author, "you gave {} the rank {} ", args[1], permissionGroup.getDisplay() + permissionGroup.getName()) + " §8(§c" + args[3] + " " + BungeeTranslateAPI.translate(author, "Days") + "§8)");
            }

            CloudNetDriver.getInstance().getPermissionManagement().updateUser(permissionUser);

            BungeeCore.getAPI().getExecutor().submit(() -> {

                if (target != null) {
                    PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(target.getUniqueId(), () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(target.getUniqueId()));
                    playerProfile.setRank(playerRank.toString());
                    BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile, true, true);

                    StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(target.getUniqueId(), () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(target.getUniqueId()));
                    if (staffProfile == null) {
                        staffProfile = new StaffProfile();
                        staffProfile.setPlayerId(target.getUniqueId());
                        staffProfile.setNotify(true);
                        staffProfile.setBanProfileList(new ArrayList<>());
                        staffProfile.setMuteProfileList(new ArrayList<>());
                        staffProfile.setReportList(new ArrayList<>());
                        BungeeCore.getAPI().getStaffService().saveEntity(staffProfile, true, true);
                    }

                }

                discordWebhook.execute();

            });

        } else {
            sendHelp(sender);
        }
    }


    private void sendHelp(CommandSender proxiedPlayer) {
        UUID author = BungeeUtil.parseAuthorUUID(proxiedPlayer);
        proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translate(author, "You can only give these ranks") + ":");
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<IPermissionGroup> permissionGroups = new ArrayList<>(CloudNetDriver.getInstance().getPermissionManagement().getGroups());
        permissionGroups.sort((o1, o2) -> Integer.compare(o2.getSortId(), o1.getSortId()));
        permissionGroups.forEach(group -> {
            if (proxiedPlayer.hasPermission("teamholy.rang." + group.getName())) {
                stringBuilder.append(group.getDisplay() + group.getName() + "§7, ");
            }
        });
        proxiedPlayer.sendMessage(stringBuilder.toString());
        proxiedPlayer.sendMessage(prefix + "/rank set§8/§7add (" + BungeeTranslateAPI.translate(author, "player") + ") (" + BungeeTranslateAPI.translate(author, "rank") + ") (" + BungeeTranslateAPI.translate(author, "time in days") + ", " + BungeeTranslateAPI.translate(author, "Lifetime") + " = -1)");

        if (proxiedPlayer instanceof ProxiedPlayer) {
            sendRank((ProxiedPlayer) proxiedPlayer);
        }
    }


    private void sendRank(ProxiedPlayer proxiedPlayer) {
        UUID author = BungeeUtil.parseAuthorUUID(proxiedPlayer);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        if (proxiedPlayer == null) {
            return;
        }
        CloudNetDriver.getInstance().getPermissionManagement().getUser(proxiedPlayer.getUniqueId()).getGroups().forEach(groupEntityData -> {
            IPermissionGroup permissionGroup = CloudNetDriver.getInstance().getPermissionManagement().getGroup(groupEntityData.getGroup());
            if (groupEntityData.getTimeOutMillis() == 0 || groupEntityData.getTimeOutMillis() == -1) {
                proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(author, "You have the {}§7 rank", permissionGroup.getDisplay() + permissionGroup.getName()) + "§8︳ §7" + BungeeTranslateAPI.translate(author, "End") + " §8» §a" + BungeeTranslateAPI.translate(author, "PERMANENT"));
            } else {
                proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(author, "You have the {}§7 rank", permissionGroup.getDisplay() + permissionGroup.getName()) + "§8︳ §7" + BungeeTranslateAPI.translate(author, "End") + " §8» §e" + simpleDateFormat.format(groupEntityData.getTimeOutMillis()));
            }
        });
    }

}
