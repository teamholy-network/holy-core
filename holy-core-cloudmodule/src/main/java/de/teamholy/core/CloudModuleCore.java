package de.teamholy.core;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.module.NodeCloudNetModule;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.task.StatsResetTask;
import eu.koboo.en2do.Credentials;
import lombok.Getter;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
@Getter
public class CloudModuleCore extends NodeCloudNetModule {

    @Getter
    private static CloudModuleCore instance;
    @Getter
    private static CoreAPI coreAPI;
    private ScheduledExecutorService service;


    private RankingSortManager sortManager;
    private MongoManager mongoManager;


    public static boolean DAILY;
    public static boolean MONTHLY;

    @ModuleTask(event = ModuleLifeCycle.LOADED)
    public void init() {

        String connectionString = "mongodb://root:leXtRoNdiAlBineLEINEidECtORmlACORceIGeNhIGhteroNfo@89.163.251.177:36410/?authSource=admin";

        instance = this;
        coreAPI = new CoreAPI(new Credentials(connectionString,"holy"));
        mongoManager = new MongoManager(connectionString,"holy");


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
