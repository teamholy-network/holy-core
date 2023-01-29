package de.teamholy.core.bukkit.perks;

import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/* copyright by Yassino */
public class UsePerkListener implements Listener {

    public UsePerkListener() {
        Bukkit.getPluginManager().registerEvents(this, BukkitCore.getInstance());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != null || event.getItem() != null || event.getItem().getType() != null || event.getItem().getType() != Material.AIR || event.getItem().getItemMeta() != null) {
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (event.getItem() == null) return;
                PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().get(event.getPlayer().getUniqueId());
                if (perkPlayerProfile == null) return;
                if (BukkitCore.getInstance().getPerkCache().getPerkHashMap().get(perkPlayerProfile.getStickPerk()).getMaterial() == event.getMaterial()) {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        event.setCancelled(true);
                    }
                }
            }
        }

    }

}
