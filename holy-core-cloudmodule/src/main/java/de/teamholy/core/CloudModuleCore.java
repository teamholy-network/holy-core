package de.teamholy.core;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.module.NodeCloudNetModule;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.event.CloudMessageEvent;
import de.teamholy.core.ping.HealthService;
import de.teamholy.core.task.HealthTask;
import eu.koboo.en2do.Credentials;
import lombok.Getter;

import java.util.concurrent.ExecutorService;
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
    @Getter
    private ScheduledExecutorService service;


    private RankingSortManager sortManager;


    public static boolean DAILY;
    public static boolean MONTHLY;

    private HealthService healthService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @ModuleTask(event = ModuleLifeCycle.LOADED)
    public void init() {


        instance = this;


        String mongoString = "mongodb://admin:dkdfGp3U81SEu+Zc2L@127.0.0.1:46410/?authSource=admin";
        coreAPI = new CoreAPI(Credentials.of(mongoString, "holy"));


        DAILY = getConfig().getBoolean("daily");
        MONTHLY = getConfig().getBoolean("monthly");

        saveConfig();
        service = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);


        sortManager = new RankingSortManager();


        healthService = new HealthService();

        HealthTask healthTask = new HealthTask(healthService);
        service.scheduleAtFixedRate(healthTask, 5, 1, TimeUnit.SECONDS);

        CloudNetDriver.getInstance().getEventManager()
            .registerListener(new CloudMessageEvent(healthService)); //Register a listener object on the event manager
    }

    @ModuleTask(event = ModuleLifeCycle.UNLOADED)
    public void unload() {
        getConfig().append("daily", DAILY);
        getConfig().append("monthly", MONTHLY);
        saveConfig();


        if (healthService != null)
            healthService.pingMap.clear();

        service.shutdown();
        executorService.shutdown();

    }

    @ModuleTask(event = ModuleLifeCycle.STOPPED)
    public void end() {
        getConfig().append("daily", DAILY);
        getConfig().append("monthly", MONTHLY);
        saveConfig();

        service.shutdown();
        executorService.shutdown();

        healthService.pingMap.clear();
        sortManager = null;
        healthService = null;
        coreAPI = null;
    }
}
