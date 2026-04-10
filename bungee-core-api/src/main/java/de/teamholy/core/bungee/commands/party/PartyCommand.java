package de.teamholy.core.bungee.commands.party;

import de.teamholy.core.translation.BungeeTranslateAPI;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.friend.FriendProfile;
import de.teamholy.core.api.utility.PartyInviteAllowance;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.PartyManager;
import de.teamholy.core.bungee.model.Party;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a command handler for managing party-related actions in a server environment.
 * This class extends the {@code net.md_5.bungee.api.plugin.Command} class and provides functionality for:
 * - Creating, joining, leaving, and managing parties
 * - Sending and handling party invitations
 * - Broadcasting messages to party members
 * - Handling permissions and party size restrictions
 * - Managing party-related state and transitions
 *
 * The command supports various administrative and user-level actions related to party management.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PartyCommand extends Command {

    private static final String PREFIX = "§5Party §8× §7";
    private static final String PERMISSION_PARTY_PUBLIC = "teamholy.party.public";
    private static final String PERMISSION_PARTY_PREMIUM = "teamholy.party.premium";
    private static final String PERMISSION_PARTY_VIP = "teamholy.party.vip";

    private static final int DEFAULT_PARTY_SIZE = 4;
    private static final int PREMIUM_PARTY_SIZE = 8;
    private static final int VIP_PARTY_SIZE = -1;

    PartyManager partyManager;

    public PartyCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.partyManager = BungeeCore.getInstance().getPartyManager();
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(
                ChatColor.RED).create());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        try {
            if (args.length == 0) {
                sendHelp(player);
                return;
            }

            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "chat" -> handlePartyChat(player, args);
                case "list", "info" -> handlePartyInfo(player);
                case "leave" -> handlePartyLeave(player);
                case "togglepublic" -> handleTogglePublic(player);
                case "create" -> handlePartyCreate(player);
                case "invite" -> handlePartyInvite(player, args);
                case "accept", "join" -> handlePartyJoin(player, args);
                case "deny" -> handlePartyDeny(player, args);
                case "kick" -> handlePartyKick(player, args);
                case "removeinvite" -> handleRemoveInvite(player, args);
                default -> sendHelp(player);
            }
        } catch (Exception e) {
            log.error("Error executing party command for player {}", player.getName(), e);
            player.sendMessage(new TextComponent(PREFIX + "§cEin Fehler ist aufgetreten. Bitte versuche es erneut."));
        }
    }

    private void handlePartyChat(ProxiedPlayer player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(new TextComponent(PREFIX + "/party chat (" + BungeeTranslateAPI.translate(player, "message") + ")"));
            return;
        }

        Party party = getPlayerParty(player);
        if (party == null) {
            sendNotInPartyMessage(player);
            return;
        }

        String message = buildMessage(args);
        String senderName = getPlayerColor(player.getUniqueId()) + player.getName();

        broadcastToParty(party, partyPlayer -> {
            String translatedPrefix = partyManager.isPartyLeader(player.getUniqueId())
                ? "§c§l" + BungeeTranslateAPI.translate(partyPlayer, "LEADER")
                : "§a§l" + BungeeTranslateAPI.translate(partyPlayer, "MEMBER");
            partyPlayer.sendMessage(new TextComponent(PREFIX + translatedPrefix + " " + senderName + " §8» §7" + message));
        });
    }

    private void handlePartyInfo(ProxiedPlayer player) {
        Party party = getPlayerParty(player);
        if (party == null) {
            sendNotInPartyMessage(player);
            return;
        }

        player.sendMessage(new TextComponent("§8§m-----------------------------"));
        player.sendMessage(new TextComponent("              §f§lPARTYINFO         "));
        player.sendMessage(new TextComponent("§7" + BungeeTranslateAPI.translate(player, "Public") + " §8» " +
            (party.isPublic() ? "§a✔" : "§c✘")));
        player.sendMessage(new TextComponent("§c" + BungeeTranslateAPI.translate(player, "Leader") + " §8» §71/§c1"));

        getPlayer(party.getPartyPlayers().get(0)).ifPresent(leader ->
            player.sendMessage(new TextComponent(" " + getPlayerColor(leader.getUniqueId()) + leader.getName()))
        );

        player.sendMessage(new TextComponent(""));

        int memberCount = party.getPartyPlayers().size() - 1;
        int maxMembers = party.getMaxSize() == -1 ? -1 : party.getMaxSize() - 1;
        player.sendMessage(new TextComponent("§a" + BungeeTranslateAPI.translate(player, "Members") + " §8» §7" +
            memberCount + "§8/§c" + (maxMembers == -1 ? "-1" : maxMembers)));

        for (int i = 1; i < party.getPartyPlayers().size(); i++) {
            final int index = i;
            getPlayer(party.getPartyPlayers().get(index)).ifPresent(member ->
                player.sendMessage(new TextComponent(" " + getPlayerColor(member.getUniqueId()) + member.getName()))
            );
        }

        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent("§8§m-----------------------------"));
    }

    private void handlePartyLeave(ProxiedPlayer player) {
        Party party = getPlayerParty(player);
        if (party == null) {
            sendNotInPartyMessage(player);
            return;
        }

        partyManager.removePlayerFromParty(player);
    }

    private void handleTogglePublic(ProxiedPlayer player) {
        Party party = getPlayerParty(player);
        if (party == null) {
            sendNotInPartyMessage(player);
            return;
        }

        if (!partyManager.isPartyLeader(player.getUniqueId())) {
            sendNotLeaderMessage(player);
            return;
        }

        if (!player.hasPermission(PERMISSION_PARTY_PUBLIC)) {
            player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translate(player,
                "You dont have permission to toggle your party public!")));
            return;
        }

        party.setPublic(!party.isPublic());

        if (party.isPublic()) {
            broadcastToParty(party, partyPlayer -> {
                partyPlayer.sendMessage(new TextComponent(PREFIX + "§a" + BungeeTranslateAPI.translate(partyPlayer,
                    "The party is now public")));
                partyPlayer.sendMessage(new TextComponent(PREFIX + "§a" + BungeeTranslateAPI.translate(partyPlayer,
                    "users can join with") + " §8/§7party join (" +
                    BungeeTranslateAPI.translate(partyPlayer, "name") + ")"));
            });
        } else {
            broadcastToParty(party, partyPlayer ->
                partyPlayer.sendMessage(new TextComponent(PREFIX + "§c" + BungeeTranslateAPI.translate(partyPlayer,
                    "The party is not public anymore")))
            );
        }
    }

    private void handlePartyCreate(ProxiedPlayer player) {
        if (getPlayerParty(player) != null) {
            player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translate(player, "You are already in a party!")));
            return;
        }

        int partySize = determinePartySize(player);
        Party party = new Party(partySize);
        party.getPartyPlayers().add(player.getUniqueId());
        partyManager.getParties().put(player.getUniqueId(), party);

        player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translate(player, "You created a party")));
    }

    private void handlePartyInvite(ProxiedPlayer player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(new TextComponent(PREFIX + "/party invite (" + BungeeTranslateAPI.translate(player, "player") + ")"));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
        if (target == null) {
            sendPlayerOfflineMessage(player);
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            sendCannotInteractWithSelfMessage(player);
            return;
        }

        if (getPlayerParty(target) != null) {
            player.sendMessage(new TextComponent(PREFIX + getPlayerColor(target.getUniqueId()) + target.getName() +
                BungeeTranslateAPI.translate(player, " §7is already in a party!")));
            return;
        }

        if (!canReceiveInvite(target.getUniqueId(), player.getUniqueId())) {
            player.sendMessage(new TextComponent(PREFIX + " §7" + BungeeTranslateAPI.translatePlaceholder(player,
                "{} toggled their party invites!", getPlayerColor(target.getUniqueId()) + target.getName())));
            return;
        }

        Party party = getOrCreateParty(player);

        if (!partyManager.isPartyLeader(player.getUniqueId())) {
            sendNotLeaderMessage(player);
            return;
        }

        if (party.getInvitedPlayers().contains(target.getUniqueId())) {
            player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translatePlaceholder(player,
                "You already invited {}§7!", getPlayerColor(target.getUniqueId()) + target.getName())));
            return;
        }

        party.getInvitedPlayers().add(target.getUniqueId());
        player.sendMessage(new TextComponent(PREFIX + "You invited " + getPlayerColor(target.getUniqueId()) +
            target.getName() + "§7!"));

        sendInviteMessage(player, target);
    }

    private void handlePartyJoin(ProxiedPlayer player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(new TextComponent(PREFIX + "/party join (" + BungeeTranslateAPI.translate(player, "player") + ")"));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
        if (target == null) {
            sendPlayerOfflineMessage(player);
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            sendCannotInteractWithSelfMessage(player);
            return;
        }

        if (getPlayerParty(player) != null) {
            player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translate(player, "You are already in a party!")));
            return;
        }

        Party party = getPlayerParty(target);
        if (party == null) {
            player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translatePlaceholder(player,
                "{} §7is not in a party!", getPlayerColor(target.getUniqueId()) + target.getName())));
            return;
        }

        if (isPartyFull(party)) {
            player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translate(player, "The party is full!")));
            return;
        }

        if (party.isPublic() || partyManager.hasInvite(player, target.getUniqueId())) {
            joinParty(player, party, target);
        } else {
            player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translatePlaceholder(player,
                "The player {} §7did not invite you!", getPlayerColor(target.getUniqueId()) + target.getName())));
        }
    }

    private void handlePartyDeny(ProxiedPlayer player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(new TextComponent(PREFIX + "/party deny (" + BungeeTranslateAPI.translate(player, "player") + ")"));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
        if (target == null) {
            sendPlayerOfflineMessage(player);
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            sendCannotInteractWithSelfMessage(player);
            return;
        }

        if (!partyManager.hasInvite(player, target.getUniqueId())) {
            player.sendMessage(new TextComponent(PREFIX + "The player " + getPlayerColor(target.getUniqueId()) +
                target.getName() + " §7did not invite you!"));
            return;
        }

        Party party = getPlayerParty(target);
        if (party != null) {
            party.getInvitedPlayers().remove(player.getUniqueId());
        }

        player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translatePlaceholder(player,
            "You declined the party invite from {}", getPlayerColor(target.getUniqueId()) + target.getName())));
        target.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translatePlaceholder(target,
            "{} §7has declined your request", getPlayerColor(player.getUniqueId()) + player.getName())));
    }

    private void handlePartyKick(ProxiedPlayer player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(new TextComponent(PREFIX + "/party kick (" + BungeeTranslateAPI.translate(player, "player") + ")"));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
        if (target == null) {
            sendPlayerOfflineMessage(player);
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            sendCannotInteractWithSelfMessage(player);
            return;
        }

        Party party = getPlayerParty(player);
        if (party == null) {
            sendNotInPartyMessage(player);
            return;
        }

        if (!partyManager.isPartyLeader(player.getUniqueId())) {
            sendNotLeaderMessage(player);
            return;
        }

        Party targetParty = getPlayerParty(target);
        if (party != targetParty) {
            player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translatePlaceholder(player,
                "{} §7is not in your party!", getPlayerColor(target.getUniqueId()) + target.getName())));
            return;
        }

        String targetName = getPlayerColor(target.getUniqueId()) + target.getName();
        broadcastToParty(party, partyPlayer ->
            partyPlayer.sendMessage(new TextComponent("§5Party §8× " + BungeeTranslateAPI.translatePlaceholder(partyPlayer,
                "{} §7was kicked out of the party", targetName)))
        );

        party.getPartyPlayers().remove(target.getUniqueId());
    }

    private void handleRemoveInvite(ProxiedPlayer player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(new TextComponent(PREFIX + "/party removeinvite (" + BungeeTranslateAPI.translate(player, "player") + ")"));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
        if (target == null) {
            sendPlayerOfflineMessage(player);
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            sendCannotInteractWithSelfMessage(player);
            return;
        }

        Party party = getPlayerParty(player);
        if (party == null) {
            sendNotInPartyMessage(player);
            return;
        }

        if (!partyManager.isPartyLeader(player.getUniqueId())) {
            sendNotLeaderMessage(player);
            return;
        }

        if (!party.getInvitedPlayers().contains(target.getUniqueId())) {
            player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translatePlaceholder(player,
                "{} §7is not invited!", getPlayerColor(target.getUniqueId()) + target.getName())));
            return;
        }

        party.getInvitedPlayers().remove(target.getUniqueId());
        player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translatePlaceholder(player,
            "The invite of {} §7was removed", getPlayerColor(target.getUniqueId()) + target.getName())));
    }

    private Party getPlayerParty(ProxiedPlayer player) {
        return partyManager.getPartyByPlayerUUID(player.getUniqueId());
    }

    private Optional<ProxiedPlayer> getPlayer(UUID uuid) {
        return Optional.ofNullable(ProxyServer.getInstance().getPlayer(uuid));
    }

    private String getPlayerColor(UUID uuid) {
        return BungeeCore.getInstance().getPlayerColor(uuid);
    }

    private String buildMessage(String[] args) {
        return Arrays.stream(args)
            .skip(1)
            .collect(Collectors.joining(" "));
    }

    private void broadcastToParty(Party party, Consumer<ProxiedPlayer> messageConsumer) {
        party.getPartyPlayers().forEach(uuid ->
            getPlayer(uuid).ifPresent(messageConsumer)
        );
    }

    private boolean canReceiveInvite(UUID targetUuid, UUID senderUuid) {
        try {
            FriendProfile friendProfile = BungeeCore.getAPI().getFriendService().getEntity(
                targetUuid,
                () -> BungeeCore.getAPI().getFriendService().getRepository().findFirstById(targetUuid)
            );

            if (friendProfile == null) {
                return true;
            }

            PartyInviteAllowance allowance = friendProfile.getPartyInviteAllowance();

            return switch (allowance) {
                case NONE -> false;
                case EVERYONE -> true;
                case ONLY_FRIENDS -> friendProfile.getFriendList() != null &&
                    friendProfile.getFriendList().contains(senderUuid);
            };
        } catch (Exception e) {
            log.error("Error checking invite allowance", e);
            return true;
        }
    }

    private int determinePartySize(ProxiedPlayer player) {
        if (player.hasPermission(PERMISSION_PARTY_VIP)) {
            return VIP_PARTY_SIZE;
        }
        if (player.hasPermission(PERMISSION_PARTY_PREMIUM)) {
            return PREMIUM_PARTY_SIZE;
        }
        return DEFAULT_PARTY_SIZE;
    }

    private Party getOrCreateParty(ProxiedPlayer player) {
        Party party = getPlayerParty(player);

        if (party == null) {
            player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translate(player, "You created a party")));
            int partySize = determinePartySize(player);
            party = new Party(partySize);
            party.getPartyPlayers().add(player.getUniqueId());
            partyManager.getParties().put(player.getUniqueId(), party);
        }

        return party;
    }

    private boolean isPartyFull(Party party) {
        return party.getMaxSize() != -1 && party.getMaxSize() == party.getPartyPlayers().size();
    }

    private void joinParty(ProxiedPlayer player, Party party, ProxiedPlayer partyLeader) {
        party.getInvitedPlayers().remove(player.getUniqueId());
        party.getPartyPlayers().add(player.getUniqueId());

        String playerName = getPlayerColor(player.getUniqueId()) + player.getName();
        String leaderName = getPlayerColor(partyLeader.getUniqueId()) + partyLeader.getName();

        player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translatePlaceholder(player,
            "You joined the party from " + leaderName)));

        broadcastToParty(party, partyPlayer -> {
            if (!partyPlayer.getUniqueId().equals(player.getUniqueId())) {
                partyPlayer.sendMessage(new TextComponent("§5Party §8× " + BungeeTranslateAPI.translatePlaceholder(partyPlayer,
                    "{} §7joined the party", playerName)));
            }
        });
    }

    private void sendInviteMessage(ProxiedPlayer sender, ProxiedPlayer target) {
        target.sendMessage(new TextComponent("§8§m----------§f§lPARTY----------"));
        target.sendMessage(new TextComponent("§7" + BungeeTranslateAPI.translatePlaceholder(target,
            "You got an partyinvite from {}", getPlayerColor(sender.getUniqueId()) + sender.getName())));
        target.sendMessage(
            new ComponentBuilder("          ")
                .append("§a§l" + BungeeTranslateAPI.translate(target, "ACCEPT"))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + sender.getName()))
                .append("       ")
                .append("§c§l" + BungeeTranslateAPI.translate(target, "Refuse"))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + sender.getName()))
                .create()
        );
        target.sendMessage(new TextComponent("§8§m-----------------------------"));
    }

    private void sendHelp(ProxiedPlayer player) {
        player.sendMessage(new TextComponent(Message.TOPLINE));
        player.sendMessage(new TextComponent("   " + Message.HELP_TITLE_PARTY));
        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent(Message.HELP_BULLET + "/party create"));
        player.sendMessage(new TextComponent(Message.HELP_BULLET + "/party invite (" +
            BungeeTranslateAPI.translate(player, "player") + ")"));
        player.sendMessage(new TextComponent(Message.HELP_BULLET + "/party join (" +
            BungeeTranslateAPI.translate(player, "player") + ")"));
        player.sendMessage(new TextComponent(Message.HELP_BULLET + "/party leave"));
        player.sendMessage(new TextComponent(Message.HELP_BULLET + "/party chat (" +
            BungeeTranslateAPI.translate(player, "message") + ")"));
        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent(Message.LINE_DOWN));
    }

    private void sendNotInPartyMessage(ProxiedPlayer player) {
        player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translate(player, "You are not in a party!")));
    }

    private void sendNotLeaderMessage(ProxiedPlayer player) {
        player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translate(player, "You are not the party leader!")));
    }

    private void sendPlayerOfflineMessage(ProxiedPlayer player) {
        player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translate(player, "The player is offline!")));
    }

    private void sendCannotInteractWithSelfMessage(ProxiedPlayer player) {
        player.sendMessage(new TextComponent(PREFIX + BungeeTranslateAPI.translate(player, "You cannot interact with yourself!")));
    }
}