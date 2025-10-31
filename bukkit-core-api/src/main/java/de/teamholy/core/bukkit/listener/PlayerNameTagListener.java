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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerNameTagListener implements Listener {

    private final BukkitCore bukkitCore;

    private static final UUID NOAHLTR_UUID = UUID.fromString("1cfcd3b8-10ff-40a8-b3f9-c61628b5d098");
    private static final UUID WEIBLICHZWOELF_UUID = UUID.fromString("60d97170-3d03-4562-918d-7ff7a493b68e");
    private static final UUID JAVAEXCEPTIONDE_UUID = UUID.fromString("ce397ef0-7973-4ce5-a3b1-3bd6e7fc9970");
    private static final UUID ANGEKLXGTER_UUID = UUID.fromString("7504c806-b491-4303-b6df-9746d4e7b34e");

    private static final UUID GREGORR_UUID = UUID.fromString("eecc3c44-eaaf-48fe-af23-3af762578446");
    private static final UUID YASSINO_UUID = UUID.fromString("fa44c187-80dd-4171-bb5a-2e694c4c8b4f");
    private static final UUID KOBOO_UUID = UUID.fromString("2ce67956-7211-4fec-a7ad-b24f2e355b61");

    private static final Set<UUID> CAT_PREFIX = new HashSet<>(Arrays.asList(
        NOAHLTR_UUID, WEIBLICHZWOELF_UUID, JAVAEXCEPTIONDE_UUID, ANGEKLXGTER_UUID
    ));

    public PlayerNameTagListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        Bukkit.getPluginManager().registerEvents(this, bukkitCore);
    }

    @EventHandler
    public void onNameTag(PlayerNameTagEvent event) {
        if (!bukkitCore.isTabPrefix()) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        PlayerCacheManager.CachedBukkitPlayer cache = bukkitCore.getPlayerCacheManager()
            .getCachedPlayers()
            .get(uuid);
        if (cache == null) return;

        PlayerRank rank = cache.getRank();
        if (rank == null) return;

        int sortId = rank.getSortId();
        String prefix = rank.getTabPrefix();
        String suffix = "";

        Clan clan = cache.getClan();
        if (clan != null) {
            suffix = " §8[" + clan.getColor() + clan.getTag() + "§8]";
        }

        if (MarkupAPI.isNicked(player)) {
            event.setSortId(PlayerRank.PLAYER.getSortId());
            event.setPrefix(PlayerRank.PLAYER.getTabPrefix());
            event.setSuffix("");
            return;
        }

        if (CAT_PREFIX.contains(uuid)) {
            prefix = "§8[§5§lᓚᘏᗢ§8] " + rank.getColorCode();
        } else if (uuid.equals(KOBOO_UUID)) {
            prefix = "§8[§5Koboo§8] §7";
        }

        if (uuid.equals(GREGORR_UUID)) {
            suffix += " §a☃";
            event.setDisplaySuffix(" §c╭ᑎ╮");
        } else if (uuid.equals(YASSINO_UUID)) {
            suffix += " §2♫";
            event.setDisplaySuffix(" §2♫");
        }

        event.setSortId(sortId);
        event.setPrefix(prefix);
        event.setSuffix(suffix);
    }
}