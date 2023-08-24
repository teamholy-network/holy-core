package eu.koboo.markup.util;

import com.mojang.authlib.properties.Property;

import java.util.UUID;

public class PlayerPreset {

    private final String name;
    private final UUID uuid;
    private final Property property;

    public PlayerPreset(String name, UUID uuid, String value, String sign) {
        this.name = name;
        this.uuid = uuid;
        this.property = new Property("textures", value, sign);
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Property getProperty() {
        return this.property;
    }
}
