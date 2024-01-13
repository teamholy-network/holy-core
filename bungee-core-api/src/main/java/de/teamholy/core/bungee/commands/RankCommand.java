package de.teamholy.core.bungee.commands;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.driver.permission.PermissionCheckResult;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bungee.BungeeCore;
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
    public void execute(CommandSender player, String[] args) {
        if (player instanceof ProxiedPlayer proxiedPlayer) {
            if (!player.hasPermission("teamholy.rang")) {
                sendRank(proxiedPlayer);
                return;
            }
        }

        if (args.length == 4) {
            UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);
            if (uuid == null) {
                player.sendMessage(prefix + "This player does not exist");
                return;
            }

            IPermissionGroup permissionGroup = CloudNetDriver.getInstance().getPermissionManagement().getGroup(args[2]);
            if (permissionGroup == null) {
                player.sendMessage(prefix + "This group does not exist");
                return;
            }

            if (!player.hasPermission("teamholy.rang." + args[2].toLowerCase())) {
                player.sendMessage(prefix + "You cant give away this rank!");
                return;
            }

            IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);
            if (permissionGroup.getGroups().contains(permissionGroup.getName())) {
                player.sendMessage(prefix + "This player is already in the group");
                return;
            }

            if (permissionUser.hasPermission("teamholy.team") == PermissionCheckResult.ALLOWED && !player.hasPermission("*")) {
                player.sendMessage(prefix + "You are not allowed to give this player a rank!");
                return;
            }


            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
            if (target != null) {
                target.sendMessage(prefix + " You now have the " + permissionGroup.getDisplay() + permissionGroup.getName() + " §7rank!");
                if (permissionGroup.isDefaultGroup() || args[3].equalsIgnoreCase("-1")) {
                    target.sendMessage(prefix + "§8(§4Lifetime§8)");
                } else {
                    target.sendMessage(prefix + "§8(§c" + args[3] + " days§8)");
                }
            }
            BungeeCore.getAPI().getCloudManager().sendCloudMessage("bukkit", "rank_update", JsonDocument.newDocument("uuid", uuid));

            PlayerRank playerRank = Arrays.stream(PlayerRank.values()).filter(playerRank1 -> playerRank1.getName().equalsIgnoreCase(permissionGroup.getName())).toList().get(0);

            DiscordWebhook discordWebhook = new DiscordWebhook("https://discord.com/api/webhooks/1061719912730603530/zb7iKpRkLfk0Th9EcfTiBhd1LJ5gI5AV99s3n9aIuOQGrCdalU7QsIjj_YXdtgYpE8Jn");
            discordWebhook.setUsername("Rang update");
            if (permissionGroup.isDefaultGroup() || args[3].equalsIgnoreCase("-1")) {
                if (args[0].equalsIgnoreCase("set")) permissionUser.getGroups().clear();
                else if (!args[0].equalsIgnoreCase("add")) {
                    sendHelp(player);
                    return;
                }
                permissionUser.addGroup(permissionGroup.getName());
                discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(new Color(playerRank.getRed(), playerRank.getGreen(), playerRank.getBlue()))
                    .setDescription(player.getName() + " hat " + args[1] + " den Rang " + args[2] + " LIFETIME gegeben")
                );
                player.sendMessage(prefix + "you gave " + args[1] + " the rank " + permissionGroup.getDisplay() + permissionGroup.getName() + " §8(§4Lifetime§8)");
            } else {
                permissionUser.getGroups().clear();
                permissionUser.addGroup(permissionGroup.getName(), Long.parseLong(args[3]), TimeUnit.DAYS);
                discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(new Color(playerRank.getRed(), playerRank.getGreen(), playerRank.getBlue()))
                    .setDescription(player.getName() + " hat " + args[1] + " den Rang " + args[2] + " für " + args[3] + " Tage gegeben")
                );
                player.sendMessage(prefix + "you gave " + args[1] + " the rank " + permissionGroup.getDisplay() + permissionGroup.getName() + " §8(§c" + args[3] + " Days§8)");
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
            sendHelp(player);
        }
    }


    private void sendHelp(CommandSender proxiedPlayer) {
        proxiedPlayer.sendMessage(prefix + "You can only give these ranks:");
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<IPermissionGroup> permissionGroups = new ArrayList<>(CloudNetDriver.getInstance().getPermissionManagement().getGroups());
        permissionGroups.sort((o1, o2) -> Integer.compare(o2.getSortId(), o1.getSortId()));
        permissionGroups.forEach(group -> {
            if (proxiedPlayer.hasPermission("teamholy.rang." + group.getName())) {
                stringBuilder.append(group.getDisplay() + group.getName() + "§7, ");
            }
        });
        proxiedPlayer.sendMessage(stringBuilder.toString());
        proxiedPlayer.sendMessage(prefix + "/rank set§8/§7add (player) (rank) (time in days, Lifetime = -1)");

        if (proxiedPlayer instanceof ProxiedPlayer) {
            sendRank((ProxiedPlayer) proxiedPlayer);
        }
    }


    private void sendRank(ProxiedPlayer proxiedPlayer) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        if (proxiedPlayer == null) {
            return;
        }
        CloudNetDriver.getInstance().getPermissionManagement().getUser(proxiedPlayer.getUniqueId()).getGroups().forEach(groupEntityData -> {
            IPermissionGroup permissionGroup = CloudNetDriver.getInstance().getPermissionManagement().getGroup(groupEntityData.getGroup());
            if (groupEntityData.getTimeOutMillis() == 0 || groupEntityData.getTimeOutMillis() == -1) {
                proxiedPlayer.sendMessage(prefix + "You have the " + permissionGroup.getDisplay() + permissionGroup.getName() + " §7rank §8︳ §7End §8» §aPERMANENT");
            } else {
                proxiedPlayer.sendMessage(prefix + "You have the " + permissionGroup.getDisplay() + permissionGroup.getName() + " §7rank §8︳ §7End §8» §e" + simpleDateFormat.format(groupEntityData.getTimeOutMillis()));
            }
        });
    }

}
