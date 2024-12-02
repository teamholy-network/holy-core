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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Optional;
import java.util.UUID;

public class MarkupAPI extends JavaPlugin {

    /**
     * A constant string used as a prefix for player nicknames within the application.
     * This prefix uses specific color codes, which are interpreted in supported environments
     * to display a visually distinct identifier next to nicknames.
     */
    public static final String NICK_PREFIX = "§dN§5ick §8× §7";

    /**
     * A logger used for logging messages and debugging information within the MarkupAPI class.
     * It uses LoggerFactory to obtain a logger specific to the MarkupAPI class, which allows
     * for controlled logging following the conventions and levels available in the logging framework.
     */
    private static final Logger logger = LoggerFactory.getLogger(MarkupAPI.class);

    /**
     * A static instance of the MarkupAPI class used to access various methods related to
     * managing player nicknames, skins, and tag updates within the application. This includes
     * methods for nicking players, changing skins, handling name tag updates, and accessing
     * various managers and repositories associated with player identity management.
     */
    private static MarkupAPI api;

    /**
     * Manages the nicknames assigned to players within the system.
     * Responsible for nick-related operations, such as setting, retrieving,
     * and handling the lifecycle of player nicknames.
     */
    private NickManager nickManager;

    /**
     * A manager responsible for handling player presets within the MarkupAPI system.
     * It provides functionalities to load, manage, and retrieve player presets.
     * The PresetManager ensures that player presets are loaded from the repository
     * and can be accessed or manipulated as needed by other components of the MarkupAPI.
     */
    private PresetManager presetManager;

    /**
     * Manages team-related functionalities within the MarkupAPI.
     * Responsible for handling player name tag updates and related events.
     * Utilizes a thread pool executor to optimize task handling.
     * Registers event listeners upon initialization.
     */
    private TeamManager teamManager;

    /**
     * An instance of {@code NickProfilesRepository} used to manage the persistence
     * and retrieval of nick profile data associated with players. It serves as a
     * bridge between the application's business logic and the data storage layer
     * that specifically handles player nicknames and their associated presentation
     * presets.
     */
    private NickProfilesRepository nickProfilesRepository;

    /**
     * Updates the name tag of a specified player.
     * This involves announcing an update through the team manager,
     * which effectively triggers a re-evaluation of the player's name tag
     * for all currently online players.
     *
     * @param player the player whose name tag is to be updated
     */
    public static void updateNameTag(Player player) {
        api.getTeamManager().announceUpdate(player);
    }

    /**
     * Updates the name tags of all currently online players.
     *
     * This method iterates over the list of all players currently online on the server,
     * calling the {@link MarkupAPI#updateNameTag(Player)} method for each player to
     * refresh their name tags. It is typically used to apply changes to player names
     * displayed in the game.
     */
    public static void updateNameTags() {
        Bukkit.getOnlinePlayers().forEach(MarkupAPI::updateNameTag);
    }

    /**
     * Applies a nickname, optional UUID, and skin properties to a player using the NickManager.
     *
     * @param player The player who will receive the nickname and optional textures.
     * @param nickName The nickname to be applied to the player. If longer than 16 characters, it will be truncated.
     * @param optionalUUID An optional UUID for the player's nickname. If null, a new UUID will be generated.
     * @param skinProperty The skin property to be applied to the player. If null, no texture changes will be made.
     */
    public static void nick(Player player, String nickName, UUID optionalUUID, Property skinProperty) {
        api.getNickManager().apply(player, nickName, optionalUUID, skinProperty);
    }

    /**
     * Changes the skin of a player by applying new texture properties.
     *
     * @param player The player whose skin is to be changed.
     * @param value The base64-encoded value of the new skin texture to be applied.
     * @param signature The signature associated with the skin texture, used for verification.
     */
    public static void changeSkin(Player player, String value, String signature) {
        api.getNickManager().apply(player, player.getName(), player.getUniqueId(), new Property("textures", value, signature));
    }

    /**
     * Resets the given player's profile by removing any applied nicknames.
     *
     * @param player The player whose nickname should be removed.
     */
    public static void unnick(Player player) {
        api.getNickManager().resetPlayer(player, false);
    }

    /**
     * Checks if the given player currently has a nickname.
     *
     * @param player the player to check for a nickname
     * @return true if the player is nicked, false otherwise
     */
    public static boolean isNicked(Player player) {
        return api.getNickManager().hasNickName(player);
    }

    /**
     * Retrieves the real name of the specified player.
     * If the player has a meta record, the real name from the meta is returned;
     * otherwise, the player's current name is used.
     *
     * @param player the player whose real name is to be retrieved, cannot be null
     * @return an Optional containing the real name of the player, or the player's current name if no real name is recorded
     */
    public static Optional<String> getRealname(Player player) {
        return Optional.ofNullable(api.getNickManager().getPlayerMeta(player.getUniqueId()))
            .map(PlayerMeta::getRealName)
            .or(() -> Optional.of(player.getName()));
    }

    /**
     * Retrieves the nickname of the specified player. If the player has a nickname set,
     * it returns the nickname; otherwise, it returns the player's real name.
     *
     * @param player the player whose nickname is to be retrieved
     * @return an Optional containing the player's nickname if set, or the real name if not
     */
    public static Optional<String> getNickname(Player player) {
        return Optional.ofNullable(api.getNickManager().getPlayerMeta(player.getUniqueId()))
            .map(PlayerMeta::getNickName)
            .or(() -> Optional.of(player.getName()));
    }

    /**
     * Reloads the player presets by invoking the preset manager's reload functionality.
     * This method refreshes the list of player presets, ensuring that the latest
     * configurations from the repository are loaded into the system.
     * It is particularly useful when there have been changes to the presets
     * that need to be reflected immediately without restarting the application.
     */
    public static void reloadPlayerPresets() {
        api.getPresetManager().reloadPresets();
    }

    /**
     * Retrieves a random player preset from the preset manager, ensuring that
     * the selected preset is not currently being used by an online player.
     *
     * @return an Optional containing a PlayerPreset object if available, or an empty Optional if no valid presets are found.
     */
    public static Optional<PlayerPreset> getRandomPlayerPreset() {
        return Optional.ofNullable(api.getPresetManager().getPlayerPreset());
    }

    /**
     * Initializes the MarkupAPI plugin when it is enabled. This method sets up repository access,
     * loads configurations, and prepares various manager instances required by the plugin.
     *
     * The steps executed in this method are:
     * - Assigns the current instance of the API.
     * - Initializes the MongoDB repository for handling nick profile data.
     * - Loads the default configuration file.
     * - Creates instances of NickManager, PresetManager, and TeamManager.
     * - Registers plugin commands and sets up packet listeners.
     * - Logs a successful start message indicating that the plugin has started successfully.
     *
     * If there is an error during the initialization of the NickProfilesRepository, the plugin
     * will log the error and proceed to disable itself to prevent further issues.
     */
    @Override
    public void onEnable() {
        api = this;

        CoreAPI coreAPI = new CoreAPI();
        try {
            nickProfilesRepository = coreAPI.getMongoManager().create(NickProfilesRepository.class);
        } catch (Exception e) {
            logger.error("Failed to initialize NickProfilesRepository", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        nickManager = new NickManager(this);
        presetManager = new PresetManager(this);
        teamManager = new TeamManager(this);

        registerCommands();
        initializePacketListeners();

        logger.info("MarkupAPI started successfully.");
    }

    /**
     * Creates and returns a new Property with the specified value and signature
     * for the attribute "textures".
     *
     * @param value the texture value for the property
     * @param signature the signature associated with the texture value
     * @return a new Property object containing the specified value and signature
     */
    public static Property getProperty(String value, String signature) {
        return new Property("textures", value, signature);
    }

    /**
     * This method is called when the MarkupAPI plugin is disabled.
     * It iterates over all player metadata stored in the NickManager,
     * firing a PlayerPostUnnickEvent for each player who is currently online.
     * Additionally, it logs an informational message indicating the plugin has been disabled.
     */
    @Override
    public void onDisable() {
        nickManager.getPlayerMetaMap().forEach((uuid, playerMeta) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                Bukkit.getPluginManager().callEvent(new PlayerPostUnnickEvent(player, playerMeta));
            }
        });

        logger.info("MarkupAPI has been disabled.");
    }

    /**
     * Retrieves the NickManager instance associated with this MarkupAPI.
     *
     * @return the NickManager instance used for managing nicknames.
     */
    public NickManager getNickManager() {
        return nickManager;
    }

    /**
     * Retrieves the PresetManager instance associated with this API.
     *
     * @return the PresetManager responsible for handling player presets and related operations.
     */
    public PresetManager getPresetManager() {
        return presetManager;
    }

    /**
     * Retrieves the TeamManager instance.
     *
     * @return the current instance of TeamManager associated with this class.
     */
    public TeamManager getTeamManager() {
        return teamManager;
    }

    /**
     * Retrieves the repository responsible for managing nick profile data.
     *
     * @return the NickProfilesRepository instance used to access and manage player nick profiles.
     */
    public NickProfilesRepository getNickProfilesRepository() {
        return nickProfilesRepository;
    }

    /**
     * Registers a set of commands with their corresponding command executors.
     * These commands provide functionality related to nicknames within the plugin.
     * The commands being registered include "hardnick", "nick", "nicklist", and "reloadpresets".
     * This method leverages the handleCommandRegistration method to link each command
     * with its respective executor class, ensuring they are available for use
     * if configured appropriately.
     */
    private void registerCommands() {
        handleCommandRegistration("hardnick", new CommandHardNick(this));
        handleCommandRegistration("nick", new CommandNick(this));
        handleCommandRegistration("nicklist", new CommandNickList(this));
        handleCommandRegistration("reloadpresets", new CommandReloadPresets(this));
    }

    /**
     * Initializes the packet listeners for handling server packets related to named entity spawning,
     * player information, and scoreboard teams. Utilizes the ProtocolManager from ProtocolLibrary
     * to add specific packet listeners, which include PacketPlayServerNamedEntitySpawnAdapter,
     * PacketPlayServerPlayerInfoAdapter, and PacketPlayServerScoreboardTeamAdapter.
     * These listeners are responsible for intercepting and modifying packets sent to players,
     * often for features such as nickname handling or team management.
     */
    private void initializePacketListeners() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketPlayServerNamedEntitySpawnAdapter(this));
        protocolManager.addPacketListener(new PacketPlayServerPlayerInfoAdapter(this));
        protocolManager.addPacketListener(new PacketPlayServerScoreboardTeamAdapter(this));
    }

    /**
     * Registers a command with a specific executor if the configuration allows it.
     *
     * @param command the name of the command to register
     * @param executor the executor to handle the command logic
     */
    private void handleCommandRegistration(String command, CommandExecutor executor) {
        String key = "register-cmd-" + command;
        if (getConfig().getBoolean(key, false)) {
            Optional.ofNullable(getCommand(command))
                .ifPresent(cmd -> cmd.setExecutor(executor));
        }
    }
}