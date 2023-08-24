package eu.koboo.markup.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.util.PlayerMeta;
import eu.koboo.markup.util.PlayerPreset;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.SocketTimeoutException;
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
    private final File file;
    private final ExecutorService service;
    private final SecureRandom secureRandom = new SecureRandom();
    private final List<PlayerPreset> presetList = new ArrayList<>();

    public PresetManager(MarkupAPI markupAPI) {
        this.markupAPI = markupAPI;
        File dataFolder = new File("plugins/MarkupAPI/");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.file = new File(dataFolder, "skin_uuids.txt");
        this.service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        exportDefaults();
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

    private void exportDefaults() {
        try {
            if (!this.file.exists()) {

                Bukkit.getConsoleSender().sendMessage("Exporting default skins to '" + this.file.getAbsolutePath() + "'..");

                this.file.createNewFile();

                InputStream inputStream = PresetManager.class.getClassLoader().getResourceAsStream("uuids.txt");
                FileOutputStream outputStream = new FileOutputStream(this.file);

                if (inputStream != null) {
                    int n;
                    while ((n = inputStream.read()) != -1) {
                        outputStream.write(n);
                    }
                    inputStream.close();
                }
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadPresets() {
        try {

            int counter = 0;

            BufferedReader reader = new BufferedReader(new FileReader(this.file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("-")) {
                    counter += 1;
                    if (counter % 25 == 0) {
                        Bukkit.getConsoleSender().sendMessage("Loading " + counter + " skins..");
                    }
                    loadPreset(UUID.fromString(line), presetList::add);
                }
            }
            reader.close();
            Bukkit.getConsoleSender().sendMessage("Finished loading " + counter + " skins from '" + this.file.getAbsolutePath() + "'..!");
        } catch (Exception e) {
            if (!(e instanceof SocketTimeoutException))
                e.printStackTrace();
        }
    }

    public void loadPreset(UUID uuid, Consumer<PlayerPreset> consumer) {
        service.execute(() -> {
            PlayerPreset preset = null;
            try {
                URLConnection con = (new URL("https://api.minetools.eu/profile/" + uuid)).openConnection();
                con.setReadTimeout(3000);
                con.setDoInput(true);
                JsonElement rootElement = (new JsonParser()).parse(new BufferedReader(new InputStreamReader(con.getInputStream())));
                JsonElement jsonElement2 = rootElement.getAsJsonObject().get("raw");
                JsonElement jsonElement3 = jsonElement2.getAsJsonObject().get("properties");
                JsonElement jsonElement4 = jsonElement3.getAsJsonArray().get(0);
                String name = rootElement.getAsJsonObject().get("decoded").getAsJsonObject().get("profileName").getAsString();
                String value = jsonElement4.getAsJsonObject().get("value").toString().replace("\"", "");
                String sign = jsonElement4.getAsJsonObject().get("signature").toString().replace("\"", "");
                con.getInputStream().close();
                preset = new PlayerPreset(name, uuid, value, sign);
            } catch (IOException e) {
                if (!(e instanceof SocketTimeoutException))
                    e.printStackTrace();
            }
            consumer.accept(preset);
        });
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
                preset = new PlayerPreset(name, uuid, value, sign);
            } catch (IOException e) {
                e.printStackTrace();
            }
            consumer.accept(preset);
        });
    }

}
