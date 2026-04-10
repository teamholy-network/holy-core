package de.teamholy.core.translation;

import de.skydb.translationapi.api.TranslationAPI;
import de.skydb.translationapi.api.TranslationBackend;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class BukkitTranslateAPI {

    private static TranslationAPI instance;

    static {
        TranslationBackend backend = new TranslationBackend();
        instance = new TranslationAPI(TranslationAPI.Platform.BUKKIT, backend);
    }

    public static String translate(Player player, String key) {
        return instance.translateMessage(player, key);
    }

    public static String translatePlaceholder(Player player, String key, Object... placeholders) {
        String[] stringPlaceholders = Arrays.stream(placeholders)
                .map(Object::toString)
                .toArray(String[]::new);
        return instance.translateMessage(player, key, stringPlaceholders);
    }

    public static String translate(UUID uuid, String key) {
        return instance.translateMessage(uuid, key);
    }

    public static String translatePlaceholder(UUID uuid, String key, Object... placeholders) {
        String[] stringPlaceholders = Arrays.stream(placeholders)
                .map(Object::toString)
                .toArray(String[]::new);
        return instance.translateMessage(uuid, key, stringPlaceholders);
    }

    public static String translate(String playerName, String key) {
        return instance.translateMessage(playerName, key);
    }

    public static String translatePlaceholder(String playerName, String key, Object... placeholders) {
        String[] stringPlaceholders = Arrays.stream(placeholders)
                .map(Object::toString)
                .toArray(String[]::new);
        return instance.translateMessage(playerName, key, stringPlaceholders);
    }

    public static boolean isEnabled() {
        return true;
    }
}
