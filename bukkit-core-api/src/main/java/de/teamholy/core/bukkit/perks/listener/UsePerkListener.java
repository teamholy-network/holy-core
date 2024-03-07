package de.teamholy.core.bukkit.perks.listener;

import com.google.common.collect.Maps;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.model.Perk;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

/* copyright by Yassino */
public class UsePerkListener implements Listener {

    private final HashMap<Player, Integer> randomColor = Maps.newHashMap();

    public UsePerkListener() {
        Bukkit.getPluginManager().registerEvents(this, BukkitCore.getInstance());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != null || event.getItem() != null || event.getItem().getType() != null || event.getItem().getType() != Material.AIR || event.getItem().getItemMeta() != null) {
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (event.getItem() == null) return;
                PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getPlayerCacheManager().getCachedPlayers().get(event.getPlayer().getUniqueId()).getPerkPlayerProfile();
                if (perkPlayerProfile == null) return;
                if (BukkitCore.getInstance().getPerkManager().getPerkHashMap().get(perkPlayerProfile.getStickPerk()).getMaterial() == event.getMaterial()) {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        event.setCancelled(true);
                    }
                }
            }
        }

    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getPlayerCacheManager().getCachedPlayers().get(event.getPlayer().getUniqueId()).getPerkPlayerProfile();
        if (perkPlayerProfile == null) return;
        Perk perk = BukkitCore.getInstance().getPerkManager().getPerkHashMap().get(perkPlayerProfile.getBlockPerk());



        if (ChatColor.stripColor(perk.getName()).toLowerCase().contains("rainbow") &&
            (event.getBlock().getType() == Material.WOOL || event.getBlock().getType() == Material.STAINED_GLASS)) {            Player player = event.getPlayer();
            if (!randomColor.containsKey(player)) randomColor.put(player, 0);


            event.getBlock().setType(event.getBlock().getType());
            event.getBlock().setData(randomColor.get(player).byteValue());

            if (randomColor.get(player) == 15) {
                randomColor.put(player, 0);
            } else {
                randomColor.put(player, randomColor.get(player) + 1);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        randomColor.remove(event.getPlayer());
    }

}
