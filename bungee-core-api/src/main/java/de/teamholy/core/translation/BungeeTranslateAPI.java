package de.teamholy.core.translation;

import de.skydb.translationapi.api.TranslationAPI;
import de.skydb.translationapi.api.TranslationBackend;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.UUID;

public class BungeeTranslateAPI {

    private static TranslationAPI instance;

    static {
        TranslationBackend backend = new TranslationBackend();
        instance = new TranslationAPI(TranslationAPI.Platform.BUNGEECORD, backend);
    }

    public static String translate(ProxiedPlayer player, String key) {
        return instance.translateMessage(player.getUniqueId(), key);
    }

    public static String translatePlaceholder(ProxiedPlayer player, String key, Object... placeholders) {
        String[] stringPlaceholders = Arrays.stream(placeholders)
                .map(Object::toString)
                .toArray(String[]::new);
        return instance.translateMessage(player.getUniqueId(), key, stringPlaceholders);
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
}