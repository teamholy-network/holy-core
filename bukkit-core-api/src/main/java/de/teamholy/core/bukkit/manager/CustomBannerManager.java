package de.teamholy.core.bukkit.manager;



import de.teamholy.core.api.entities.banner.Banner;
import de.teamholy.core.api.entities.banner.BannerRepository;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.block.banner.Pattern;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;


public class CustomBannerManager {

    private BukkitCore bukkitCore;

    private HashMap<Integer, Banner> bannerHashMap = new HashMap<>();


    public CustomBannerManager(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        loadBanners();
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
        }.runTaskLater(bukkitCore, 0L);




    }

    public void setAndPlaceCustomBanner1(Player player, int bannerId) {

        Banner cBanner = bannerHashMap.get(bannerId);

        if (cBanner != null) {
            List<Pattern> bukkitPatterns = new ArrayList<>();

            for (de.teamholy.core.api.entities.banner.Banner.Pattern customPattern : cBanner.getPatterns()) {
                DyeColor dyeColor = colorMapper(customPattern.getColor());
                PatternType patternType = PatternType.valueOf(customPattern.getPattern());

                Pattern bukkitPattern = new org.bukkit.block.banner.Pattern(dyeColor, patternType);
                bukkitPatterns.add(bukkitPattern);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        ItemStack banner = new ItemStack(Material.BANNER);
                        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

                        if (bannerMeta != null) {
                            bannerMeta.setBaseColor(DyeColor.valueOf(cBanner.getBaseColor()));
                            bannerMeta.setPatterns(bukkitPatterns);
                            banner.setItemMeta(bannerMeta);
                        }

                        player.getInventory().setHelmet(banner);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskLater(bukkitCore, 0L);
        } else {
            return;
        }
    }

    public void removeCustomBanner(Player player) {
        ItemStack currentHelmet = player.getInventory().getHelmet();
            if (currentHelmet != null && currentHelmet.getType() == Material.BANNER) {
                player.getInventory().setHelmet(null);

            }
     }

    private DyeColor colorMapper(String colorName) {
        switch (colorName) {
            case "LIGHT_PURPLE": return DyeColor.MAGENTA;
            default: return DyeColor.valueOf(colorName);
        }
    }



    public void loadBanners() {
        try {
            BannerRepository bannerRepository = bukkitCore.getCoreAPI().getBannerService().getRepository();

            bannerRepository.findAll().forEach(banner -> {


               Integer bannerId = Integer.parseInt(banner.getId());
                bannerHashMap.put(bannerId, banner);

                System.out.println("Banner " + banner.getName() + " loaded");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
