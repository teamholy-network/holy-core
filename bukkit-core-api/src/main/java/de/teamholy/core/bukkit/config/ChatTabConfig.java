package de.teamholy.core.bukkit.config;

import de.teamholy.core.bukkit.BukkitCore;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class ChatTabConfig {

    private File file = new File("plugins/API/config.yml");
    public FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

    public ChatTabConfig(){
        configuration.options().copyDefaults(true);
        getConfiguration().options().header("Deaktivere Chat & tab prefix.");
        configuration.addDefault("ChatPrefix",true);
        configuration.addDefault("TabPrefix",true);
        BukkitCore.getInstance().setChatPrefix(configuration.getBoolean("ChatPrefix"));
        BukkitCore.getInstance().setTabPrefix(configuration.getBoolean("TabPrefix"));
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
