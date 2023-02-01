package de.teamholy.core.api.manager;

import eu.koboo.config.Config;
import eu.koboo.config.FileConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class ConfigManager {

    String username;
    String password;
    String host;
    int port;
    String database;
    boolean useAuthSource;

    public ConfigManager() {

        FileConfig config = Config.of("mongodb.cfg", c -> {
            c.init("username", "default");
            c.init("password", "default");
            c.init("host", "localhost");
            c.init("port", 27017);
            c.init("database", "default");
            c.init("useAuthSource", false);
        });
        username = config.getString("username");
        password = config.getString("password");
        host = config.getString("host");
        port = config.getInt("port");
        database = config.getString("database");
        useAuthSource = config.getBoolean("useAuthSource");
    }
}
