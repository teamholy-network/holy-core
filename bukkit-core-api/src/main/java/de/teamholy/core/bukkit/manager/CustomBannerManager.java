package de.teamholy.core.bukkit.manager;



import de.teamholy.core.api.entities.banner.Banner;
import de.teamholy.core.api.entities.banner.BannerRepository;
import de.teamholy.core.api.utility.CustomBanner;
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

    public CustomBannerManager(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
    }

    public void setAndPlaceCustomBanner1(Player player, CustomBanner customBanner) {

        if (customBanner != null && customBanner.isActivated()) {

            List<Pattern> bukkitPatterns = new ArrayList<>();

            for (CustomBanner.Pattern customPattern : customBanner.getPatterns()) {
                DyeColor dyeColor = colorMapper(customPattern.getColor().toUpperCase());
                PatternType patternType = PatternType.valueOf(customPattern.getPatternName().toUpperCase());  // Use getPatternName()

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
                            bannerMeta.setBaseColor(DyeColor.valueOf(customBanner.getBaseColor().toUpperCase()));
                            bannerMeta.setPatterns(bukkitPatterns);
                            banner.setItemMeta(bannerMeta);
                        }

                        player.getInventory().setHelmet(banner);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskLater(bukkitCore, 0L);
        }

    }


    public void removeCustomBanner(Player player) {
        ItemStack currentHelmet = player.getInventory().getHelmet();
        if (currentHelmet != null && currentHelmet.getType() == Material.BANNER) {
            player.getInventory().setHelmet(null);

        }
    }

    private DyeColor colorMapper(String colorName) {
        return switch (colorName) {
            case "LIGHT_PURPLE" -> DyeColor.MAGENTA;
            case "LIGHT_GRAY" -> DyeColor.SILVER;
            case "DARK_GRAY" -> DyeColor.GRAY;
            case "LIGHT_GREEN" -> DyeColor.LIME;
            default -> DyeColor.valueOf(colorName);
        };
    }





}
