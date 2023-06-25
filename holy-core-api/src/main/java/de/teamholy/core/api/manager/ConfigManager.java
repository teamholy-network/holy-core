package de.teamholy.core.api.manager;

import eu.koboo.yaml.Yaml;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.IOException;

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

        try {
            Yaml yaml = Yaml.parseFilePath("mongodb.cfg");

            username = yaml.getString("username");
            password = yaml.getString("password");
            host = yaml.getString("host");
            port = yaml.getInt("port");
            database = yaml.getString("database");
            useAuthSource = yaml.getBoolean("useAuthSource");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
