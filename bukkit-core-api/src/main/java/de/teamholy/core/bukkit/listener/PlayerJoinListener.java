package de.teamholy.core.bukkit.listener;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.skin.SkinProfile;
import de.teamholy.core.api.entities.skin.SkinService;
import de.teamholy.core.bukkit.BukkitCore;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerJoinListener implements Listener {

    BukkitCore bukkitCore;

    public PlayerJoinListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        Bukkit.getPluginManager().registerEvents(this, bukkitCore);
    }

    @EventHandler
    // justin
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        SkinService skinService = bukkitCore.getCoreAPI().getSkinService();
        skinService.getEntityAsync(player.getUniqueId(),() -> skinService.getRepository().findFirstById(player.getUniqueId()), skinProfile -> {


            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            GameProfile gameProfile = entityPlayer.getProfile();
            Property property = gameProfile.getProperties().get("textures").iterator().next();
            String value = property.getValue();
            String signature = property.getSignature();



            if (skinProfile == null) skinProfile = new SkinProfile();
            if (!skinProfile.getSignature().equals(signature) || !skinProfile.getValue().equals(value)) {
                skinProfile.setSignature(signature);
                skinProfile.setValue(value);
                skinService.saveEntity(skinProfile,true);
            }

        });

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
    }
}