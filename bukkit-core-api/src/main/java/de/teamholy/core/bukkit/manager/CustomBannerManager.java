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
                DyeColor dyeColor = DyeColor.valueOf(customPattern.getColor());
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









    public List<Pattern> parsePattern(String blockEntityTag) {
        List<Pattern> patterns = new ArrayList<>();

        String patternString = "\\{Pattern:(.*?),Color:(\\d+)}";
        java.util.regex.Pattern patternRegex = java.util.regex.Pattern.compile(patternString);
        Matcher matcher = patternRegex.matcher(blockEntityTag);

        while (matcher.find()) {
            String patternIdentifier = matcher.group(1);
            int color = Integer.parseInt(matcher.group(2));

            DyeColor dyeColor = getColorByData(color);
            PatternType patternType = PatternType.getByIdentifier(patternIdentifier);

            if (dyeColor != null && patternType != null) {
                patterns.add(new Pattern(dyeColor, patternType));
            }
        }

        return patterns;
    }

    public DyeColor getColorByData(int color) {
        return switch (color) {
            case 0 -> DyeColor.WHITE;
            case 1 -> DyeColor.ORANGE;
            case 2 -> DyeColor.MAGENTA;
            case 3 -> DyeColor.LIGHT_BLUE;
            case 4 -> DyeColor.YELLOW;
            case 5 -> DyeColor.LIME;
            case 6 -> DyeColor.PINK;
            case 7 -> DyeColor.GRAY;
            case 8 -> DyeColor.SILVER;
            case 9 -> DyeColor.CYAN;
            case 10 -> DyeColor.PURPLE;
            case 11 -> DyeColor.BLUE;
            case 12 -> DyeColor.BROWN;
            case 13 -> DyeColor.GREEN;
            case 14 -> DyeColor.RED;
            case 15 -> DyeColor.BLACK;
            default -> null;
        };
    }

    public void removeCustomBanner(Player player) {
        ItemStack currentHelmet = player.getInventory().getHelmet();
            if (currentHelmet != null && currentHelmet.getType() == Material.BANNER) {
                player.getInventory().setHelmet(null);

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
