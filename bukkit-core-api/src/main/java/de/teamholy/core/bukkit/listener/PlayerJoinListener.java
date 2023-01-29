package de.teamholy.core.bukkit.listener;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerService;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.skin.SkinProfile;
import de.teamholy.core.api.entities.skin.SkinService;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.perks.Perk;
import de.teamholy.core.bukkit.perks.PerkRankType;
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
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        bukkitCore.getCoreAPI().getExecutor().submit(() -> {
            SkinService skinService = bukkitCore.getCoreAPI().getSkinService();
            PerkPlayerService perkPlayerService = bukkitCore.getCoreAPI().getPerkPlayerService();

            SkinProfile skinProfile = skinService.getEntity(player.getUniqueId(), () -> skinService.getRepository().findFirstById(player.getUniqueId()));
//            PerkPlayerProfile perkPlayerProfile = perkPlayerService.getEntity(player.getUniqueId(),() -> perkPlayerService.getRepository().findFirstById(player.getUniqueId()));
//
//            Perk stick = bukkitCore.getPerkCache().getPerkHashMap().get(perkPlayerProfile.getStickPerk());
//            Perk block = bukkitCore.getPerkCache().getPerkHashMap().get(perkPlayerProfile.getBlockPerk());
//            Perk chat = bukkitCore.getPerkCache().getPerkHashMap().get(perkPlayerProfile.getChatPerk());
//
//            boolean needUpdate = false;
//
//            if (!player.hasPermission(PerkRankType.PREMIUM.getPermission())) {
//                if (!stick.isBuyAble() && stick.getId() != 100) needUpdate = true;
//                if (!block.isBuyAble() && block.getId() != 0) needUpdate = true;
//                if (!chat.isBuyAble() && chat.getId() != 200) needUpdate = true;
//            }
//
//            if (needUpdate){
//                perkPlayerProfile.setStickPerk(100);
//                perkPlayerProfile.setChatPerk(200);
//                perkPlayerProfile.setBlockPerk(0);
//                bukkitCore.getCoreAPI().getPerkPlayerService().saveEntity(perkPlayerProfile,true,true);
//            }

            //bukkitCore.getPerkCache().getPerkPlayerProfileHashMap().put(player.getUniqueId(),perkPlayerProfile);


            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            GameProfile gameProfile = entityPlayer.getProfile();
            Property property = gameProfile.getProperties().get("textures").iterator().next();
            String value = property.getValue();
            String signature = property.getSignature();


            if (skinProfile == null) skinProfile = new SkinProfile();
            if (skinProfile.getValue() == null|| !skinProfile.getSignature().equals(signature) || !skinProfile.getValue().equals(value)) {
                skinProfile.setPlayerId(player.getUniqueId());
                skinProfile.setSignature(signature);
                skinProfile.setValue(value);
                skinService.saveEntity(skinProfile,true,true);
            }

        });
    }
}