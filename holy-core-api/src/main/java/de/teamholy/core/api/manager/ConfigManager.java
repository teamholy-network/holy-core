package de.teamholy.core.api.manager;

import eu.koboo.yaml.Yaml;
import eu.koboo.yaml.YamlParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.IOException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class ConfigManager {

    String username;
    String password;
    String redisPassword;
    String host;
    int port;
    String database;
    boolean useAuthSource;

    public ConfigManager() {

        try {
            // Load the config file from the same direction as the jar file

            String path = System.getProperty("user.dir") + "/mongodb.cfg";
            Yaml yaml = YamlParser.parseFilePath(path);
            System.out.println("Loaded config file, path: " + path);

            username = yaml.getString("username");
            password = yaml.getString("password");
            redisPassword = yaml.getString("redisPassword");
            host = yaml.getString("host");
            port = yaml.getInt("port");
            database = yaml.getString("database");
            useAuthSource = yaml.getBoolean("useAuthSource");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
