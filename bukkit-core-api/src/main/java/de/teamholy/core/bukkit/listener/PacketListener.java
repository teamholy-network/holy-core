package de.teamholy.core.bukkit.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/* copyright by Greg */
public class PacketListener extends PacketAdapter {

    private final Map<Player, Integer> playerViewDistance = new HashMap<>();

    public PacketListener(Plugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.SETTINGS);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.SETTINGS) {
            PacketContainer packet = event.getPacket();
            int viewDistance = packet.getIntegers().read(3);
            playerViewDistance.put(event.getPlayer(), viewDistance);
        }
    }


    public void onPlayerLeave(Player player) {
        playerViewDistance.remove(player);
    }


    public int getViewDistance(Player player) {
        return playerViewDistance.getOrDefault(player, 30); /* Default auf 10 wenn nicht vorhanden */
    }
}
