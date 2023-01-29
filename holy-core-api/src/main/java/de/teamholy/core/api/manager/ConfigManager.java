package de.teamholy.core.api.manager;

import eu.koboo.config.Config;
import eu.koboo.config.FileConfig;

/* copyright by Yassino */
public class ConfigManager {




    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private final String database;
    private final boolean useAuthSource;

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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public boolean isUseAuthSource() {
        return useAuthSource;
    }



}
