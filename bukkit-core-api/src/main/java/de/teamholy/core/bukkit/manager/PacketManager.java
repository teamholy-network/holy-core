package de.teamholy.core.bukkit.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/* copyright by Greg */

public class PacketManager {

    private BukkitCore bukkitCore;

    public Map<UUID, BukkitTask> gameStatePacketLoopTask = new HashMap<>();



    public PacketManager(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;


    }

    public void GameStatePacket(Player player, int type, float state, boolean loop) {
        if (player == null) return;

        if (loop) {
            gameStatePacketLoop(player, type, state);
        } else {
            sendGameStatePacket(player, type, state);
        }
    }

    private void sendGameStatePacket(Player player, int type, float state) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);

        packet.getIntegers().write(0, type);
        packet.getFloat().write(0, state);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gameStatePacketLoop(Player player, int type, float state) {
        UUID playerUUID = player.getUniqueId();

        BukkitTask existingTask = gameStatePacketLoopTask.get(playerUUID);
        if (existingTask != null) {
            existingTask.cancel();
            gameStatePacketLoopTask.remove(playerUUID);
        } else {
            BukkitTask newTask = new BukkitRunnable() {
                @Override
                public void run() {
                    sendGameStatePacket(player, type, state);
                }
            }.runTaskTimerAsynchronously(bukkitCore, 0L, 0L);

            gameStatePacketLoopTask.put(playerUUID, newTask);
        }
    }




    public void sendBlockChangePacket(Player player, Material material) {

        if (material == null) return;
        if (player == null) return;

        new BukkitRunnable() {
            @Override
            public void run() {

                Location playerOldLocation = player.getLocation();
                for (int x = 0; x < 200; x++) {
                    for (int y = 0; y < 30; y++) {
                        for (int z = 0; z < 200; z++) {
                            if (new Location(playerOldLocation.getWorld(), playerOldLocation.getBlockX() - 100 + x,
                                    playerOldLocation.getBlockY() - 7 + y, playerOldLocation.getBlockZ() - 100 + z)
                                    .getBlock()
                                    .getType() != Material.AIR) {
                                Location l = new Location(playerOldLocation.getWorld(),
                                        playerOldLocation.getBlockX() - 100 + x, playerOldLocation.getBlockY() - 7 + y,
                                        playerOldLocation.getBlockZ() - 100 + z);
                                player.sendBlockChange(l, material, (byte) 0);
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(bukkitCore);
    }


}






