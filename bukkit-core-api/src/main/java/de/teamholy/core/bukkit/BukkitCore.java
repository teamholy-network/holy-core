package de.teamholy.core.bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.utility.AbstractConfiguration;
import de.teamholy.core.bukkit.listener.CloudMessageListener;
import de.teamholy.core.bukkit.listener.PlayerJoinListener;
import de.teamholy.core.bukkit.listener.PlayerQuitListener;
import de.teamholy.core.bukkit.perks.*;
import de.teamholy.core.bukkit.report.ReportBukkitManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)

public class BukkitCore extends JavaPlugin {

    @Getter
    private static BukkitCore instance;

    @Getter
    private ProtocolManager protocolManager;

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

        getCommand("reportsgui").setExecutor(new ReportBukkitManager());
        protocolManager = ProtocolLibrary.getProtocolManager();

        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new UsePerkListener();
        new CloudMessageListener(this);

        Perk defaultStick =
                new Perk(100, "Stick", Material.STICK, (byte) 0, PerkType.STICK, -1, PerkRankType.PLAYER, null);


        Perk defaultBlock =
                new Perk(0, "Sandstone", Material.SANDSTONE, (byte) 0, PerkType.BLOCK, -1, PerkRankType.PLAYER, null);


        Perk chat =
                new Perk(200, "7-Grey", Material.INK_SACK, (byte) 7, PerkType.CHAT, -1, PerkRankType.PLAYER, null);


        AbstractConfiguration configuration = new AbstractConfiguration(new File("plugins/core"),"perks");
        configuration.load();
        configuration.append("default.stick",100,true);
        configuration.append("default.block",0,true);
        configuration.append("default.chat",200,true);
        configuration.append("perks.block", List.of(defaultBlock),false);
        configuration.append("perks.stick", List.of(defaultStick),false);
        configuration.append("perks.chat", List.of(chat),false);
        configuration.save();

        configuration.getList("perks.block",Perk.class).forEach(o -> {
            Perk perk = (Perk) o;
            System.out.println(perk.getMaterial() + String.valueOf(perk.getSubId()));
            if (perk.getMaterial() != null) {
                getPerkCache().getPerkHashMap().put(perk.getId(), perk);
            }
        });

        configuration.getList("perks.chat",Perk.class).forEach(o -> {
            Perk perk = (Perk) o;
            if (perk.getMaterial() != null) {
                getPerkCache().getPerkHashMap().put(perk.getId(), perk);
            }
        });



        configuration.getList("perks.stick",Perk.class).forEach(o -> {
            Perk perk = (Perk) o;
            if (perk.getMaterial() != Material.BANNER) {
                perk.setBannerMeta(null,null);
            }
            if (perk.getMaterial() != null) {
                getPerkCache().getPerkHashMap().put(perk.getId(), perk);
            }
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
