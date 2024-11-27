package de.teamholy.core.bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.manager.MetricsManager;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.commands.*;
import de.teamholy.core.bukkit.config.ChatTabConfig;
import de.teamholy.core.bukkit.listener.*;
import de.teamholy.core.bukkit.manager.*;
import de.teamholy.core.bukkit.npc.NPCService;
import de.teamholy.core.bukkit.perks.*;
import de.teamholy.core.bukkit.perks.listener.UsePerkListener;
import de.teamholy.core.bukkit.report.ReportBukkitManager;
import de.teamholy.core.bukkit.task.BukkitHealthTask;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import eu.koboo.markup.MarkupAPI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@FieldDefaults(level = AccessLevel.PRIVATE)

@Setter
@Getter
public class BukkitCore extends JavaPlugin {

    @Getter
    private static BukkitCore instance;

    ProtocolManager protocolManager;
    CloudMessageManager cloudMessageManager;


    CustomBannerManager customBannerManager;
    MetricsManager metricsManager;
    CoreAPI coreAPI;
    PerkManager perkManager;
    ChatTabConfig chatTabConfig;
    LocationManager locationManager;
    PlayerCacheManager playerCacheManager;
    StatsManager statsManager;
    NPCService npcService;


    public static String PREFIX = "§6Teamholy §8× §7";


    String group;


    boolean chatPrefix;
    boolean tabPrefix;
    public static boolean RESTART = false;

    public BukkitCore() {
        instance = this;
    }

    final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);

    @Override
    public void onEnable() {
        coreAPI = new CoreAPI();
        protocolManager = ProtocolLibrary.getProtocolManager();
        metricsManager = new MetricsManager(this.coreAPI);
        perkManager = new PerkManager(this);
        cloudMessageManager = new CloudMessageManager(this);
        chatTabConfig = new ChatTabConfig();
        locationManager = new LocationManager();
        playerCacheManager = new PlayerCacheManager();
        customBannerManager = new CustomBannerManager(this);
        statsManager = new StatsManager();
        npcService = new NPCService(this);


        new BukkitCloudManager(this);

        registerCommands();
        registerListeners();
        clearAllWorlds();

        System.out.println("Starting ServiceAliveTask\n");
        coreAPI.getCloudManager().sendCloudMessage("alive", "ping:initial", new JsonDocument().append("server", Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName()).append("response", "online"));
        Bukkit.getScheduler().runTaskTimer(this, new BukkitHealthTask(), 0, 20);

        group = Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName().split("-")[0];
        protocolManager.addPacketListener(new TabCompleteListener(this, PacketType.Play.Client.TAB_COMPLETE));

    }

    private void registerCommands() {
        getCommand("xyz").setExecutor(new XyzCommand(this));
        getCommand("location").setExecutor(new LocationCommand());
        getCommand("clearchat").setExecutor(new ChatclearCommand());
        getCommand("gc").setExecutor(new GcCommand());
        getCommand("tpblock").setExecutor(new TpBlockCommand());
        getCommand("sudo").setExecutor(new SudoCommand());
        getCommand("bcommand").setExecutor(new BungeeCommandCommand());
        getCommand("gamemode").setExecutor(new GamemodeCommand());
        getCommand("reportsgui").setExecutor(new ReportBukkitManager());
        getCommand("buglog").setExecutor(new BugLogCommand());
        getCommand("stopcore").setExecutor(new de.teamholy.core.bukkit.commands.StopCommand());
        getCommand("whitelist").setExecutor(new de.teamholy.core.bukkit.commands.WhitelistCommand());
    }

    private void registerListeners() {
        new PlayerNameTagListener(this);
        new PlayerJoinQuitListener(this);
        new CustomBannerManager(this);
        new UsePerkListener();
        new CloudMessageListener(this);
        new PlayerChatListener(this);
        new CommandListener(this);
        new NickListener(this);
    }

    private void clearAllWorlds() {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (World world : Bukkit.getWorlds()) {
                world.setMonsterSpawnLimit(0);
                world.setTicksPerMonsterSpawns(8888888);
                world.setTime(1000);
                world.setDifficulty(Difficulty.EASY);
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setGameRuleValue("doMobSpawning", "false");
                for (Entity ent : Bukkit.getWorld(world.getName()).getEntities()) {
                    if (ent instanceof Animals) ent.remove();
                    if (ent instanceof Monster) ent.remove();
                }
            }
        }, 200);
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

    public String getPlayerColor(UUID uuid, boolean withNick) {
        if (withNick && MarkupAPI.isNicked(Bukkit.getPlayer(uuid))) {
            return PlayerRank.PLAYER.getColorCode();
        }

        return coreAPI.getCloudManager().getColor(uuid);
    }
}
