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

import java.util.UUID;

public class PlayerNameTagListener implements Listener {

    private BukkitCore bukkitCore;

    private static final UUID NoahLTR_UUID = UUID.fromString("1cfcd3b8-10ff-40a8-b3f9-c61628b5d098");
    private static final UUID WeiblichZwoelf_UUID = UUID.fromString("60d97170-3d03-4562-918d-7ff7a493b68e");
    private static final UUID JavaExceptionDE_UUID = UUID.fromString("ce397ef0-7973-4ce5-a3b1-3bd6e7fc9970");
    private static final UUID angeklxgter_UUID = UUID.fromString("7504c806-b491-4303-b6df-9746d4e7b34e");

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

        UUID uuid = player.getUniqueId();

        if (uuid.equals(NoahLTR_UUID)) {
            prefix = "§8[§5ᓚᘏᗢ§8] §7";
        } else if (uuid.equals(WeiblichZwoelf_UUID)) {
            prefix = "§8[§5ᓚᘏᗢ§8] §7";
        } else if (uuid.equals(JavaExceptionDE_UUID)) {
            prefix = "§8[§5ᓚᘏᗢ§8] §7";
        } else if (uuid.equals(angeklxgter_UUID)) {
            prefix = "§8[§5ᓚᘏᗢ§8] §7";
        }


        // Set the values into the event
        event.setSortId(sortId);
        event.setPrefix(prefix);
        event.setSuffix(suffix);
    }
}
