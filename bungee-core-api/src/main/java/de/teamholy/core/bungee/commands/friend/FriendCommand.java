package de.teamholy.core.bungee.commands.friend;

import de.teamholy.core.api.manager.FriendManager;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
                printFriendList(proxiedPlayer, 1);
            } else if (args[0].equalsIgnoreCase("requests")) {


                BungeeCore.getAPI().getFriendService().getEntityAsync(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getFriendService().getRepository().findFirstById(proxiedPlayer.getUniqueId()), friendProfile -> {
                    friendProfile.getFriendReqeustsList().forEach(uuid -> {
                        String nameColor = getColor(uuid) + getName(uuid);
                        proxiedPlayer.sendMessage(new ComponentBuilder(" §8» " + nameColor + " §8× ").append("§a§lACCEPT").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + getName(uuid)))
                            .append(" ").append("§c§lDENY").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + getName(uuid)))
                            .create());
                    });
                    if (friendProfile.getFriendReqeustsList().isEmpty()) {
                        proxiedPlayer.sendMessage("§c-/-");
                    }
                    proxiedPlayer.sendMessage(prefix + "Friend request list §a" + friendProfile.getFriendReqeustsList().size());
                });
            } else {
                sendHelp(proxiedPlayer);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
                int page;

                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    proxiedPlayer.sendMessage(prefix + "§cPlease enter a valid number");
                    return;
                }
                if (page < 1) page = 1;
                printFriendList(proxiedPlayer, page);
            } else if (args[0].equalsIgnoreCase("add")) {
                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);

                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + "This player does not exist");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().canAddFriendSize(proxiedPlayer.getUniqueId())) {
                    proxiedPlayer.sendMessage(prefix + "§7You reached the §cmax §7friends §4" + BungeeCore.getAPI().getFriendManager().getMaxFriendsCount(proxiedPlayer.getUniqueId()));
                    return;
                }

                if (BungeeCore.getAPI().getFriendManager().isFriend(proxiedPlayer.getUniqueId(), target)) {
                    proxiedPlayer.sendMessage(prefix + "You are already friends with " + getColor(target) + getName(target));
                    return;
                }

                if (target.toString().equals(proxiedPlayer.getUniqueId().toString())) {
                    proxiedPlayer.sendMessage(prefix + "You can't add yourself!");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriendRequestAllowed(target)) {
                    proxiedPlayer.sendMessage(prefix + "The player " + getColor(target) + getName(target) + " §7disabled his friend requests");
                    return;
                }

                if (BungeeCore.getAPI().getFriendManager().isFriendRequest(proxiedPlayer.getUniqueId(), target)) {
                    proxiedPlayer.sendMessage(prefix + "You already send " + getColor(target) + getName(target) + " §7a friend request");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().canAddFriendSize(target)) {
                    proxiedPlayer.sendMessage(prefix + "The player " + getColor(target) + getName(target) + " §7has reached the §cmax §7friends");
                    return;
                }

                if (BungeeCore.getAPI().getFriendManager().isFriendRequest(target, proxiedPlayer.getUniqueId())) {
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(proxiedPlayer, "friend accept " + getName(target));
                    return;
                }

                ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(target);
                boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

                proxiedPlayer.sendMessage(prefix + "You send a friend request to " + getColor(target) + getName(target));
                BungeeCore.getAPI().getFriendManager().sendFriendRequest(proxiedPlayer.getUniqueId(), target, isOnline);

                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage("§8§m-----------§f§lFRIEND§8§m------------");
                    targetPlayer.sendMessage("§7You got an friend request from " + getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId()));
                    targetPlayer.sendMessage(new ComponentBuilder("          ").append("§a§lACCEPT").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + proxiedPlayer.getName()))
                        .append("       ").append("§c§lDENY").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + proxiedPlayer.getName()))
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
                    proxiedPlayer.sendMessage(prefix + "You are not friends with " + getColor(target) + getName(target));
                    return;
                }

                ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(target);
                boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

                BungeeCore.getAPI().getFriendManager().removeFriend(proxiedPlayer.getUniqueId(), target, isOnline);
                BungeeCore.getAPI().getFriendManager().removeFriend(target, proxiedPlayer.getUniqueId(), true);

                proxiedPlayer.sendMessage(prefix + "You removed " + getColor(target) + getName(target) + " §7as your friend");
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage(prefix + "Your friendship with " + getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId()) + " §7has been dissolved!");
                }

            } else if (args[0].equalsIgnoreCase("accept")) {

                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);

                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + "This player does not exist");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriendRequest(target, proxiedPlayer.getUniqueId())) {
                    proxiedPlayer.sendMessage(prefix + "The player " + getColor(target) + getName(target) + " §7didn't send you a friend request");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().canAddFriendSize(target)) {
                    proxiedPlayer.sendMessage(prefix + "The player " + getColor(target) + getName(target) + " §7has reached the §cmax §7friends");
                    return;
                }

                ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(target);
                boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

                BungeeCore.getAPI().getFriendManager().addFriend(proxiedPlayer.getUniqueId(), target, isOnline);
                BungeeCore.getAPI().getFriendManager().removeFriendRequest(proxiedPlayer.getUniqueId(), target, isOnline);


                BungeeCore.getAPI().getFriendManager().addFriend(target, proxiedPlayer.getUniqueId(), true);
                BungeeCore.getAPI().getFriendManager().removeFriendRequest(target, proxiedPlayer.getUniqueId(), true);

                proxiedPlayer.sendMessage(prefix + "You accepted the friend request from " + getColor(target) + getName(target));
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage(prefix + "You are now friends with " + getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId()));
                }


            } else if (args[0].equalsIgnoreCase("deny")) {
                UUID target = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);

                if (target == null) {
                    proxiedPlayer.sendMessage(prefix + "This player does not exist");
                    return;
                }

                if (!BungeeCore.getAPI().getFriendManager().isFriendRequest(target, proxiedPlayer.getUniqueId())) {
                    proxiedPlayer.sendMessage(prefix + "The player " + getColor(target) + getName(target) + " §7didn't send you a friend requests");
                    return;
                }

                BungeeCore.getAPI().getFriendManager().removeFriendRequest(target, proxiedPlayer.getUniqueId(), true);
                proxiedPlayer.sendMessage(prefix + "You denied the friend request of " + getColor(target) + getName(target));

                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage(prefix + "The player " + getColor(proxiedPlayer.getUniqueId()) + getName(proxiedPlayer.getUniqueId()) + " §7has denied your friend request!");
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
                    proxiedPlayer.sendMessage(prefix + "You are not friends with " + getColor(target) + getName(target));
                    return;
                }


                if (!BungeeCore.getAPI().getFriendManager().canJump(target)) {
                    proxiedPlayer.sendMessage(prefix + "The player " + getColor(target) + getName(target) + " §7disabled their friend jump");
                    return;
                }

                proxiedPlayer.connect(targetPlayer.getServer().getInfo());
                proxiedPlayer.sendMessage(prefix + "You jumped to " + getColor(target) + getName(target));
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


    public static void printFriendList(ProxiedPlayer proxiedPlayer, int page) {
        BungeeCore.getAPI().getFriendService().getEntityAsync(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getFriendService().getRepository().findFirstById(proxiedPlayer.getUniqueId()), friendProfile -> {
            if (friendProfile == null) return;

            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(prefix + "Friend list §a" + friendProfile.getFriendList().size() +
                "§7/§c" + BungeeCore.getAPI().getFriendManager().getMaxFriendsCount(proxiedPlayer.getUniqueId()) + " §8(§7Page §e" + page + "§8)"));

            /*
            friendProfile.getFriendList().forEach(friendUUID -> {
                if (friendUUID.equals(proxiedPlayer.getUniqueId())) return;
                FriendManager.FriendEntry friendEntry = FriendManager.FriendEntry.friendCache.get(friendUUID);
            });*/

            ArrayList<FriendManager.Friend> friendArrayList = BungeeCore.getAPI().getFriendManager().getFriendCache(proxiedPlayer.getUniqueId())
                .values()
                .stream()
                .sorted((o1, o2) -> {
                    boolean o1Online = o1.isOnline();
                    boolean o2Online = o2.isOnline();
                    if (o1Online && !o2Online) {
                        return -1;
                    } else if (o1Online == o2Online) {
                        return o1Online ? 0 : Long.compare(o2.getLastJoin(), o1.getLastJoin());
                    } else {
                        return 1;
                    }
                }).collect(Collectors.toCollection(ArrayList::new));


            List<TextComponent> sorted = sortOnlineOffline(friendArrayList, page);
            if (sorted.isEmpty()) {
                proxiedPlayer.sendMessage(TextComponent.fromLegacyText(prefix + "§cThis page is empty!"));
                return;
            }
            sorted.forEach(proxiedPlayer::sendMessage);
        });
    }

    private static String convertTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        String result = "";

        if (days > 0) {
            result += days + "d ";
            hours = hours % 24;
        }
        if (hours > 0) {
            result += hours + "h ";
            minutes = minutes % 60;
        }
        if (minutes > 0 && days == 0) {
            result += minutes + "m";
        }

        return result.trim();
    }


    private static List<TextComponent> sortOnlineOffline(List<FriendManager.Friend> list, int pageNumber) {
        // in the list of the uuids should the online players be first

        list.sort((o1, o2) -> {
            ProxiedPlayer player1 = ProxyServer.getInstance().getPlayer(o1.getUuid());
            ProxiedPlayer player2 = ProxyServer.getInstance().getPlayer(o2.getUuid());
            if (player1 != null && player2 == null) {
                return -1;
            } else if (player1 == null && player2 != null) {
                return 1;
            } else {
                return 0;
            }
        });

        List<TextComponent> onlineOfflineList = new ArrayList<>();

        int startIndex = (pageNumber - 1) * 10;
        int endIndex = Math.min(startIndex + 10, list.size());

        for (int i = startIndex; i < endIndex; i++) {
            FriendManager.Friend friend = list.get(i);
            UUID uuid = friend.getUuid();
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            TextComponent textComponent = new TextComponent("§8- " + getColor(uuid) + getName(uuid));
            if (player != null) {
                textComponent.addExtra("§8: §aOnline §7on §e" + player.getServer().getInfo().getName());
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend jump " + player.getName()));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aJump to " + getColor(uuid) + getName(uuid)).create()));
                onlineOfflineList.add(textComponent);
            } else {
                textComponent.addExtra(" §8(§cOffline§8)");
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cOffline §7since §6" + convertTime(System.currentTimeMillis() - friend.getLastJoin())).create()));
                onlineOfflineList.add(textComponent);
            }
        }

        return onlineOfflineList;
    }

    private void sendHelp(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage("§8§m----------§f§lFRIEND§8§m-------------");
        proxiedPlayer.sendMessage(prefix + "/friend add (player)");
        proxiedPlayer.sendMessage(prefix + "/friend remove (player)");
        proxiedPlayer.sendMessage(prefix + "/friend accept (player)");
        proxiedPlayer.sendMessage(prefix + "/friend deny (player)");
        proxiedPlayer.sendMessage(prefix + "/friend jump (player)");
        proxiedPlayer.sendMessage(prefix + "/friend list (page)");
        proxiedPlayer.sendMessage(prefix + "/friend requests");
        proxiedPlayer.sendMessage(prefix + "/msg (player)");
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
