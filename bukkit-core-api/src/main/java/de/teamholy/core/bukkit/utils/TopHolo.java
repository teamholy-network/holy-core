package de.teamholy.core.bukkit.utils;


import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.collect.Maps;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.redisson.api.RScoredSortedSet;
import org.redisson.client.protocol.ScoredEntry;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class TopHolo {

    private Hologram hologram;
    private StatsType statsType = StatsType.ALLTIME;
    private long cooldown = System.currentTimeMillis();
    private Gamemodes gamemode;

    private static final String TOP_10_HEADER_FOOTER = "§8§m----------§f§lTOP 10§8§m----------";
    private static final String EMPTY_PLAYER_LINE = "§7-/-";

    private final HashMap<StatsType, HashMap<Integer, TopPlayer>> top = new HashMap<>();

    public TopHolo(Location location, Gamemodes gamemode, ItemStack itemStack) {
        for (StatsType value : StatsType.values()) {
            top.put(value, Maps.newHashMap());
        }

        this.gamemode = gamemode;
        hologram = HologramsAPI.createHologram(BukkitCore.getInstance(), location);
        hologram.appendItemLine(itemStack);
        for (int i = 0; i < 13; i++) {
            hologram.appendTextLine("Loading...").setTouchHandler((player) -> {
                if (cooldown > System.currentTimeMillis()) {
                    player.sendMessage("§cPlease wait");
                    return;
                }

                cooldown = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 100);


                switch (statsType) {
                    case DAILY -> statsType = StatsType.ALLTIME;
                    case MONTHLY -> statsType = StatsType.DAILY;
                    case ALLTIME -> statsType = StatsType.MONTHLY;
                }


                updateHologram(StatsType.ALLTIME);
            });
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(BukkitCore.getInstance(), () -> {
            for (StatsType value : StatsType.values()) {
                top.put(value, refreshTop(value));
            }

            Bukkit.getScheduler().runTask(BukkitCore.getInstance(), () -> updateHologram(StatsType.ALLTIME));
        }, 10, 20 * 60 * 5);
    }


    private void updateHologram(StatsType statsType) {
        TextLine textLine = (TextLine) hologram.getLine(1);
        textLine.setText(TOP_10_HEADER_FOOTER);
        for (int i = 2; i <= 11; i++) {
            TopPlayer topPlayer = top.get(statsType).get(i - 1);
            int value = (int) Math.round(topPlayer.value);
            textLine = (TextLine) hologram.getLine(i);
            if (topPlayer != null) {
                textLine.setText("§7#§6" + topPlayer.rank + " §8︳ " + topPlayer.name + " §8» §a" + value + " §cTrophies §8× " + TrophieLeague.getEloRank(value).getName());
            } else {
                textLine.setText(EMPTY_PLAYER_LINE);
            }
        }

        TextLine lastLine = (TextLine) hologram.getLine(12);
        switch (statsType) {
            case DAILY -> {
                this.statsType = StatsType.ALLTIME;
                lastLine.setText("§c§lALLTIME §8︳ §7Monthly §8︳ §7Daily");
            }
            case MONTHLY -> {
                this.statsType = StatsType.DAILY;
                lastLine.setText("§7Alltime §8︳ §7Monthly §8︳ §a§lDAILY");
            }
            case ALLTIME -> {
                this.statsType = StatsType.MONTHLY;
                lastLine.setText("§7Alltime §8︳ §e§lMONTHLY §8︳ §7Daily");
            }
        }
        ((TextLine) hologram.getLine(13)).setText(TOP_10_HEADER_FOOTER);

    }


    private HashMap<Integer, TopPlayer> refreshTop(StatsType statsType) {
        HashMap<Integer, TopPlayer> top = Maps.newHashMap();
        RScoredSortedSet scoredSortedSet = BukkitCore.getAPI().getRedissonManager().getRedissonClient().getScoredSortedSet(gamemode.toString() + "_" + statsType.toString());
        scoredSortedSet.entryRangeReversed(0, 9).forEach((entry) -> {
            ScoredEntry<UUID> scoredEntry = (ScoredEntry<UUID>) entry;
            PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getEntity(scoredEntry.getValue(),
                () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(scoredEntry.getValue()));

            int rank = top.size() + 1;

            top.put(rank, new TopPlayer(PlayerRank.valueOf(playerProfile.getRank()).getColorCode() + playerProfile.getPlayerName(), scoredEntry.getScore(), rank));
        });

        return top;
    }

    public record TopPlayer(String name, double value, int rank) {
    }

}
