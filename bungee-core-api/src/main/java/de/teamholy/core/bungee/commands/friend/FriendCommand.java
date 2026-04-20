package de.teamholy.core.bungee.commands.friend;

import de.teamholy.core.api.constants.Message;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ChatColor;
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
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(
                ChatColor.RED).create());
            return;
        }
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
                        proxiedPlayer.sendMessage(new ComponentBuilder(" §8» " + nameColor + " §8× ").append("§a§l" + "ACCEPT").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + getName(uuid)))
                            .append(" ").append("§c§l" + "DENY").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + getName(uuid)))
                            .create());
                    });
                    if (friendProfile.getFriendReqeustsList().size() == 0) {
                        proxiedPlayer.sendMessage("§c-/-");
                    }
                    proxiedPlayer.sendMessage(prefix + ("Friend request list " + ("§a" + friendProfile.getFriendReqeustsList().size())));
                });
            } else {
                sendHelp(proxiedPlayer);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);

                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + "This player does not exist");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().canAddFriendSize(proxiedPlayer.getUniqueId())) {
                    proxiedPlayer.sendMessage(prefix + "§7" + ("You reached the §cmax §7friends " + ("§4" + BungeeCore.getAPI().getFriendManager().getMaxFriendsCount(proxiedPlayer.getUniqueId()))));
                    return;
                }

                if (BungeeCore.getAPI().getFriendManager().isFriend(proxiedPlayer.getUniqueId(), target)) {
                    proxiedPlayer.sendMessage(prefix + ("You are already friends with " + (getColor(target) + getName(target) + "§7")));
                    return;
                }

                if (target.toString().equals(proxiedPlayer.getUniqueId().toString())) {
                    proxiedPlayer.sendMessage(prefix + "You can't add yourself!");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriendRequestAllowed(target)) {
                    proxiedPlayer.sendMessage(prefix + ("The player " + (getColor(target) + getName(target) + "§7") + " §7disabled his friend requests"));
                    return;
                }

                if (BungeeCore.getAPI().getFriendManager().isFriendRequest(proxiedPlayer.getUniqueId(), target)) {
                    proxiedPlayer.sendMessage(prefix + ("You already send " + (getColor(target) + getName(target) + "§7") + " §7a friend request"));
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().canAddFriendSize(target)) {
                    proxiedPlayer.sendMessage(prefix + ("The player " + (getColor(target) + getName(target) + "§7") + " §7has reached the §cmax §7friends"));
                    return;
                }

                if (BungeeCore.getAPI().getFriendManager().isFriendRequest(target, proxiedPlayer.getUniqueId())) {
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(proxiedPlayer, "friend accept " + getName(target));
                    return;
                }

                ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(target);
                boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

                proxiedPlayer.sendMessage(prefix + ("You sent a friend request to " + (getColor(target) + getName(target) + "§7")));
                BungeeCore.getAPI().getFriendManager().sendFriendRequest(proxiedPlayer.getUniqueId(), target, isOnline);

                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage("§8§m-----------§f§lFRIEND§8§m------------");
                    targetPlayer.sendMessage("§7" + ("You got an friend request from " + (getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId()))));
                    targetPlayer.sendMessage(new ComponentBuilder("          ").append("§a§l" + "ACCEPT").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + proxiedPlayer.getName()))
                        .append("       ").append("§c§l" + "DENY").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + proxiedPlayer.getName()))
                        .create());
                    targetPlayer.sendMessage("§8§m-----------------------------");
                }


            } else if (args[0].equalsIgnoreCase("remove")) {

                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);
                BungeeCore.getAPI().getUuidManager().getUUID(args[1]);
                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + "This player does not exist");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriend(proxiedPlayer.getUniqueId(), target)) {
                    proxiedPlayer.sendMessage(prefix + ("You are not friends with " + (getColor(target) + getName(target) + "§7")));
                    return;
                }

                ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(target);
                boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

                BungeeCore.getAPI().getFriendManager().removeFriend(proxiedPlayer.getUniqueId(), target, isOnline);
                BungeeCore.getAPI().getFriendManager().removeFriend(target, proxiedPlayer.getUniqueId(), true);

                proxiedPlayer.sendMessage(prefix + ("You removed " + (getColor(target) + getName(target) + "§7") + " as your friend"));
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage(prefix + ("Your friendship with " + (getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId()) + "§7") + " has been dissolved!"));
                }

            } else if (args[0].equalsIgnoreCase("accept")) {

                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);

                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + "This player does not exist");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriendRequest(target, proxiedPlayer.getUniqueId())) {
                    proxiedPlayer.sendMessage(prefix + ("The player " + (getColor(target) + getName(target) + "§7") + " didn't send you a friend request"));
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().canAddFriendSize(target)) {
                    proxiedPlayer.sendMessage(prefix + ("The player " + (getColor(target) + getName(target) + "§7") + " has reached the §cmax §7friends"));
                    return;
                }

                ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(target);
                boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

                BungeeCore.getAPI().getFriendManager().addFriend(proxiedPlayer.getUniqueId(), target, isOnline);
                BungeeCore.getAPI().getFriendManager().removeFriendRequest(proxiedPlayer.getUniqueId(), target, isOnline);


                BungeeCore.getAPI().getFriendManager().addFriend(target, proxiedPlayer.getUniqueId(), true);
                BungeeCore.getAPI().getFriendManager().removeFriendRequest(target, proxiedPlayer.getUniqueId(), true);

                proxiedPlayer.sendMessage(prefix + ("You accepted the friend request from " + (getColor(target) + getName(target) + "§7")));
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage(prefix + ("You are now friends with " + (getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId()) + "§7")));
                }


            } else if (args[0].equalsIgnoreCase("deny")) {
                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);

                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + "This player does not exist");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriendRequest(target, proxiedPlayer.getUniqueId())) {
                    proxiedPlayer.sendMessage(prefix + ("The player " + (getColor(target) + getName(target) + "§7") + " didn't send you a friend requests"));
                    return;
                }

                BungeeCore.getAPI().getFriendManager().removeFriendRequest(target, proxiedPlayer.getUniqueId(), true);
                proxiedPlayer.sendMessage(prefix + ("You denied the friend request of " + (getColor(target) + getName(target) + "§7")));

                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage(prefix + ("The player " + (getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId()) + "§7") + " has denied your friend request!"));
                }
            } else if (args[0].equalsIgnoreCase("jump")) {
                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);

                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + "This player does not exist");
                    return;
                }

                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);
                if (targetPlayer == null) {
                    proxiedPlayer.sendMessage(prefix + "Player not online");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriend(proxiedPlayer.getUniqueId(), target)) {
                    proxiedPlayer.sendMessage(prefix + ("You are not friends with " + (getColor(target) + getName(target) + "§7")));
                    return;
                }


                if (!BungeeCore.getAPI().getFriendManager().canJump(target)) {
                    proxiedPlayer.sendMessage(prefix + ("The player " + (getColor(target) + getName(target) + "§7") + " disabled their friend jump"));
                    return;
                }

                proxiedPlayer.connect(targetPlayer.getServer().getInfo());
                proxiedPlayer.sendMessage(prefix + ("You jumped to " + (getColor(target) + getName(target) + "§7")));
            } else {
                sendHelp(proxiedPlayer);
            }
        }

    }

    private static String getColor(UUID uuid) {
        return BungeeCore.getInstance().getPlayerColor(uuid);
    }

    private static String getName(UUID uuid) {
        return BungeeCore.getAPI().getUuidManager().getName(uuid);
    }


    public static void printFriendList(ProxiedPlayer proxiedPlayer) {
        BungeeCore.getAPI().getFriendService().getEntityAsync(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getFriendService().getRepository().findFirstById(proxiedPlayer.getUniqueId()), friendProfile -> {
            proxiedPlayer.sendMessage(prefix + "Friend list" + " §a" + friendProfile.getFriendList().size() + "§7/§c" + BungeeCore.getAPI().getFriendManager().getMaxFriendsCount(proxiedPlayer.getUniqueId()) + " §8»");
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
                proxiedPlayer.sendMessage("§7... " + "and" + " §a" + (friendProfile.getFriendList().size() - 10) + " §7" + "other");
            }
        });
    }

    private void sendHelp(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage(Message.TOPLINE);
        proxiedPlayer.sendMessage("   " + Message.HELP_TITLE_FRIEND);
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/friend add (" + "name" + ")");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/friend remove (" + "name" + ")");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/friend accept (" + "name" + ")");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/friend deny (" + "name" + ")");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/friend jump (" + "name" + ")");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/friend list");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/friend requests");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/msg (" + "name" + ") (" + "message" + ")");
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage(Message.LINE_DOWN);
    }


    public static void msg(UUID sender, UUID getter, String message) {
        ProxiedPlayer senderPlayer = ProxyServer.getInstance().getPlayer(sender);
        ProxiedPlayer getterPlayer = ProxyServer.getInstance().getPlayer(getter);

        senderPlayer.sendMessage(MSGPREFIX + BungeeCore.getInstance().getPlayerColor(sender) + senderPlayer.getName() + " §7» " + BungeeCore.getInstance().getPlayerColor(getterPlayer.getUniqueId()) + getterPlayer.getName() + " §8» §a" + message);
        getterPlayer.sendMessage(MSGPREFIX + BungeeCore.getInstance().getPlayerColor(sender) + senderPlayer.getName() + " §7» " + BungeeCore.getInstance().getPlayerColor(getterPlayer.getUniqueId()) + getterPlayer.getName() + " §8» §a" + message);

        LASTREPLYS.put(sender, getter);
        LASTREPLYS.put(getter, sender);
    }
}