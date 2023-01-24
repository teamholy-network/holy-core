package de.teamholy.core.bukkit.listener;

import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.player.PlayerService;
import de.teamholy.core.bukkit.BukkitCore;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JoinListener {

    BukkitCore bukkitCore;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerService playerService = bukkitCore.getCoreAPI().getPlayerService();

        // Get entity from service, findFirstById is called if entity can't be found in redis by key.
        PlayerProfile playerProfile = playerService.getEntity(player.getUniqueId(),
                () -> playerService.getRepository().findFirstById(player.getUniqueId()));

        // Create new player profile
        if(playerProfile == null) {
            playerProfile = new PlayerProfile();
            playerProfile.setPlayerId(player.getUniqueId());
            playerProfile.setOnline(true);
            playerProfile.setCoins(0);
            playerProfile.setOnlineTime(0);
            playerProfile.setJoinMeTokens(0);
            playerProfile.setStatsResetTokens(0);
            playerProfile.setServerName("lobby");
        }

        // Update this values always
        playerProfile.setPlayerName(player.getName());
        playerProfile.setIp(player.getAddress().getAddress().getHostAddress());
        playerProfile.setServerName("lobby");

        // Save entity through service
        playerService.saveEntity(playerProfile, true);

        // Save directly to database
        playerService.getRepository().save(playerProfile);
    }
}