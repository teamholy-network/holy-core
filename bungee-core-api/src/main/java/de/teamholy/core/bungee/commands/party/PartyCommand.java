package de.teamholy.core.bungee.commands.party;

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
                player.sendMessage(prefix + "You are not in a party!");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int amount = 1; amount < args.length; amount++) {
                sb.append(args[amount]).append(" ");
            }


            party.getPartyPlayers().forEach(all -> {
                ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                if (partyHandler.isPartyLeader(player.getUniqueId())) {
                    partyPlayer.sendMessage(prefix + "§c§lLEADER " + getColor(player.getUniqueId()) + player.getName() + " §8» §7" + sb);
                } else {
                    partyPlayer.sendMessage(prefix + "§a§lMEMBER " + getColor(player.getUniqueId()) + player.getName() + " §8» §7" + sb);
                }
            });

        } else if (args.length == 1) {

            if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("info")) {

                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + "You are not in a party!");
                    return;
                }

                player.sendMessage("§8§m-----------------------------");
                player.sendMessage("              §f§lPARTYINFO         ");
                player.sendMessage("§7Public §8» " + (party.isPublic() ? "§a✔" : "§c✘"));
                player.sendMessage("§cLeader §8» §71/§c1");
                ProxiedPlayer leader = ProxyServer.getInstance().getPlayer(party.getPartyPlayers().get(0));
                player.sendMessage(" " + getColor(leader.getUniqueId()) + leader.getName());
                player.sendMessage("");
                player.sendMessage("§aMembers §8» §7" + (party.getPartyPlayers().size() - 1) + "§8/§c" + (party.getMaxSize() == -1 ? "-1" : (party.getMaxSize() - 1)));
                for (int i = 1; i < party.getPartyPlayers().size(); i++) {
                    ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(party.getPartyPlayers().get(i));
                    player.sendMessage(" " + getColor(partyPlayer.getUniqueId()) + partyPlayer.getName());
                }
                player.sendMessage("");
                player.sendMessage("§8§m-----------------------------");

            } else if (args[0].equalsIgnoreCase("leave")) {
                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + "You are not in a party!");
                    return;
                }

                partyHandler.removePlayerFromParty(player);
            } else if (args[0].equalsIgnoreCase("togglepublic")) {
                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + "You are not in a party!");
                    return;
                }


                if (!partyHandler.isPartyLeader(player.getUniqueId())) {
                    player.sendMessage(prefix + "You are not the party leader!");
                    return;
                }

                if (!player.hasPermission("teamholy.party.public")) {
                    player.sendMessage(prefix + "You dont have permission to toggle your party public!");
                    return;
                }

                party.setPublic(!party.isPublic());
                if (party.isPublic()) {
                    for (UUID all : party.getPartyPlayers()) {
                        ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                        partyPlayer.sendMessage(prefix + "§aThe party is now public");
                        partyPlayer.sendMessage(prefix + "§ausers can join with §8/§7party join (name)");
                    }
                } else {
                    for (UUID all : party.getPartyPlayers()) {
                        ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                        partyPlayer.sendMessage(prefix + "§cThe party is not public anymore");
                    }
                }

            } else if (args[0].equalsIgnoreCase("create")) {
                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party != null) {
                    player.sendMessage(prefix + "You are already in a party!");
                    return;
                }

                player.sendMessage(prefix + "You created a party");
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
                    player.sendMessage(prefix + "The player is offline!");
                    return;
                }

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(prefix + "You cannot interact with yourself!");
                    return;
                }

                if (partyHandler.getPartyByPlayerUUID(target.getUniqueId()) != null) {
                    player.sendMessage(prefix + getColor(target.getUniqueId()) + target.getName() + " §7is already in a party!");
                    return;
                }

                if (!canInvite(target.getUniqueId(), player.getUniqueId())) {
                    player.sendMessage(prefix + getColor(target.getUniqueId()) + target.getName() + " §7toggled their party invites!");
                    return;
                }

                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + "You created a party");
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
                    player.sendMessage(prefix + "You are not the party leader!");
                    return;
                }

                if (party.getInvitedPlayers().contains(target.getUniqueId())) {
                    player.sendMessage(prefix + "You already invited " + getColor(target.getUniqueId()) + target.getName() + "§7!");
                    return;
                }

                player.sendMessage(prefix + "You invited " + getColor(target.getUniqueId()) + target.getName() + "§7!");


                party.getInvitedPlayers().add(target.getUniqueId());
                TextComponent message = new TextComponent("§a§lACCEPT");
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + player.getName()));
                TextComponent message1 = new TextComponent("§c§lDENY");
                message1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + player.getName()));
                target.sendMessage("§8§m----------§f§lPARTY----------");
                target.sendMessage("§7You got an partyinvite from " + getColor(player.getUniqueId()) + player.getName());
                target.sendMessage(new ComponentBuilder("          ").append("§a§lACCEPT").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + player.getName()))
                    .append("       ").append("§c§lDENY").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + player.getName()))
                    .create());
                target.sendMessage("§8§m-----------------------------");

            } else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("join")) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(prefix + "The player is offline!");
                    return;
                }

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(prefix + "You cannot interact with yourself!");
                    return;
                }

                if (partyHandler.getPartyByPlayerUUID(player.getUniqueId()) != null) {
                    player.sendMessage(prefix + "You are already in a party!");
                    return;
                }

                Party party = partyHandler.getPartyByPlayerUUID(target.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + getColor(target.getUniqueId()) + target.getName() + " §7is not in a party!");
                    return;
                }

                if (party.getMaxSize() != -1 && party.getMaxSize() == party.getPartyPlayers().size()) {
                    player.sendMessage(prefix + "The party is full!");
                    return;
                }

                if (party.isPublic()) {
                    player.sendMessage(prefix + "You joined the party from " + getColor(target.getUniqueId()) + target.getName());

                    party.getInvitedPlayers().remove(player.getUniqueId());
                    String name = getColor(player.getUniqueId()) + player.getName();
                    party.getPartyPlayers().forEach(all -> {
                        ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                        partyPlayer.sendMessage("§5Party §8× " + name + " §7joined the party");
                    });
                    party.getPartyPlayers().add(player.getUniqueId());
                    return;
                }

                if (!partyHandler.gotInvited(player, target.getUniqueId())) {
                    player.sendMessage(prefix + "The player " + getColor(target.getUniqueId()) + target.getName() + " §7did not invite you!");
                    return;
                }


                player.sendMessage(prefix + "You accepted the party invite from " + getColor(target.getUniqueId()) + target.getName());

                party.getInvitedPlayers().remove(player.getUniqueId());
                String name = getColor(player.getUniqueId()) + player.getName();
                party.getPartyPlayers().forEach(all -> {
                    ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                    partyPlayer.sendMessage("§5Party §8× " + name + " §7joined the party");
                });
                party.getPartyPlayers().add(player.getUniqueId());
            } else if (args[0].equalsIgnoreCase("deny")) {

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(prefix + "The player is offline!");
                    return;
                }

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(prefix + "You cannot interact with yourself!");
                    return;
                }

                if (!partyHandler.gotInvited(player, target.getUniqueId())) {
                    player.sendMessage(prefix + "The player " + getColor(target.getUniqueId()) + target.getName() + " §7did not invite you!");
                    return;
                }

                player.sendMessage(prefix + "You declined the party invite from " + getColor(target.getUniqueId()) + target.getName());

                Party party = partyHandler.getPartyByPlayerUUID(target.getUniqueId());
                party.getInvitedPlayers().remove(player.getUniqueId());
                target.sendMessage(prefix + getColor(player.getUniqueId()) + player.getName() + " §7has declined your request");
            } else if (args[0].equalsIgnoreCase("kick")) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(prefix + "The player is offline!");
                    return;
                }

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(prefix + "You cannot interact with yourself!");
                    return;
                }

                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + "You are not in a party!");
                    return;
                }

                if (!partyHandler.isPartyLeader(player.getUniqueId())) {
                    player.sendMessage(prefix + "You are not the party leader!");
                    return;
                }

                if (!(partyHandler.getPartyByPlayerUUID(player.getUniqueId()) == partyHandler.getPartyByPlayerUUID(target.getUniqueId()))) {
                    player.sendMessage(prefix + getColor(target.getUniqueId()) + target.getName() + " §7is not in your party!");
                    return;
                }

                party.getPartyPlayers().forEach(all -> {
                    ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                    partyPlayer.sendMessage("§5Party §8× " + getColor(target.getUniqueId()) + target.getName() + " §7was kicked out of the party");
                });
                party.getPartyPlayers().remove(target.getUniqueId());
            } else if (args[0].equalsIgnoreCase("removeinvite")) {

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(prefix + "The player is offline!");
                    return;
                }

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(prefix + "You cannot interact with yourself!");
                    return;
                }

                Party party = partyHandler.getPartyByPlayerUUID(player.getUniqueId());

                if (party == null) {
                    player.sendMessage(prefix + "You are not in a party!");
                    return;
                }

                if (!partyHandler.isPartyLeader(player.getUniqueId())) {
                    player.sendMessage(prefix + "You are not the party leader!");
                    return;
                }

                if (!party.getInvitedPlayers().contains(target.getUniqueId())) {
                    player.sendMessage(prefix + getColor(target.getUniqueId()) + target.getName() + " §7is not invited!");
                    return;
                }

                player.sendMessage(prefix + "The invite of " + getColor(target.getUniqueId()) + target.getName() + " §7was removed");
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
        return BungeeCore.getAPI().getCloudManager().getColor(uuid);
    }


    private void sendHelp(ProxiedPlayer proxiedPlayer) {
        proxiedPlayer.sendMessage("§8§m----------§f§lPARTY§8§m-------------");
        proxiedPlayer.sendMessage(prefix + "/party invite (player)");
        proxiedPlayer.sendMessage(prefix + "/party accept§8/§7join (player)");
        proxiedPlayer.sendMessage(prefix + "/party deny (player)");
        proxiedPlayer.sendMessage(prefix + "/party kick (player)");
        proxiedPlayer.sendMessage(prefix + "/party removeinvite (player)");
        proxiedPlayer.sendMessage(prefix + "/party chat (message)");
        proxiedPlayer.sendMessage(prefix + "/party togglepublic");
        proxiedPlayer.sendMessage(prefix + "/party leave");
        proxiedPlayer.sendMessage(prefix + "/party create");
        proxiedPlayer.sendMessage(prefix + "/party list§8/§7info");
        proxiedPlayer.sendMessage("§8§m-----------------------------");

    }
}
