package de.teamholy.core.bukkit.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/* copyright by Yassino */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationManager {

    HashMap<String, Location> locations;
    File file;
    YamlConfiguration yamlConfiguration;

    public LocationManager() {
        this.locations = new HashMap<>();
        this.file = new File("plugins//API//locations.yml");
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void addLocation(String name, Location location) {
        locations.put(name, location);
        createConfigLocation(location, name);
    }

    public Location getLocation(String location) {
        if (getConfigLocation(location) != null) {
            locations.put(location, getConfigLocation(location));
            return locations.get(location);
        }
        return null;
    }


    public void createConfigLocation(Location loc, String path) {
        yamlConfiguration.set(path + ".World", loc.getWorld().getName());
        yamlConfiguration.set(path + ".X", loc.getX());
        yamlConfiguration.set(path + ".Y", loc.getY());
        yamlConfiguration.set(path + ".Z", loc.getZ());
        yamlConfiguration.set(path + ".Yaw", loc.getYaw());
        yamlConfiguration.set(path + ".Pich", loc.getPitch());
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location getConfigLocation(String path) {
        try {
            World w = Bukkit.getWorld(yamlConfiguration.getString(path + ".World"));
            double x = yamlConfiguration.getDouble(path + ".X");
            double y = yamlConfiguration.getDouble(path + ".Y");
            double z = yamlConfiguration.getDouble(path + ".Z");
            float yaw = (float) yamlConfiguration.getDouble(path + ".Yaw");
            float pitch = (float) yamlConfiguration.getDouble(path + ".Pitch");
            return new Location(w, x, y, z, yaw, pitch);
        } catch (Exception e) {
            System.out.println("§cError while loading location from config: " + path);
        }
        return null;
    }


}
