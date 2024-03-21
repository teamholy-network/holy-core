package de.teamholy.core.bukkit.listener;

import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.PlayerCacheManager;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.events.PlayerNameTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerNameTagListener implements Listener {

    private BukkitCore bukkitCore;

    public PlayerNameTagListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        Bukkit.getPluginManager().registerEvents(this, bukkitCore);
    }

    @EventHandler
    public void onNameTag(PlayerNameTagEvent event) {

        if (!bukkitCore.isTabPrefix()) return;

        Player player = event.getPlayer();
        PlayerCacheManager.CachedBukkitPlayer playerCache = bukkitCore.getPlayerCacheManager().getCachedPlayers().get(player.getUniqueId());
        if (playerCache == null) return;

        PlayerRank playerRank = playerCache.getRank();

        if (playerRank == null) return;

        // Get default values from HolyPlayer
        int sortId = playerRank.getSortId();
        String prefix = playerRank.getTabPrefix();
        String suffix = "";

        // Get PlayerProfiles
        Clan clan = playerCache.getClan();

        // Check and add clan-tag as suffix if exists
        if (clan != null) {
            suffix = " §8[" + clan.getColor() + clan.getTag() + "§8]";
        }

        if (player.getName().equalsIgnoreCase("Gregorr")) {
            suffix = suffix + " §a☃";
            event.setDisplaySuffix(" §c╭ᑎ╮");
        } else if (player.getName().equalsIgnoreCase("Yassino")) {
            suffix = suffix + " §2♫";
            event.setDisplaySuffix(" §2♫");
        }

        // Fake PLAYER rank if we got a nicked player
        if (MarkupAPI.isNicked(player)) {
            sortId = PlayerRank.PLAYER.getSortId();
            prefix = PlayerRank.PLAYER.getTabPrefix();
            suffix = "";
        }

        if (player.getName().equalsIgnoreCase("Koboo")) {
            prefix = "§8[§5Koboo§8] §7";
        }


        // Set the values into the event
        event.setSortId(sortId);
        event.setPrefix(prefix);
        event.setSuffix(suffix);
    }
}
