package eu.koboo.markup.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.util.PlayerMeta;
import eu.koboo.markup.wrapper.WrapperPlayServerScoreboardScore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketPlayServerScoreboardScoreAdapter extends PacketAdapter {

    private final MarkupAPI markupAPI;

    public PacketPlayServerScoreboardScoreAdapter(MarkupAPI markupAPI) {
        super(markupAPI, ListenerPriority.LOWEST, PacketType.Play.Server.SCOREBOARD_SCORE);
        this.markupAPI = markupAPI;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.isPlayerTemporary()) {
            return;
        }
        WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore(event.getPacket());

        Player player = Bukkit.getPlayer(packet.getScoreName());

        if (player == null || event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
            return;
        }

        PlayerMeta playerMeta = markupAPI.getNickManager().getPlayerMeta(player.getUniqueId());
        if (playerMeta == null) {
            return;
        }

        WrapperPlayServerScoreboardScore additionalPacket = new WrapperPlayServerScoreboardScore();

        additionalPacket.setPacketMode(packet.getPacketMode());
        additionalPacket.setScoreName(playerMeta.getNickName());
        additionalPacket.setValue(packet.getValue());
        additionalPacket.sendPacket(event.getPlayer());
    }

}