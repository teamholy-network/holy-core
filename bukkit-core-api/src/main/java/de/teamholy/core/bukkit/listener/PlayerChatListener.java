package de.teamholy.core.bukkit.listener;

import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.PlayerCacheManager;
import de.teamholy.core.bukkit.perks.model.Perk;
import eu.koboo.markup.MarkupAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerChatListener implements Listener {

    BukkitCore bukkitCore;



    public PlayerChatListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        Bukkit.getPluginManager().registerEvents(this, bukkitCore);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (BukkitCore.getInstance().isChatPrefix()) {
            String message = event.getMessage().replace("%","%%");
            Player player = event.getPlayer();

            if(MarkupAPI.isNicked(player)) {
                event.setFormat(PlayerRank.PLAYER.getChatPrefix() + player.getDisplayName() + " §8» §7" +  message);
                return;
            }

            PlayerCacheManager.CachedBukkitPlayer cachedBukkitPlayer = BukkitCore.getInstance().getPlayerCacheManager().getCachedPlayers().get(player.getUniqueId());
            Perk perk = BukkitCore.getInstance().getPerkManager().getPerkHashMap().get(cachedBukkitPlayer.getPerkPlayerProfile().getChatPerk());
            if (perk == null) {
                event.setCancelled(true);
                return;
            }
            String[] color = perk.getName().split("-");
            event.setFormat(cachedBukkitPlayer.getRank().getChatPrefix() + event.getPlayer().getName() + " §8» §" + color[0] + message);
        }

    }
}
