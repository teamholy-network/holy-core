package de.teamholy.core.bungee;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.bungee.listener.LoginListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.plugin.Plugin;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BungeeCore extends Plugin {

    @Getter
    private static BungeeCore instance;

    @Getter
    CoreAPI coreAPI;

    public BungeeCore() {
        instance = this;
    }

    @Override
    public void onEnable() {
        coreAPI = new CoreAPI();
        coreAPI.onEnable();


        new LoginListener();
    }

    @Override
    public void onDisable() {
        coreAPI.onDisable();
    }

    public static CoreAPI getAPI() {
        return instance.getCoreAPI();
    }
}
