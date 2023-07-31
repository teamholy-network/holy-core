package de.teamholy.core.bukkit.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/* copyright by Greg */

public class PacketManager {

    private BukkitCore bukkitCore;


    public Map<UUID, BukkitTask> gameStatePacketLoopTask = new HashMap<>();

    public Map<UUID, Boolean> hornyJail = new HashMap<>();


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
                    for (int y = 0; y < 200; y++) {
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





    @SuppressWarnings("deprecation")
    public void sendPlayerToHornyJail(Player player) {
        if (player == null) return;

        Bukkit.getScheduler().runTask(bukkitCore, () -> {
            player.playSound(player.getLocation(), Sound.HORSE_ZOMBIE_DEATH, 1.0F, 1.0F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 3));

            Bukkit.getScheduler().runTaskLater(bukkitCore, () -> {
                player.setAllowFlight(true);
                player.setVelocity(player.getLocation().getDirection().multiply(0.5D).setY(3.8D));
                player.setAllowFlight(false);
            }, 40L);

            Bukkit.getScheduler().runTaskLater(bukkitCore, () -> {
                Location playerLocation = player.getLocation();
                World playerWorld = player.getWorld();
                for(int x = -2; x <= 2; x++) {
                    for(int y = 0; y <= 4; y++) {
                        for(int z = -2; z <= 2; z++) {
                            Block block = playerWorld.getBlockAt(playerLocation.getBlockX() + x, playerLocation.getBlockY() + y, playerLocation.getBlockZ() + z);
                            if(y == 4 || y == 0 || x == -2 || x == 2 || z == -2 || z == 2) {
                                if ((x == 2 || x == -2 || z == 2 || z == -2) && y == 2) {
                                    block.setType(Material.GLASS);
                                } else {
                                    block.setType(Material.BEDROCK);
                                }
                            } else {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }


                Block bedBlock1 = playerWorld.getBlockAt(playerLocation.getBlockX(), playerLocation.getBlockY() + 1, playerLocation.getBlockZ());
                Block bedBlock2 = playerWorld.getBlockAt(playerLocation.getBlockX(), playerLocation.getBlockY() + 1, playerLocation.getBlockZ() + 1);

                byte data1 = (byte) 0x0;
                byte data2 = (byte) 0x8;
                bedBlock1.setType(Material.BED_BLOCK);
                bedBlock1.setData(data1);
                bedBlock2.setType(Material.BED_BLOCK);
                bedBlock2.setData(data2);


                playerWorld.getBlockAt(playerLocation.getBlockX() + 1, playerLocation.getBlockY() + 1, playerLocation.getBlockZ()).setType(Material.WORKBENCH);
            }, 60L);
        });
    }













}










