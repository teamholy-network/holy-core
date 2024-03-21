package de.teamholy.core.api.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/* copyright by Yassino */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AbstractConfiguration {

    File path;
    File configFile;
    Gson gson;
    @NonFinal
    JsonObject baseObject = new JsonObject();

    public AbstractConfiguration(File path, String fileName) {
        this.path = path;
        this.path.mkdirs();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.configFile = new File(path, fileName + ".json");
        if (!configFile.exists()) {
            save();
        }
        load();
    }

    public <T> AbstractConfiguration append(String key, T value, boolean fullUpdate) {

        if (fullUpdate) {
            baseObject.add(key, gson.toJsonTree(value));
        } else {
            if (!baseObject.has(key)) {
                baseObject.add(key, gson.toJsonTree(value));
            }
        }


        return this;
    }

    public <T> T get(String key, Class<?> clazz) {
        return gson.fromJson(baseObject.get(key), (Type) clazz);
    }


    public <T> List<T> getList(String key, Class<?> clazz) {
        List<T> list = new ArrayList<>();
        for (Object o : gson.fromJson(baseObject.get(key), List.class)) {
            list.add(gson.fromJson(gson.toJson(o), (Type) clazz));
        }
        return list;
    }

    public void save() {
        try {
            FileWriter fileWriter = new FileWriter(configFile);
            fileWriter.write(gson.toJson(baseObject));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ignored) {
        }
    }

    public void load() {
        try {
            baseObject = gson.fromJson(new FileReader(configFile), JsonObject.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
