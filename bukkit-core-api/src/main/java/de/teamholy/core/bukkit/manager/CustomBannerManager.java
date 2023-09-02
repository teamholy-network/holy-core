package de.teamholy.core.bukkit.manager;

import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class CustomBannerManager {

    private BukkitCore bukkitCore;

    private Map<Player, Boolean> playerBannerMap = new HashMap<>();

    public CustomBannerManager(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
    }

    public void setAndPlaceCustomBanner(Player player, String baseColor) {

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ItemStack banner = new ItemStack(Material.BANNER);
                    BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

                    if (bannerMeta != null) {
                        bannerMeta.setBaseColor(DyeColor.valueOf(baseColor));
                        banner.setItemMeta(bannerMeta);
                    }

                    player.getInventory().setHelmet(banner);
                    playerBannerMap.put(player, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }.runTaskLater(bukkitCore, 20L);




    }

    public void removeCustomBanner(Player player) {
        if (playerBannerMap.containsKey(player) && playerBannerMap.get(player)) {
            ItemStack currentHelmet = player.getInventory().getHelmet();
            if (currentHelmet != null && currentHelmet.getType() == Material.BANNER) {
                player.getInventory().setHelmet(null);
                playerBannerMap.remove(player);
                System.out.println("removed custom banner");
            }
        }
    }



}
