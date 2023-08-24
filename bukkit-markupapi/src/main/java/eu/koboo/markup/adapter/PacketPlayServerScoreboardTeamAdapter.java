package eu.koboo.markup.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.util.PlayerMeta;
import eu.koboo.markup.wrapper.WrapperPlayServerScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class PacketPlayServerScoreboardTeamAdapter extends PacketAdapter {

    private final MarkupAPI markupAPI;

    public PacketPlayServerScoreboardTeamAdapter(MarkupAPI markupAPI) {
        super(markupAPI, ListenerPriority.LOWEST, PacketType.Play.Server.SCOREBOARD_TEAM);
        this.markupAPI = markupAPI;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam(event.getPacket());

        Iterator<String> playerIterator = packet.getPlayers().iterator();

        if (!playerIterator.hasNext()) {
            return;
        }

        Player player = Bukkit.getPlayer(playerIterator.next());

        if (player == null) {
            return;
        }

        PlayerMeta playerMeta = markupAPI.getNickManager().getPlayerMeta(player.getUniqueId());
        if (playerMeta == null) {
            return;
        }

        if (packet.getPlayers().contains(playerMeta.getRealName())) {
            packet.getPlayers().add(playerMeta.getNickName());
            packet.getPlayers().remove(playerMeta.getRealName());
        }

    }

}