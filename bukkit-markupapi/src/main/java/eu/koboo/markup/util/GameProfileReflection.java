package eu.koboo.markup.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class GameProfileReflection {

    private static Field NAME_FIELD;
    private static Field UUID_FIELD;

    private static Field PLAY_OUT_INFO;

    static {
        try {
            NAME_FIELD = GameProfile.class.getDeclaredField("name");
            NAME_FIELD.setAccessible(true);

            UUID_FIELD = GameProfile.class.getDeclaredField("id");
            UUID_FIELD.setAccessible(true);

            PLAY_OUT_INFO = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
            PLAY_OUT_INFO.setAccessible(true);
        } catch (Exception e) {
            Bukkit.broadcastMessage("Reflection error! Couldn't use NickAPI!");
        }
    }

    public static void setName(GameProfile profile, String name) {
        try {
            NAME_FIELD.set(profile, name);
        } catch (IllegalAccessException ignored) {

        }
    }

    public static void setUUID(GameProfile profile, UUID uuid) {
        try {
            UUID_FIELD.set(profile, uuid);
        } catch (IllegalAccessException e) {
        }
    }

    public static void setPlayerInfo(PacketPlayOutPlayerInfo packet, List<PacketPlayOutPlayerInfo.PlayerInfoData> list) {
        try {
            PLAY_OUT_INFO.set(packet, list);
        } catch (IllegalAccessException e) {
        }
    }
}
