package de.teamholy.core.bukkit.npc.tasks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class UpdateLookTask {
    public UpdateLookTask() {
        (new BukkitRunnable() {
            public void run() {
                try {
                    for (NPCPlayer npcPlayerEntry : BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().values()) {
                        npcPlayerEntry.getNpcs().values().forEach(npcEntry -> {
                            Player player = npcEntry.getPlayer();

                            if (npcEntry.isInRange(player))
                                npcEntry.updateSkin();

                            if (npcEntry.isLooker() && player != null && npcEntry.getLocation().getWorld().getUID().equals(player.getWorld().getUID()) && npcEntry.getLocation().distance(player.getLocation()) <= npcEntry.getMaxTargetRange()) {
                                npcEntry.createTargetLocation(player);
                            }

                            if (npcEntry.isKickBack() && player != null && npcEntry.getLocation().getWorld().getUID().equals(player.getWorld().getUID()) && npcEntry.getLocation().distance(player.getLocation()) <= 1.8D ) {
                                player.setVelocity(player.getLocation().getDirection().clone().multiply(-0.5).normalize());
                                player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1.0F, 1.0F);
                                npcEntry.animation(player,0);
                                Bukkit.getScheduler().runTaskLater(BukkitHolyAPI.getInstance(),() -> {
                                    forceEmote(player,npcEntry.getUuid(),37);
                                },1);
                            }
                        });
                    }
                } catch (Exception ignored) {}
            }
        }).runTaskTimerAsynchronously(BukkitHolyAPI.getInstance(), 0L, 1L);
    }

    private void forceEmote(Player receiver, UUID npcUUID, int emoteId ) {
        // List of all forced emotes
        JsonArray array = new JsonArray();

        // Emote and target NPC
        JsonObject forcedEmote = new JsonObject();
        forcedEmote.addProperty( "uuid", npcUUID.toString() );
        forcedEmote.addProperty( "emote_id", emoteId );
        array.add(forcedEmote);

        // Send to LabyMod using the API
        ILabyMod.sendLMCMessage( receiver, "emote_api", array );
    }

}
