package de.teamholy.core.bukkit.listener;

import com.avaje.ebean.validation.Email;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/* copyright by Yassino */
public class PlayerQuitListener implements Listener {


    BukkitCore bukkitCore;

    public PlayerQuitListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        Bukkit.getPluginManager().registerEvents(this, bukkitCore);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        bukkitCore.getPerkCache().getPerkPlayerProfileHashMap().remove(event.getPlayer().getUniqueId());
    }
}
