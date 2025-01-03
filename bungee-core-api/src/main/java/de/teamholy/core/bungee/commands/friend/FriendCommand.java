package de.teamholy.core.bungee.commands.friend;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/* copyright by Yassino */
public class FriendCommand extends Command {

    public static String MSGPREFIX = "§6MSG §8× §7";
    public static final String prefix = "§6Friend §8× §7";


    public static final HashMap<UUID, UUID> LASTREPLYS = new HashMap<>();

    public FriendCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        if (args.length == 0) {
            sendHelp(proxiedPlayer);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                printFriendList(proxiedPlayer);
            } else if (args[0].equalsIgnoreCase("requests")) {


                BungeeCore.getAPI().getFriendService().getEntityAsync(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getFriendService().getRepository().findFirstById(proxiedPlayer.getUniqueId()), friendProfile -> {
                    friendProfile.getFriendReqeustsList().forEach(uuid -> {
                        String nameColor = getColor(uuid) + getName(uuid);
                        proxiedPlayer.sendMessage(new ComponentBuilder(" §8» " + nameColor + " §8× ").append("§a§l" + BungeeTranslateAPI.translate(proxiedPlayer, "ACCEPT")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + getName(uuid)))
                            .append(" ").append("§c§l" + BungeeTranslateAPI.translate(proxiedPlayer, "DENY")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + getName(uuid)))
                            .create());
                    });
                    if (friendProfile.getFriendReqeustsList().size() == 0) {
                        proxiedPlayer.sendMessage("§c-/-");
                    }
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "Friend request list {}", "§a" + friendProfile.getFriendReqeustsList().size()));
                });
            } else {
                sendHelp(proxiedPlayer);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);

                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translate(proxiedPlayer, "This player does not exist"));
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().canAddFriendSize(proxiedPlayer.getUniqueId())) {
                    proxiedPlayer.sendMessage(prefix + "§7" + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "You reached the §cmax §7friends {}", "§4" + BungeeCore.getAPI().getFriendManager().getMaxFriendsCount(proxiedPlayer.getUniqueId())));
                    return;
                }

                if (BungeeCore.getAPI().getFriendManager().isFriend(proxiedPlayer.getUniqueId(), target)) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "You are already friends with {}", getColor(target) + getName(target)));
                    return;
                }

                if (target.toString().equals(proxiedPlayer.getUniqueId().toString())) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translate(proxiedPlayer, "You can't add yourself!"));
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriendRequestAllowed(target)) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "The player {} §7disabled his friend requests", getColor(target) + getName(target)));
                    return;
                }

                if (BungeeCore.getAPI().getFriendManager().isFriendRequest(proxiedPlayer.getUniqueId(), target)) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "You already send {} §7a friend request", getColor(target) + getName(target)));
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().canAddFriendSize(target)) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "The player {} §7has reached the §cmax §7friends", getColor(target) + getName(target)));
                    return;
                }

                if (BungeeCore.getAPI().getFriendManager().isFriendRequest(target, proxiedPlayer.getUniqueId())) {
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(proxiedPlayer, "friend accept " + getName(target));
                    return;
                }

                ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(target);
                boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

                proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "You sent a friend request to {}", getColor(target) + getName(target)));
                BungeeCore.getAPI().getFriendManager().sendFriendRequest(proxiedPlayer.getUniqueId(), target, isOnline);

                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage("§8§m-----------§f§lFRIEND§8§m------------");
                    targetPlayer.sendMessage("§7" + BungeeTranslateAPI.translatePlaceholder(targetPlayer, "You got an friend request from {}", getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId())));
                    targetPlayer.sendMessage(new ComponentBuilder("          ").append("§a§l" + BungeeTranslateAPI.translate(targetPlayer, "ACCEPT")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + proxiedPlayer.getName()))
                        .append("       ").append("§c§l" + BungeeTranslateAPI.translate(targetPlayer, "DENY")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + proxiedPlayer.getName()))
                        .create());
                    targetPlayer.sendMessage("§8§m-----------------------------");
                }


            } else if (args[0].equalsIgnoreCase("remove")) {

                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);
                BungeeCore.getAPI().getUuidManager().getUUID(args[1]);
                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translate(proxiedPlayer, "This player does not exist"));
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriend(proxiedPlayer.getUniqueId(), target)) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "You are not friends with {}", getColor(target) + getName(target)));
                    return;
                }

                ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(target);
                boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

                BungeeCore.getAPI().getFriendManager().removeFriend(proxiedPlayer.getUniqueId(), target, isOnline);
                BungeeCore.getAPI().getFriendManager().removeFriend(target, proxiedPlayer.getUniqueId(), true);

                proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "You removed {} as your friend", getColor(target) + getName(target) + "§7"));
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(targetPlayer, "Your friendship with {} has been dissolved!", getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId()) + "§7"));
                }

            } else if (args[0].equalsIgnoreCase("accept")) {

                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);

                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translate(proxiedPlayer, "This player does not exist"));
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriendRequest(target, proxiedPlayer.getUniqueId())) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "The player {} didn't send you a friend request", getColor(target) + getName(target) + "§7"));
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().canAddFriendSize(target)) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "The player {} has reached the §cmax §7friends", getColor(target) + getName(target) + "§7"));
                    return;
                }

                ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(target);
                boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

                BungeeCore.getAPI().getFriendManager().addFriend(proxiedPlayer.getUniqueId(), target, isOnline);
                BungeeCore.getAPI().getFriendManager().removeFriendRequest(proxiedPlayer.getUniqueId(), target, isOnline);


                BungeeCore.getAPI().getFriendManager().addFriend(target, proxiedPlayer.getUniqueId(), true);
                BungeeCore.getAPI().getFriendManager().removeFriendRequest(target, proxiedPlayer.getUniqueId(), true);

                proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "You accepted the friend request from {}", getColor(target) + getName(target)));
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "You are now friends with {}", getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId())));
                }


            } else if (args[0].equalsIgnoreCase("deny")) {
                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);

                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translate(proxiedPlayer, "This player does not exist"));
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriendRequest(target, proxiedPlayer.getUniqueId())) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "The player {} didn't send you a friend requests", getColor(target) + getName(target) + "§7"));
                    return;
                }

                BungeeCore.getAPI().getFriendManager().removeFriendRequest(target, proxiedPlayer.getUniqueId(), true);
                proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "You denied the friend request of {}", getColor(target) + getName(target)));

                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "The player {} has denied your friend request!", getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId()) + "§7"));
                }
            } else if (args[0].equalsIgnoreCase("jump")) {
                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);

                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translate(proxiedPlayer, "This player does not exist"));
                    return;
                }

                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);
                if (targetPlayer == null) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translate(proxiedPlayer, "Player not online"));
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriend(proxiedPlayer.getUniqueId(), target)) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "You are not friends with {}", getColor(target) + getName(target)));
                    return;
                }


                if (!BungeeCore.getAPI().getFriendManager().canJump(target)) {
                    proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "The player {} disabled their friend jump", getColor(target) + getName(target) + "§7"));
                    return;
                }

                proxiedPlayer.connect(targetPlayer.getServer().getInfo());
                proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer, "You jumped to {}", getColor(target) + getName(target)));
            } else {
                sendHelp(proxiedPlayer);
            }
        }

    }

    private static String getColor(UUID uuid) {
        return BungeeCore.getAPI().getCloudManager().getColor(uuid);
    }

    private static String getName(UUID uuid) {
        return BungeeCore.getAPI().getUuidManager().getName(uuid);
    }


    public static void printFriendList(ProxiedPlayer proxiedPlayer) {
        BungeeCore.getAPI().getFriendService().getEntityAsync(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getFriendService().getRepository().findFirstById(proxiedPlayer.getUniqueId()), friendProfile -> {
            proxiedPlayer.sendMessage(prefix + BungeeTranslateAPI.translate(proxiedPlayer, "Friend list") + " §a" + friendProfile.getFriendList().size() + "§7/§c" + BungeeCore.getAPI().getFriendManager().getMaxFriendsCount(proxiedPlayer.getUniqueId()) + " §8»");
            StringBuilder online = new StringBuilder();
            ;
            AtomicInteger i = new AtomicInteger();
            for (UUID uuid : friendProfile.getFriendList()) {
                i.getAndIncrement();
                String nameColor = getColor(uuid) + getName(uuid);
                online.append(nameColor + "§7, ");
                if (i.get() == 10) break;
            }
            if (friendProfile.getFriendList().size() == 0) {
                proxiedPlayer.sendMessage("§c-/-");
            } else {
                proxiedPlayer.sendMessage(online.toString());
                proxiedPlayer.sendMessage("§7... " + BungeeTranslateAPI.translate(proxiedPlayer, "and") + " §a" + (friendProfile.getFriendList().size() - 10) + " §7" + BungeeTranslateAPI.translate(proxiedPlayer, "other"));
            }
        });
    }

    private void sendHelp(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage("§8§m----------§f§lFRIEND§8§m-------------");
        proxiedPlayer.sendMessage(prefix + "/friend add (" + BungeeTranslateAPI.translate(proxiedPlayer, "player") + ")");
        proxiedPlayer.sendMessage(prefix + "/friend remove (" + BungeeTranslateAPI.translate(proxiedPlayer, "player") + ")");
        proxiedPlayer.sendMessage(prefix + "/friend accept (" + BungeeTranslateAPI.translate(proxiedPlayer, "player") + ")");
        proxiedPlayer.sendMessage(prefix + "/friend deny (" + BungeeTranslateAPI.translate(proxiedPlayer, "player") + ")");
        proxiedPlayer.sendMessage(prefix + "/friend jump (" + BungeeTranslateAPI.translate(proxiedPlayer, "player") + ")");
        proxiedPlayer.sendMessage(prefix + "/friend list");
        proxiedPlayer.sendMessage(prefix + "/friend requests");
        proxiedPlayer.sendMessage(prefix + "/msg (" + BungeeTranslateAPI.translate(proxiedPlayer, "player") + ")");
        proxiedPlayer.sendMessage("§8§m-----------------------------");
    }


    public static void msg(UUID sender, UUID getter, String message) {
        ProxiedPlayer senderPlayer = ProxyServer.getInstance().getPlayer(sender);
        ProxiedPlayer getterPlayer = ProxyServer.getInstance().getPlayer(getter);

        senderPlayer.sendMessage(MSGPREFIX + BungeeCore.getAPI().getCloudManager().getColor(sender) + senderPlayer.getName() + " §7» " + BungeeCore.getAPI().getCloudManager().getColor(getterPlayer.getUniqueId()) + getterPlayer.getName() + " §8» §a" + message);
        getterPlayer.sendMessage(MSGPREFIX + BungeeCore.getAPI().getCloudManager().getColor(sender) + senderPlayer.getName() + " §7» " + BungeeCore.getAPI().getCloudManager().getColor(getterPlayer.getUniqueId()) + getterPlayer.getName() + " §8» §a" + message);

        LASTREPLYS.put(sender, getter);
        LASTREPLYS.put(getter, sender);
    }
}