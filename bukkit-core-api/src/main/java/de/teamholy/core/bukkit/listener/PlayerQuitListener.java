package de.teamholy.core.bukkit.listener;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/* copyright by Yassino */
public class PlayerQuitListener implements Listener {


    BukkitCore bukkitCore;
    PacketManager packetmanager;

    public PlayerQuitListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        packetmanager = new PacketManager(bukkitCore);
        Bukkit.getPluginManager().registerEvents(this, bukkitCore);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        bukkitCore.getPerkCache().getPerkPlayerProfileHashMap().remove(playerUUID);
        if (packetmanager.gameStatePacketLoopTask.containsKey(playerUUID)) {
            packetmanager.gameStatePacketLoopTask.get(playerUUID).cancel();
            packetmanager.gameStatePacketLoopTask.remove(playerUUID);
        }

    }
}
