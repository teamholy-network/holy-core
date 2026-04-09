package de.teamholy.core.bungee.commands;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
//import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BungeeUtil;
import lombok.SneakyThrows;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.util.Tristate;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/* copyright by Yassino */
public class RankCommand extends Command {

    private String prefix = "§cRank §8× §7";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

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
            if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("set")) {
                sendHelp(sender);
                return;
            }

            if (!isNumber(args[3])) {
                sendHelp(sender);
                return;
            }

            UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);
            if (uuid == null) {
                sender.sendMessage(prefix + "This player does not exist");
                return;
            }

            Optional<Group> group = BungeeCore.getAPI().getRankManager().getGroupByDisplayName(args[2]);
            if (!group.isPresent()) {
                sender.sendMessage(prefix + "This group does not exist");
                return;
            }

            if (!sender.hasPermission("teamholy.rang." + args[2].toLowerCase())) {
                sender.sendMessage(prefix + "You cant give away this rank!");
                return;
            }

            CompletableFuture<User> userFuture = BungeeCore.getAPI().getRankManager().getUser(uuid);
            userFuture.whenCompleteAsync((user, userThrowable) -> {
                if (userThrowable != null) {
                    sender.sendMessage(prefix + "Failed to set rank");
                    return;
                }

                if (user.getCachedData().getPermissionData().checkPermission("teamholy.team") == Tristate.TRUE
                        && !sender.hasPermission("*")) {
                    sender.sendMessage(prefix
                            + "You are not allowed to give this player a rank!");
                    return;
                }

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                boolean lifetime = group.get().getName().equals("default") || args[3].equalsIgnoreCase("-1");
                if (target != null) {
                    target.sendMessage(prefix + BungeeUtil.format(" You now have the {}§7 rank!", getRankDisplay(group.get())));
                    if (lifetime) {
                        target.sendMessage(prefix + "§8(§4" + "Lifetime" + "§8)");
                    } else {
                        target.sendMessage(prefix + "§8(§c" + args[3] + " "
                                + "days" + "§8)");
                    }
                }

                AtomicReference<String> hexColor = new AtomicReference<>(
                        group.get().getCachedData().getMetaData().getMetaValue("hexColor"));
                if (hexColor.get() == null) {
                    hexColor.set("#FFFFFF");
                }

                PlayerRank playerRank = PlayerRank.PLAYER;
                if (args[0].equalsIgnoreCase("add")) {
                    Optional<InheritanceNode> currentRankNode = BungeeCore.getAPI().getRankManager()
                            .getPrimaryGroup(user);
                    Group currentGroup = currentRankNode
                            .map(node -> BungeeCore.getAPI().getRankManager().getGroup(node.getGroupName()))
                            .orElse(group.get());
                    if (currentGroup == null) {
                        currentGroup = group.get();
                    }
                    Group highestGroup = currentGroup.getWeight().orElse(0) > group.get().getWeight().orElse(0)
                            ? currentGroup
                            : group.get();
                    playerRank = Arrays.stream(PlayerRank.values())
                            .filter(rank -> rank.getName().equalsIgnoreCase(highestGroup.getName()))
                            .findFirst().orElse(PlayerRank.PLAYER);
                } else if (args[0].equalsIgnoreCase("set")) {
                    playerRank = Arrays.stream(PlayerRank.values())
                            .filter(rank -> rank.getName().equalsIgnoreCase(group.get().getName())).findFirst()
                            .orElse(PlayerRank.PLAYER);
                }
                if (target != null) {
                    PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(target.getUniqueId(),
                            () -> BungeeCore.getAPI().getPlayerService().getRepository()
                                    .findFirstById(target.getUniqueId()));
                    playerProfile.setRank(playerRank.toString());
                    BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile, true, true);

                    StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(target.getUniqueId(),
                            () -> BungeeCore.getAPI().getStaffService().getRepository()
                                    .findFirstById(target.getUniqueId()));
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
                BungeeCore.getAPI().getCloudManager().sendCloudMessage("bukkit", "rank_update",
                        JsonDocument.newDocument("uuid", uuid));

                Integer expiryDuration = Integer.parseInt(args[3]);
                String groupName = group.get().getDisplayName() == null ? group.get().getName()
                        : group.get().getDisplayName();
                if (args[0].equalsIgnoreCase("set")) {
                    BungeeCore.getAPI().getRankManager().setGroup(user, group.get(), expiryDuration)
                            .whenCompleteAsync((result, throwable) -> {
                                if (throwable != null) {
                                    sender.sendMessage(
                                            prefix + "Failed to set rank");
                                    return;
                                }
                                sendRankUpdateResult(result, sender, args[1], group.get(), expiryDuration, lifetime);
                                if (result) {
                                    try {
                                        sendWebhook(lifetime, sender.getName(), args[1], groupName, hexColor.get(),
                                                args[3]);
                                    } catch (Exception e) {
                                        ProxyServer.getInstance().getLogger()
                                                .severe("Failed to send webhook: " + e.getMessage());
                                    }
                                }
                            });
                } else if (args[0].equalsIgnoreCase("add")) {
                    BungeeCore.getAPI().getRankManager().addGroup(user, group.get(), expiryDuration)
                            .whenCompleteAsync((result, throwable) -> {
                                if (throwable != null) {
                                    sender.sendMessage(
                                            prefix + "Failed to add rank");
                                    return;
                                }
                                sendRankUpdateResult(result, sender, args[1], group.get(), expiryDuration, lifetime);
                                if (result) {
                                    try {
                                        sendWebhook(lifetime, sender.getName(), args[1], groupName, hexColor.get(),
                                                args[3]);
                                    } catch (Exception e) {
                                        ProxyServer.getInstance().getLogger()
                                                .severe("Failed to send webhook: " + e.getMessage());
                                    }
                                }
                            });
                }

            });
        } else {
            sendHelp(sender);
        }
    }

    private void sendHelp(CommandSender proxiedPlayer) {
        UUID author = BungeeUtil.parseAuthorUUID(proxiedPlayer);
        proxiedPlayer.sendMessage(prefix + "You can only give these ranks" + ":");
        StringBuilder stringBuilder = new StringBuilder();
        List<Group> ranks = BungeeCore.getAPI().getRankManager().getRanks();
        ranks.sort((o1, o2) -> Integer.compare(
                BungeeCore.getAPI().getRankManager().getPrefixNode(o2.getName()).getPriority(),
                BungeeCore.getAPI().getRankManager().getPrefixNode(o1.getName()).getPriority()));
        ranks.forEach(group -> {
            if (proxiedPlayer.hasPermission("teamholy.rang." + group.getName())) {
                stringBuilder.append(getRankDisplay(group) + "§7, ");
            }
        });
        proxiedPlayer.sendMessage(stringBuilder.toString());
        proxiedPlayer.sendMessage(prefix + "/rank set§8/§7add (" + "player"
                + ") (" + "rank" + ") ("
                + "time in days" + ", "
                + "Lifetime" + " = -1)");

        if (proxiedPlayer instanceof ProxiedPlayer) {
            sendRank((ProxiedPlayer) proxiedPlayer);
        }
    }

    private void sendRank(ProxiedPlayer proxiedPlayer) {
        UUID author = BungeeUtil.parseAuthorUUID(proxiedPlayer);
        if (proxiedPlayer == null) {
            return;
        }
        BungeeCore.getAPI().getRankManager().getRanks(proxiedPlayer.getUniqueId()).thenAccept(optional -> {
            if (optional.isPresent()) {
                optional.get().forEach(inheritanceNode -> sendRank(proxiedPlayer, inheritanceNode));
            } else {
                proxiedPlayer.sendMessage(prefix + "You have no ranks");
            }
        });
    }

    private void sendRank(ProxiedPlayer proxiedPlayer, InheritanceNode inheritanceNode) {
        Group group = BungeeCore.getAPI().getRankManager().getGroup(inheritanceNode.getGroupName());
        if (group == null) {
            return;
        }
        String expiryDuration = "";
        if (inheritanceNode.getExpiryDuration() == null) {
            expiryDuration = "§a" + "PERMANENT";
        } else if (inheritanceNode.getExpiryDuration().toMillis() > 0) {
            expiryDuration = "§e" + simpleDateFormat.format(inheritanceNode.getExpiry().toEpochMilli());
        } else {
            expiryDuration = "§c" + "EXPIRED";
        }
        proxiedPlayer.sendMessage(prefix
                + BungeeUtil.format("You have the {}§7 rank", getRankDisplay(group))
                + "§8︳ §7" + "End" + " §8» " + expiryDuration);
    }

    private String getRankDisplay(Group group) {
        return BungeeCore.getAPI().getRankManager().getRankDisplay(group);
    }

    private void sendWebhook(boolean lifetime, String sender, String target, String rank, String hex, String time) {
        DiscordWebhook discordWebhook = new DiscordWebhook(
                "https://discord.com/api/webhooks/1061719912730603530/zb7iKpRkLfk0Th9EcfTiBhd1LJ5gI5AV99s3n9aIuOQGrCdalU7QsIjj_YXdtgYpE8Jn");
        discordWebhook.setUsername("Rang update");
        if (lifetime) {
            discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(parseHexColor(hex))
                    .setDescription(sender + " hat " + target + " den Rang " + rank + " LIFETIME gegeben"));
        } else {
            discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setColor(parseHexColor(hex))
                    .setDescription(
                            sender + " hat " + target + " den Rang " + rank + " für " + time + " Tage gegeben"));
        }
        discordWebhook.execute();
    }

    private Color parseHexColor(String hex) {
        try {
            return Color.decode(hex.startsWith("#") ? hex : "#" + hex);
        } catch (NumberFormatException e) {
            return Color.WHITE;
        }
    }

    private void sendRankUpdateResult(boolean result, CommandSender sender, String target, Group group,
            Integer expiryDuration, boolean lifetime) {
        UUID author = BungeeUtil.parseAuthorUUID(sender);
        if (result) {
            if (lifetime) {
                sender.sendMessage(prefix
                        + BungeeUtil.format("you gave {} the rank {} ", target, getRankDisplay(group))
                        + " §8(§4" + "Lifetime" + "§8)");
            } else {
                sender.sendMessage(prefix
                        + BungeeUtil.format("you gave {} the rank {} ", target, getRankDisplay(group))
                        + " §8(§c" + expiryDuration + " " + "Days" + "§8)");
            }
        } else {
            sender.sendMessage(prefix + "§c" + "Failed to set rank");
        }
    }

    private boolean isNumber(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
