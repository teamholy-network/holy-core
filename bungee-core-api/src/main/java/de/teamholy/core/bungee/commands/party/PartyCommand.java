package de.teamholy.core.bungee.commands.party;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.friend.FriendProfile;
import de.teamholy.core.api.utility.PartyInviteAllowance;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.PartyManager;
import de.teamholy.core.bungee.model.Party;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/* copyright by Yassino */
public class PartyCommand extends Command {


    private String prefix = "§5Party §8× §7";
    private final PartyManager partyHandler = BungeeCore.getInstance().getPartyManager();

    public PartyCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length > 1 && args[0] != null && args[0].equalsIgnoreCase("chat")) {

            Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

            if (party == null) {
                player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are not in a party!"));
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int amount = 1; amount < args.length; amount++) {
                sb.append(args[amount]).append(" ");
            }


            party.getPartyPlayers().forEach(all -> {
                ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                if (partyHandler.isPartyLeader(player.getUniqueId())) {
                    partyPlayer.sendMessage(prefix + "§c§l" + BungeeTranslateAPI.translate(player, "LEADER") + " " + getColor(player.getUniqueId()) + player.getName() + " §8» §7" + sb);
                } else {
                    partyPlayer.sendMessage(prefix + "§a§l" + BungeeTranslateAPI.translate(player, "MEMBER") + " " + getColor(player.getUniqueId()) + player.getName() + " §8» §7" + sb);
                }
            });

        } else if (args.length == 1) {

            if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("info")) {

                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are not in a party!"));
                    return;
                }

                player.sendMessage("§8§m-----------------------------");
                player.sendMessage("              §f§lPARTYINFO         ");
                player.sendMessage("§7" + BungeeTranslateAPI.translate(player, "Public") + " §8» " + (party.isPublic() ? "§a✔" : "§c✘"));
                player.sendMessage("§c" + BungeeTranslateAPI.translate(player, "Leader") + " §8» §71/§c1");
                ProxiedPlayer leader = ProxyServer.getInstance().getPlayer(party.getPartyPlayers().get(0));
                player.sendMessage(" " + getColor(leader.getUniqueId()) + leader.getName());
                player.sendMessage("");
                player.sendMessage("§a" + BungeeTranslateAPI.translate(player, "Members") + " §8» §7" + (party.getPartyPlayers().size() - 1) + "§8/§c" + (party.getMaxSize() == -1 ? "-1" : (party.getMaxSize() - 1)));
                for (int i = 1; i < party.getPartyPlayers().size(); i++) {
                    ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(party.getPartyPlayers().get(i));
                    player.sendMessage(" " + getColor(partyPlayer.getUniqueId()) + partyPlayer.getName());
                }
                player.sendMessage("");
                player.sendMessage("§8§m-----------------------------");

            } else if (args[0].equalsIgnoreCase("leave")) {
                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are not in a party!"));
                    return;
                }

                partyHandler.removePlayerFromParty(player);
            } else if (args[0].equalsIgnoreCase("togglepublic")) {
                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are not in a party!"));
                    return;
                }


                if (!partyHandler.isPartyLeader(player.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are not the party leader!"));
                    return;
                }

                if (!player.hasPermission("teamholy.party.public")) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You dont have permission to toggle your party public!"));
                    return;
                }

                party.setPublic(!party.isPublic());
                if (party.isPublic()) {
                    for (UUID all : party.getPartyPlayers()) {
                        ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                        partyPlayer.sendMessage(prefix + "§a" + BungeeTranslateAPI.translate(player, "The party is now public"));
                        partyPlayer.sendMessage(prefix + "§a" + BungeeTranslateAPI.translate(player, "users can join with") + " §8/§7party join (" + BungeeTranslateAPI.translate(player, "name") + ")");
                    }
                } else {
                    for (UUID all : party.getPartyPlayers()) {
                        ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                        partyPlayer.sendMessage(prefix + "§c" + BungeeTranslateAPI.translate(player, "The party is not public anymore"));
                    }
                }

            } else if (args[0].equalsIgnoreCase("create")) {
                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party != null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are already in a party!"));
                    return;
                }

                player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You created a party"));
                int i = 4;
                if (player.hasPermission("teamholy.party.premium")) {
                    i = 8;
                }
                if (player.hasPermission("teamholy.party.vip")) {
                    i = -1;
                }
                party = new Party(i);
                party.getPartyPlayers().add(player.getUniqueId());
                partyHandler.getParties().put(player.getUniqueId(), party);
            }

        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("invite")) {

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "The player is offline!"));
                    return;
                }

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You cannot interact with yourself!"));
                    return;
                }

                if (partyHandler.getPartyByPlayerUUID(target.getUniqueId()) != null) {
                    player.sendMessage(prefix + getColor(target.getUniqueId()) + target.getName() + BungeeTranslateAPI.translate(player, " §7is already in a party!"));
                    return;
                }

                if (!canInvite(target.getUniqueId(), player.getUniqueId())) {
                    player.sendMessage(prefix + " §7" + BungeeTranslateAPI.translatePlaceholder(player, "{} toggled their party invites!", getColor(target.getUniqueId()) + target.getName()));
                    return;
                }

                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You created a party"));
                    int i = 4;
                    if (player.hasPermission("teamholy.party.premium")) {
                        i = 8;
                    }
                    if (player.hasPermission("teamholy.party.vip")) {
                        i = -1;
                    }
                    party = new Party(i);
                    party.getPartyPlayers().add(player.getUniqueId());
                    partyHandler.getParties().put(player.getUniqueId(), party);
                }

                if (!partyHandler.isPartyLeader(player.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are not the party leader!"));
                    return;
                }

                if (party.getInvitedPlayers().contains(target.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(player, "You already invited {}§7!", getColor(target.getUniqueId()) + target.getName()));
                    return;
                }

                player.sendMessage(prefix + "You invited " + getColor(target.getUniqueId()) + target.getName() + "§7!");


                party.getInvitedPlayers().add(target.getUniqueId());
                TextComponent message = new TextComponent("§a§l" + BungeeTranslateAPI.translate(player, "ACCEPT"));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + player.getName()));
                TextComponent message1 = new TextComponent("§c§l" + BungeeTranslateAPI.translate(player, "DENY"));
                message1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + player.getName()));
                target.sendMessage("§8§m----------§f§lPARTY----------");
                target.sendMessage("§7" + BungeeTranslateAPI.translatePlaceholder(player, "You got an partyinvite from {}", getColor(player.getUniqueId()) + player.getName()));
                target.sendMessage(new ComponentBuilder("          ").append("§a§l" + BungeeTranslateAPI.translate(player, "ACCEPT")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + player.getName()))
                    .append("       ").append("§c§l" + BungeeTranslateAPI.translate(player, "Refuse")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + player.getName()))
                    .create());
                target.sendMessage("§8§m-----------------------------");

            } else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("join")) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "The player is offline!"));
                    return;
                }

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You cannot interact with yourself!"));
                    return;
                }

                if (partyHandler.getPartyByPlayerUUID(player.getUniqueId()) != null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are already in a party!"));
                    return;
                }

                Party party = partyHandler.getPartyByPlayerUUID(target.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(player, "{} §7is not in a party!", getColor(target.getUniqueId()) + target.getName()));
                    return;
                }

                if (party.getMaxSize() != -1 && party.getMaxSize() == party.getPartyPlayers().size()) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "The party is full!"));
                    return;
                }

                if (party.isPublic()) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(player, "You joined the party from " + getColor(target.getUniqueId()) + target.getName()));

                    party.getInvitedPlayers().remove(player.getUniqueId());
                    String name = getColor(player.getUniqueId()) + player.getName();
                    party.getPartyPlayers().forEach(all -> {
                        ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                        partyPlayer.sendMessage("§5Party §8× " + BungeeTranslateAPI.translatePlaceholder(player, "{} §7joined the party", name));
                    });
                    party.getPartyPlayers().add(player.getUniqueId());
                    return;
                }

                if (!partyHandler.gotInvited(player, target.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(player, "The player {} §7did not invite you!", getColor(target.getUniqueId()) + target.getName()));
                    return;
                }


                player.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(player, "You accepted the party invite from {}", getColor(target.getUniqueId()) + target.getName()));

                party.getInvitedPlayers().remove(player.getUniqueId());
                String name = getColor(player.getUniqueId()) + player.getName();
                party.getPartyPlayers().forEach(all -> {
                    ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                    partyPlayer.sendMessage("§5Party §8× " + BungeeTranslateAPI.translatePlaceholder(player, "{} §7joined the party", name));
                });
                party.getPartyPlayers().add(player.getUniqueId());
            } else if (args[0].equalsIgnoreCase("deny")) {

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "The player is offline!"));
                    return;
                }

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You cannot interact with yourself!"));
                    return;
                }

                if (!partyHandler.gotInvited(player, target.getUniqueId())) {
                    player.sendMessage(prefix + "The player " + getColor(target.getUniqueId()) + target.getName() + " §7did not invite you!");
                    return;
                }

                player.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(player, "You declined the party invite from {}", getColor(target.getUniqueId()) + target.getName()));

                Party party = partyHandler.getPartyByPlayerUUID(target.getUniqueId());
                party.getInvitedPlayers().remove(player.getUniqueId());
                target.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(player, "{} §7has declined your request", getColor(player.getUniqueId()) + player.getName()));
            } else if (args[0].equalsIgnoreCase("kick")) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "The player is offline!"));
                    return;
                }

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You cannot interact with yourself!"));
                    return;
                }

                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are not in a party!"));
                    return;
                }

                if (!partyHandler.isPartyLeader(player.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are not the party leader!"));
                    return;
                }

                if (!(partyHandler.getPartyByPlayerUUID(player.getUniqueId()) == partyHandler.getPartyByPlayerUUID(target.getUniqueId()))) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(player, "{} §7is not in your party!", getColor(target.getUniqueId()) + target.getName()));
                    return;
                }

                party.getPartyPlayers().forEach(all -> {
                    ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                    partyPlayer.sendMessage("§5Party §8× " + BungeeTranslateAPI.translatePlaceholder(partyPlayer, "{} §7was kicked out of the party", getColor(target.getUniqueId()) + target.getName()));
                });
                party.getPartyPlayers().remove(target.getUniqueId());
            } else if (args[0].equalsIgnoreCase("removeinvite")) {

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "The player is offline!"));
                    return;
                }

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You cannot interact with yourself!"));
                    return;
                }

                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are not in a party!"));
                    return;
                }

                if (!partyHandler.isPartyLeader(player.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translate(player, "You are not the party leader!"));
                    return;
                }

                if (!party.getInvitedPlayers().contains(target.getUniqueId())) {
                    player.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(player, "{} §7is not invited!", getColor(target.getUniqueId()) + target.getName()));
                    return;
                }

                player.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(player, "The invite of {} §7was removed", getColor(target.getUniqueId()) + target.getName()));
                party.getInvitedPlayers().remove(target.getUniqueId());
            }

        } else {
            sendHelp(player);
        }
    }

    private boolean canInvite(UUID uuid, UUID player) {
        FriendProfile friendProfile = BungeeCore.getAPI().getFriendService().getEntity(uuid, () -> BungeeCore.getAPI().getFriendService().getRepository().findFirstById(uuid));
        if (friendProfile.getPartyInviteAllowance() == PartyInviteAllowance.NONE) return false;
        if (friendProfile.getPartyInviteAllowance() == PartyInviteAllowance.EVERYONE) return true;
        if (friendProfile.getPartyInviteAllowance() == PartyInviteAllowance.ONLY_FRIENDS && friendProfile.getFriendList().contains(player))
            return true;

        return false;
    }

    private String getColor(UUID uuid) {
        return BungeeCore.getInstance().getPlayerColor(uuid);
    }


    private void sendHelp(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage(Message.TOPLINE);
        proxiedPlayer.sendMessage("   " + Message.HELP_TITLE_PARTY);
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/party create");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/party invite (" + BungeeTranslateAPI.translate(proxiedPlayer,"player") + ")");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/party join (" + BungeeTranslateAPI.translate(proxiedPlayer,"player") + ")");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/party leave");
        proxiedPlayer.sendMessage(Message.HELP_BULLET + "/party chat (" + BungeeTranslateAPI.translate(proxiedPlayer,"message") + ")");
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage(Message.LINE_DOWN);
    }
}
