package de.teamholy.core.bukkit;

import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.utility.AbstractConfiguration;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.listener.PlayerJoinListener;
import de.teamholy.core.bukkit.listener.PlayerQuitListener;
import de.teamholy.core.bukkit.perks.*;
import eu.koboo.yaml.Yaml;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)

public class BukkitCore extends JavaPlugin {

    @Getter
    private static BukkitCore instance;

    @Getter
    CoreAPI coreAPI;
    @Getter
    PerkCache perkCache;
    @Getter
    PerkManager perkManager;
    @Getter
    String group;

    public BukkitCore() {
        instance = this;
    }

    @Override
    public void onEnable() {
        coreAPI = new CoreAPI();
        perkCache = new PerkCache();
        perkManager = new PerkManager();
        group = Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName().split("-")[0];

        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new UsePerkListener();

        getPerkCache().getPerkHashMap().put(100,
                new Perk(100, "Stick", Material.STICK, (byte) 0, PerkType.STICK, -1, PerkRankType.PLAYER, null));

        getPerkCache().getPerkHashMap().put(101,
                new Perk(101, "Blazerod", Material.BLAZE_ROD, (byte) 0, PerkType.STICK, -1, PerkRankType.PREMIUM, null));

        getPerkCache().getPerkHashMap().put(102,
                new Perk(102, "Bone", Material.BONE, (byte) 0, PerkType.STICK, 1000, null, null));

        getPerkCache().getPerkHashMap().put(103,
                new Perk(103, "Feather", Material.FEATHER, (byte) 0, PerkType.STICK, -1, PerkRankType.PREMIUM, null));

        getPerkCache().getPerkHashMap().put(104,
                new Perk(104, "Raw fish", Material.RAW_FISH, (byte) 0, PerkType.STICK, -1, PerkRankType.PREMIUM, null));

        getPerkCache().getPerkHashMap().put(105,
                new Perk(105, "Puffer fish", Material.RAW_FISH, (byte) 3, PerkType.STICK, -1, PerkRankType.PREMIUM, null));

        getPerkCache().getPerkHashMap().put(106,
                new Perk(106, "Germany banner", Material.BANNER, (byte) 0, PerkType.STICK, 1100, null, null, DyeColor.BLACK,
                        Arrays.asList(new Pattern(DyeColor.YELLOW, PatternType.STRIPE_LEFT), new Pattern(DyeColor.RED, PatternType.STRIPE_CENTER), new Pattern(DyeColor.BLACK, PatternType.STRIPE_RIGHT))
                ));


        getPerkCache().getPerkHashMap().put(0,
                new Perk(0, "Sandstone", Material.SANDSTONE, (byte) 0, PerkType.BLOCK, -1, PerkRankType.PLAYER, null));

        getPerkCache().getPerkHashMap().put(1,
                new Perk(1, "Red sandstone", Material.RED_SANDSTONE, (byte) 0, PerkType.BLOCK, -1, PerkRankType.PREMIUM, null));

        getPerkCache().getPerkHashMap().put(2,
                new Perk(2, "Stone brick", Material.SMOOTH_BRICK, (byte) 0, PerkType.BLOCK, 1000, null, null));

        getPerkCache().getPerkHashMap().put(3,
                new Perk(3, "Nether brick", Material.NETHER_BRICK, (byte) 0, PerkType.BLOCK, -1, PerkRankType.PREMIUM, null));

        getPerkCache().getPerkHashMap().put(4,
                new Perk(4, "Brick", Material.BRICK, (byte) 0, PerkType.BLOCK, -1, PerkRankType.PREMIUM, null));

        getPerkCache().getPerkHashMap().put(5,
                new Perk(5, "Black clay", Material.STAINED_CLAY, (byte) 15, PerkType.BLOCK, -1, PerkRankType.PREMIUM, null));

        getPerkCache().getPerkHashMap().put(6,
                new Perk(6, "White clay", Material.STAINED_CLAY, (byte) 0, PerkType.BLOCK, 1100, null, null));


        getPerkCache().getPerkHashMap().put(200,
                new Perk(200, "7-Grey", Material.INK_SACK, (byte) 7, PerkType.CHAT, -1, PerkRankType.PLAYER, null)
        );
        getPerkCache().getPerkHashMap().put(201,
                new Perk(201, "a-Light Green", Material.INK_SACK, (byte) 10, PerkType.CHAT, -1, PerkRankType.PREMIUM, null)
        );
        getPerkCache().getPerkHashMap().put(202,
                new Perk(202, "2-Green", Material.INK_SACK, (byte) 2, PerkType.CHAT, 1000, null, null)
        );
        getPerkCache().getPerkHashMap().put(203,
                new Perk(203, "9-Blue", Material.INK_SACK, (byte) 4, PerkType.CHAT, -1, PerkRankType.PREMIUM, null)
        );
        getPerkCache().getPerkHashMap().put(204,
                new Perk(204, "b-Light Blue", Material.INK_SACK, (byte) 12, PerkType.CHAT, -1, PerkRankType.PREMIUM, Arrays.asList(Gamemodes.MLGRUSH,Gamemodes.BRIDGE))
        );
        getPerkCache().getPerkHashMap().put(205,
                new Perk(205, "c-Light Red", Material.INK_SACK, (byte) 1, PerkType.CHAT, 1500, PerkRankType.PLAYER, Arrays.asList(Gamemodes.values()))
        );

        AbstractConfiguration configuration = new AbstractConfiguration(new File("plugins/core"),"perks");
        configuration.load();
        configuration.append("default.stick",100,true);
        configuration.append("default.block",0,true);
        configuration.append("default.chat",200,true);
        configuration.append("perks.block",getPerkCache().getPerkHashMap().values().stream().filter(perk -> perk.getPerkType() == PerkType.BLOCK).collect(Collectors.toList()),true);
        configuration.append("perks.stick",getPerkCache().getPerkHashMap().values().stream().filter(perk -> perk.getPerkType() == PerkType.STICK).collect(Collectors.toList()),true);
        configuration.append("perks.chat",getPerkCache().getPerkHashMap().values().stream().filter(perk -> perk.getPerkType() == PerkType.CHAT).collect(Collectors.toList()),true);
        configuration.save();

        configuration.getList("perks.block",Perk.class).forEach(o -> {
            Perk perk = (Perk) o;
            System.out.println(perk.getName());
        });

    }

    @Override
    public void onDisable() {
        coreAPI.onDisable();
    }

    public static CoreAPI getAPI() {
        return instance.getCoreAPI();
    }
}
