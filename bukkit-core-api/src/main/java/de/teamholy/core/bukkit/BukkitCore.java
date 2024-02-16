package de.teamholy.core.bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.manager.MetricsManager;
import de.teamholy.core.api.utility.AbstractConfiguration;
import de.teamholy.core.bukkit.commands.XyzCommand;
import de.teamholy.core.bukkit.listener.CloudMessageListener;
import de.teamholy.core.bukkit.listener.PlayerChatListener;
import de.teamholy.core.bukkit.listener.PlayerJoinQuitListener;
import de.teamholy.core.bukkit.manager.CloudMessageManager;
import de.teamholy.core.bukkit.manager.CustomBannerManager;
import de.teamholy.core.bukkit.perks.*;
import de.teamholy.core.bukkit.report.ReportBukkitManager;
import de.teamholy.core.bukkit.task.ServiceAliveTask;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE)

public class BukkitCore extends JavaPlugin {

    @Getter
    private static BukkitCore instance;

    @Getter
    private ProtocolManager protocolManager;

    @Getter
    CloudMessageManager cloudMessageManager;

    CustomBannerManager customBannerManager;

    MetricsManager metricsManager;

    @Getter
    CoreAPI coreAPI;
    @Getter
    PerkCache perkCache;
    @Getter
    PerkManager perkManager;
    @Getter
    String group;

    public static boolean RESTART = false;

    public BukkitCore() {
        instance = this;
    }

    final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);

    @Override
    public void onEnable() {
        coreAPI = new CoreAPI();
        metricsManager = new MetricsManager(this.coreAPI);
        perkCache = new PerkCache();
        perkManager = new PerkManager(this);
        cloudMessageManager = new CloudMessageManager(this);

        System.out.println("Starting ServiceAliveTask\n");
        Bukkit.getScheduler().runTaskTimer(this, new ServiceAliveTask(), 0, 20*5);
        System.out.println("\nStarted ServiceAliveTask");

        group = Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName().split("-")[0];

        getCommand("reportsgui").setExecutor(new ReportBukkitManager());
        getCommand("stopcore").setExecutor(new de.teamholy.core.bukkit.commands.StopCommand());
        protocolManager = ProtocolLibrary.getProtocolManager();

        new PlayerJoinQuitListener(this);
        new CustomBannerManager(this);
        new UsePerkListener();
        new CloudMessageListener(this);
        new PlayerChatListener(this);

        getCommand("xyz").setExecutor(new XyzCommand(this));

        customBannerManager = new CustomBannerManager(this);


        Perk defaultStick = new Perk(100, "Stick", Material.STICK, (byte) 0, PerkType.STICK, -1, PerkRankType.PLAYER, null);

        Perk defaultBlock = new Perk(0, "Sandstone", Material.SANDSTONE, (byte) 0, PerkType.BLOCK, -1, PerkRankType.PLAYER, null);

        Perk chat = new Perk(200, "7-Grey", Material.INK_SACK, (byte) 7, PerkType.CHAT, -1, PerkRankType.PLAYER, null);

        Perk cBanner = new Perk(99999, "Custom Banner", Material.BANNER, (byte) 0, PerkType.CBANNER, 15000, PerkRankType.PLAYER, null);

        AbstractConfiguration configuration = new AbstractConfiguration(new File("plugins/core"), "perks");
        configuration.load();
        configuration.append("default.stick", 100, true);
        configuration.append("default.block", 0, true);
        configuration.append("default.chat", 200, true);
        configuration.append("perks.block", List.of(defaultBlock), false);
        configuration.append("perks.stick", List.of(defaultStick), false);
        configuration.append("perks.chat", List.of(chat), false);
        configuration.save();

        configuration.getList("perks.block", Perk.class).forEach(o -> {
            Perk perk = (Perk) o;
            System.out.println(perk.getMaterial() + String.valueOf(perk.getSubId()));
            if (perk.getMaterial() != null) {
                getPerkCache().getPerkHashMap().put(perk.getId(), perk);
            }
        });

        configuration.getList("perks.chat", Perk.class).forEach(o -> {
            Perk perk = (Perk) o;
            if (perk.getMaterial() != null) {
                getPerkCache().getPerkHashMap().put(perk.getId(), perk);
            }
        });


        configuration.getList("perks.stick", Perk.class).forEach(o -> {
            Perk perk = (Perk) o;
            if (perk.getMaterial() != Material.BANNER) {
                perk.setBannerMeta(null, null);
            }
            if (perk.getMaterial() != null) {
                getPerkCache().getPerkHashMap().put(perk.getId(), perk);
            }
        });

        getPerkCache().getPerkHashMap().put(99999, cBanner);


        BukkitCore.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(BukkitCore.getInstance(), () -> {

            cloudMessageManager.sendBungeeReport("bungee", "ohio:report");

        }, 0, 50);


    }

    @Override
    public void onDisable() {
        coreAPI.getMetricsManager().removeMetric(this.getServer().getServerName());

        coreAPI.onDisable();


        executorService.shutdown();
    }

    public static CoreAPI getAPI() {
        return instance.getCoreAPI();
    }
}
