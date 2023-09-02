package de.teamholy.core.bukkit.manager;

import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.DyeColor;

import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scheduler.BukkitRunnable;



public class CustomBannerManager {

    private BukkitCore bukkitCore;

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
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }.runTaskLater(bukkitCore, 20L);




    }

    public void removeCustomBanner(Player player) {
        ItemStack currentHelmet = player.getInventory().getHelmet();
            if (currentHelmet != null && currentHelmet.getType() == Material.BANNER) {
                player.getInventory().setHelmet(null);

            }
     }



}
