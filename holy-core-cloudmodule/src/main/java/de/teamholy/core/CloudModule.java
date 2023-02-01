package de.teamholy.core;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.module.NodeCloudNetModule;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.task.RankingSortTask;
import de.teamholy.core.task.StatsResetTask;
import lombok.Getter;
import org.checkerframework.checker.units.qual.C;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScoredSortedSetAsync;
import org.redisson.api.RSortedSet;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
@Getter
public class CloudModule extends NodeCloudNetModule {

    @Getter
    private static CloudModule instance;
    @Getter
    private static CoreAPI coreAPI;
    private ScheduledExecutorService service;

    @Getter
    private IPlayerManager playerManager;

    private RankingSortManager sortManager;


    public static boolean DAILY;
    public static boolean MONTHLY;

    @ModuleTask(event = ModuleLifeCycle.LOADED)
    public void init() {
        instance = this;
        coreAPI = new CoreAPI();
        playerManager = CloudNetDriver.getInstance().getServicesRegistry()
                .getFirstService(IPlayerManager.class);


        DAILY = getConfig().getBoolean("daily");
        MONTHLY = getConfig().getBoolean("monthly");
        saveConfig();
        service = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        service.scheduleAtFixedRate(new StatsResetTask(),0,1,TimeUnit.MINUTES);

        sortManager = new RankingSortManager();
    }

    @ModuleTask(event = ModuleLifeCycle.STOPPED)
    public void end() {
        getConfig().append("daily",DAILY);
        getConfig().append("monthly",MONTHLY);
        saveConfig();
    }

}
