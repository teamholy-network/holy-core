package eu.koboo.markup.util;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.mojang.authlib.properties.Property;

import java.util.UUID;

public class PlayerMeta {

    private final String realName;
    private final Property realTextures;
    private final UUID realUUID;
    private String nickName;
    private Property nickTextures;
    private UUID nickUUID;

    public PlayerMeta(String realName, Property realTextures, UUID realUUID, String nickName, Property nickTextures, UUID nickUUID) {
        this.realName = realName;
        this.realTextures = realTextures;
        this.realUUID = realUUID;
        updateMeta(nickName, nickTextures, nickUUID);
    }


    public String getRealName() {
        return realName;
    }

    public Property getRealTextures() {
        return realTextures;
    }

    public UUID getRealUUID() {
        return realUUID;
    }

    public String getNickName() {
        return nickName;
    }

    public Property getNickTextures() {
        return nickTextures;
    }

    public UUID getNickUUID() {
        return nickUUID;
    }

    public void updateMeta(String nickName, Property nickTextures, UUID nickUUID) {
        this.nickName = nickName;
        this.nickTextures = nickTextures;
        this.nickUUID = nickUUID;
    }

    public WrappedGameProfile getNickedProfile() {
        WrappedGameProfile profile = new WrappedGameProfile(realUUID, nickName);

        Property property = getNickTextures();
        if (property == null)
            property = getRealTextures();

        WrappedSignedProperty signedProperty = new WrappedSignedProperty("textures", property.getValue(), property.getSignature());
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", signedProperty);
        return profile;
    }
}
