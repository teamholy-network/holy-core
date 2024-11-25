package de.teamholy.core.api.manager;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class ConfigManager {

    String username;
    String password;
    String redisPassword;
    String host;
    String rabbitConnection;
    String port;
    String database;
    String useAuthSource;

    public ConfigManager() {

        try {
            String path = "/home/Cloud/mongodb.cfg";
            YamlReader reader = new YamlReader(new FileReader(path));
            Map<String, Object> yamlMap = (Map<String, Object>) reader.read();
            System.out.println("Loaded config file, path: " + path);

            username = (String) yamlMap.get("username");
            password = (String) yamlMap.get("password");
            redisPassword = (String) yamlMap.get("redisPassword");
            host = (String) yamlMap.get("host");
            port = (String) yamlMap.get("port");
            database = (String) yamlMap.get("database");
            useAuthSource = (String) yamlMap.get("useAuthSource");
            rabbitConnection = (String) yamlMap.get("rabbitConnection");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}