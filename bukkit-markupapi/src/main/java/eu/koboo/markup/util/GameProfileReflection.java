package eu.koboo.markup.util;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GameProfileReflection {

    /**
     * A logger for logging messages and exceptions within the GameProfileReflection class.
     * This Logger is used to provide detailed logging information at various levels of severity.
     */
    private static final Logger LOGGER = Logger.getLogger(GameProfileReflection.class.getName());

    /**
     * A reflection-based representation of the "name" field within the GameProfile class.
     * This field is used to access and modify the "name" attribute of a GameProfile instance
     * using reflection, allowing for dynamic manipulation of private field values.
     */
    private static Field nameField;

    /**
     * A reflection-based Field object representing the "id" field of the GameProfile class.
     * This static field is initialized within a static block by accessing the declared field
     * "id" of the GameProfile class and making it accessible for reflection-based operations.
     * It is used to modify the UUID of a GameProfile instance through reflection.
     */
    private static Field idField;

    static {
        try {
            nameField = GameProfile.class.getDeclaredField("name");
            nameField.setAccessible(true);

            idField = GameProfile.class.getDeclaredField("id");
            idField.setAccessible(true);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Reflection error! Couldn't use GameProfileReflection!");
            LOGGER.log(Level.SEVERE, "Exception in static initializer block", e);
        }
    }

    /**
     * Modifies the name of a given GameProfile using reflection.
     *
     * @param profile The GameProfile whose name is to be set.
     * @param name The new name to assign to the GameProfile.
     */
    public static void setName(GameProfile profile, String name) {
        try {
            nameField.set(profile, name);
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Failed to set name in GameProfile", e);
        }
    }

    /**
     * Sets the UUID for the specified GameProfile.
     *
     * @param profile the GameProfile whose UUID is to be set
     * @param uuid the new UUID to set in the GameProfile
     */
    public static void setUUID(GameProfile profile, UUID uuid) {
        try {
            idField.set(profile, uuid);
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Failed to set UUID in GameProfile", e);
        }
    }

    /**
     * Retrieves the GameProfile associated with a given Player object. This method
     * utilizes reflection to access the private fields of the Player's underlying
     * entity to obtain the GameProfile.
     *
     * @param player The Player whose GameProfile is to be retrieved.
     * @return The GameProfile of the given Player or null if the operation fails.
     */
    public static GameProfile getGameProfile(Player player) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);
            Field profileField = entityPlayer.getClass().getDeclaredField("gameProfile");
            profileField.setAccessible(true);
            return (GameProfile) profileField.get(entityPlayer);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to get GameProfile", e);
            return null;
        }
    }
}