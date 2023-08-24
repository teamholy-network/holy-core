package eu.koboo.markup.manager;

import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.events.PlayerNameTagEvent;
import eu.koboo.markup.events.PlayerPostNickEvent;
import eu.koboo.markup.events.PlayerPostUnnickEvent;
import eu.koboo.markup.util.PlayerMeta;
import eu.koboo.markup.wrapper.WrapperPlayServerScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeamManager implements Listener {

    private final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final MarkupAPI markupAPI;

    public TeamManager(MarkupAPI markupAPI) {
        this.markupAPI = markupAPI;
        Bukkit.getPluginManager().registerEvents(this, markupAPI);
    }

    public void announceUpdate(Player player) {
        service.execute(() -> {
            PlayerMeta playerMeta = markupAPI.getNickManager().getPlayerMeta(player.getUniqueId());
            String playerName = playerMeta != null ? playerMeta.getNickName() : player.getName();
            for (Player online : Bukkit.getOnlinePlayers()) {
                fireNameTagEvent(player, online, playerName);
                PlayerMeta otherMeta = markupAPI.getNickManager().getPlayerMeta(online.getUniqueId());
                String otherName = otherMeta != null ? otherMeta.getNickName() : online.getName();
                fireNameTagEvent(online, player, otherName);
            }
        });
    }

    private void fireNameTagEvent(Player player, Player other, String name) {
        PlayerNameTagEvent nameTagEvent = new PlayerNameTagEvent(player, other);
        Bukkit.getPluginManager().callEvent(nameTagEvent);
        if (nameTagEvent.getSortId() == 0 && nameTagEvent.getPrefix() == null && nameTagEvent.getSuffix() == null) {
            return;
        }
        WrapperPlayServerScoreboardTeam createPacket = getCreateTeam(player, name, nameTagEvent.getSortId(), nameTagEvent.getPrefix(), nameTagEvent.getSuffix(), nameTagEvent.getDisplaySuffix());
        WrapperPlayServerScoreboardTeam deletePacket = getDeleteTeam(player, name, nameTagEvent.getSortId());
        deletePacket.sendPacket(other);
        createPacket.sendPacket(other);
    }

    private WrapperPlayServerScoreboardTeam getCreateTeam(Player player, String playerName, int sortId, String prefix, String suffix, String displaySuffix) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        String teamName = sortId + playerName;
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }

        //prefix = prefix == null ? "" : prefix.length() > 16 ? prefix.substring(0, 16) : prefix;
        //suffix = suffix == null ? "" : suffix.length() > 16 ? suffix.substring(0, 16) : suffix;
        List<String> playerList = new ArrayList<>();
        playerList.add(player.getName());

        packet.setTeamName(teamName);
        packet.setMode(0);
        packet.setTeamDisplayName(player.getName());
        packet.setTeamPrefix(prefix);
        if (displaySuffix != null) {
            packet.setTeamSuffix(displaySuffix);
        }
        packet.setFriendlyFire((byte) 0);
        packet.setNameTagVisibility("ALWAYS");
        packet.setColor(0);
        packet.setPlayers(playerList);
        player.setPlayerListName(prefix + player.getName() + suffix);

        return packet;
    }

    private WrapperPlayServerScoreboardTeam getDeleteTeam(Player player, String playerName, int sortId) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        String teamName = sortId + playerName;
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }

        packet.setTeamName(teamName);
        packet.setMode(1);
        return packet;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostNick(PlayerPostNickEvent event) {
        announceUpdate(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostUnnick(PlayerPostUnnickEvent event) {
        announceUpdate(event.getPlayer());
    }

}
