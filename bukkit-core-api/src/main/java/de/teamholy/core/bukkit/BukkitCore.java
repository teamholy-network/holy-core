package de.teamholy.core.bukkit;

import de.teamholy.core.api.CoreAPI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.java.JavaPlugin;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BukkitCore extends JavaPlugin {

    @Getter
    private static BukkitCore instance;

    @Getter
    CoreAPI coreAPI;

    public BukkitCore() {
        instance = this;
    }

    @Override
    public void onEnable() {
        coreAPI = new CoreAPI();
        coreAPI.onEnable();


    }

    @Override
    public void onDisable() {
        coreAPI.onDisable();
    }

    public static CoreAPI getAPI() {
        return instance.getCoreAPI();
    }
}
