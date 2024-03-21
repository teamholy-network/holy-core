package eu.koboo.markup.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.util.PlayerMeta;
import eu.koboo.markup.util.PlayerPreset;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PresetManager {

    private final MarkupAPI markupAPI;
    private final ExecutorService service;
    private final SecureRandom secureRandom = new SecureRandom();
    private final List<PlayerPreset> presetList = new ArrayList<>();

    @Getter
    private boolean load = true;

    public PresetManager(MarkupAPI markupAPI) {
        this.markupAPI = markupAPI;
        File dataFolder = new File("plugins/MarkupAPI/");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        reloadPresets();
    }

    public PlayerPreset getPlayerPreset() {
        PlayerPreset preset = presetList.get(secureRandom.nextInt(presetList.size()));

        Player target = Bukkit.getPlayer(preset.getName());
        if (target != null) {
            return getPlayerPreset();
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerMeta playerMeta = markupAPI.getNickManager().getPlayerMeta(online.getUniqueId());
            if (playerMeta == null) {
                continue;
            }
            if (playerMeta.getRealName().equalsIgnoreCase(preset.getName())) {
                return getPlayerPreset();
            }
        }
        return preset;
    }


    public void loadPreset(String playerName, Consumer<PlayerPreset> consumer) {
        service.execute(() -> {
            PlayerPreset preset = null;
            try {
                URLConnection con = (new URL("https://api.ashcon.app/mojang/v2/user/" + playerName)).openConnection();
                con.setReadTimeout(3000);
                con.setDoInput(true);
                JsonElement rootElement = (new JsonParser()).parse(new BufferedReader(new InputStreamReader(con.getInputStream())));
                JsonElement texturesElement = rootElement.getAsJsonObject().get("textures");
                JsonElement rawElement = texturesElement.getAsJsonObject().get("raw");
                UUID uuid = UUID.fromString(rootElement.getAsJsonObject().get("uuid").getAsString());
                String name = rootElement.getAsJsonObject().get("username").getAsString();
                String value = rawElement.getAsJsonObject().get("value").toString().replace("\"", "");
                String sign = rawElement.getAsJsonObject().get("signature").toString().replace("\"", "");
                con.getInputStream().close();
                preset = new PlayerPreset();
                preset.setUuid(uuid);
                preset.setName(name);
                preset.setValue(value);
                preset.setSignature(sign);
            } catch (IOException e) {
                e.printStackTrace();
            }
            consumer.accept(preset);
        });
    }

    public void reloadPresets() {
        service.execute(() -> {
            System.out.println("Loading presets...");
            presetList.addAll(markupAPI.getNickProfilesRepository().findAll());
            System.out.println("Loaded " + presetList.size() + " presets.");
            if (presetList.isEmpty()) {
                load = false;
            }
        });
    }


}
