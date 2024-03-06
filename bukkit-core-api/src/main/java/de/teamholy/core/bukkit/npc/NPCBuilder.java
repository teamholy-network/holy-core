package de.teamholy.core.bukkit.npc;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.PlayerCacheManager;
import de.teamholy.core.bukkit.npc.models.NPCEntry;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/* copyright by Yassino */
public class NPCBuilder {

    private final String name, displayName;
    private final UUID skin;
    private final int maxSeeRange, maxTargetRange;
    private final boolean looker, kickBack;
    private final Location location;
    private final List<String> holoLines = new ArrayList<>();

    public NPCBuilder(String name, String displayName, UUID skin, int maxSeeRange, int maxTargetRange, boolean looker, boolean kickBack, Location location) {
        this.name = name;
        this.displayName = displayName;
        this.skin = skin;
        this.maxSeeRange = maxSeeRange;
        this.maxTargetRange = maxTargetRange;
        this.looker = looker;
        this.kickBack = kickBack;
        this.location = location;
    }

    public NPCBuilder addHolo(String... lines) {
        holoLines.addAll(Arrays.asList(lines));
        return this;
    }

    public void build(Player player) {
        PlayerCacheManager.CachedBukkitPlayer cachedBukkitPlayer = BukkitCore.getInstance().getPlayerCacheManager().getCachedPlayers().get(player.getUniqueId());
        if (cachedBukkitPlayer != null) {
            cachedBukkitPlayer.getNpcPlayer().getNpcs().put(name,
                    new NPCEntry(displayName, this.skin, location, maxSeeRange, maxTargetRange, looker, kickBack).setPlayer(player).addHolo(holoLines));
        }
      }
}
