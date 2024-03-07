package eu.koboo.markup;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mojang.authlib.properties.Property;
import de.teamholy.core.api.CoreAPI;
import eu.koboo.markup.adapter.PacketPlayServerNamedEntitySpawnAdapter;
import eu.koboo.markup.adapter.PacketPlayServerPlayerInfoAdapter;
import eu.koboo.markup.adapter.PacketPlayServerScoreboardTeamAdapter;
import eu.koboo.markup.commands.*;
import eu.koboo.markup.events.PlayerPostUnnickEvent;
import eu.koboo.markup.manager.NickManager;
import eu.koboo.markup.manager.PresetManager;
import eu.koboo.markup.manager.TeamManager;
import eu.koboo.markup.repository.NickProfilesRepository;
import eu.koboo.markup.util.PlayerMeta;
import eu.koboo.markup.util.PlayerPreset;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.function.Consumer;

public class MarkupAPI extends JavaPlugin {

    public static final String NICK_PREFIX = "§5Nick §8× §7";

    private static MarkupAPI api;

    private NickManager nickManager;
    private PresetManager presetManager;
    private TeamManager teamManager;
    private NickProfilesRepository nickProfilesRepository;

    public static void updateNameTag(Player player) {
        api.getTeamManager().announceUpdate(player);
    }


    public static void updateNameTags() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            updateNameTag(online);
        }
    }

    public static void nick(Player player, String nickName, UUID optionalUUID, Property skinProperty) {
        api.getNickManager().apply(player, nickName, optionalUUID, skinProperty);
    }

    public static void changeSkin(Player player, String value, String signature) {
        api.getNickManager().apply(player, player.getName(), player.getUniqueId(), new Property("textures",value,signature));
    }

    public static void unnick(Player player) {
        api.getNickManager().resetPlayer(player);
    }

    public static boolean isNicked(Player player) {
        return api.getNickManager().hasNickName(player);
    }

    public static String getRealname(Player player) {
        PlayerMeta playerMeta = api.getNickManager().getPlayerMeta(player.getUniqueId());
        if (playerMeta != null)
            return playerMeta.getRealName();
        return player.getName();
    }

    public static String getNickname(Player player) {
        PlayerMeta playerMeta = api.getNickManager().getPlayerMeta(player.getUniqueId());
        if (playerMeta != null)
            return playerMeta.getNickName();
        return player.getName();
    }

    public static void reloadPlayerPresets() {
        api.getPresetManager().reloadPresets();
    }


    public static PlayerPreset getRandomPlayerPreset() {
        return api.getPresetManager().getPlayerPreset();
    }

    @Override
    public void onEnable() {
        api = this;

        CoreAPI coreAPI = new CoreAPI();
        nickProfilesRepository = coreAPI.getMongoManager().create(NickProfilesRepository.class);

        saveDefaultConfig();

        nickManager = new NickManager(this);
        presetManager = new PresetManager(this);
        teamManager = new TeamManager(this);


        handleCommandRegistration("hardnick", new CommandHardNick(this));
        handleCommandRegistration("nick", new CommandNick(this));
        handleCommandRegistration("nicklist", new CommandNickList(this));
        handleCommandRegistration("reloadpresets", new CommandReloadPresets(this));

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketPlayServerNamedEntitySpawnAdapter(this));
        protocolManager.addPacketListener(new PacketPlayServerPlayerInfoAdapter(this));
        protocolManager.addPacketListener(new PacketPlayServerScoreboardTeamAdapter(this));
        System.out.println("Started MarkupAPI");
    }


    public static Property getProperty(String value, String signature) {
        return new Property("textures", value, signature);
    }

    @Override
    public void onDisable() {
        nickManager.getPlayerMetaMap().forEach((uuid, playerMeta) -> Bukkit.getPluginManager().callEvent(new PlayerPostUnnickEvent(Bukkit.getPlayer(uuid),playerMeta)));
    }

    public NickManager getNickManager() {
        return nickManager;
    }

    public PresetManager getPresetManager() {
        return presetManager;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public NickProfilesRepository getNickProfilesRepository() {
        return nickProfilesRepository;
    }

    private void handleCommandRegistration(String command, CommandExecutor executor) {
        String key = "register-cmd-" + command;
        if(getConfig().contains(key) && getConfig().getBoolean(key)) {
            getCommand(command).setExecutor(executor);
        }
    }
}
