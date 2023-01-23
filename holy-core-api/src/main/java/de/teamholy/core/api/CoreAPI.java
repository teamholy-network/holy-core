package de.teamholy.core.api;

import eu.koboo.en2do.MongoManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CoreAPI {

    MongoManager mongoManager;

    public CoreAPI() {
        this.mongoManager = new MongoManager();
    }

    public void onEnable() {

    }

    public void onDisable() {
        mongoManager.close();
    }
}
