package de.teamholy.core.bukkit.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.entity.Player;

/* copyright by Greg */

public class PacketManager {

    private BukkitCore bukkitCore;

    public PacketManager(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
    }

    public void sendGameStatePacket(Player player, int type, float state) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);

        packet.getIntegers().write(0, type);
        packet.getFloat().write(0, state);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
