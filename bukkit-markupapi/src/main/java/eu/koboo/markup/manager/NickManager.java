package eu.koboo.markup.manager;

import com.comphenix.protocol.wrappers.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.events.*;
import eu.koboo.markup.util.PlayerMeta;
import eu.koboo.markup.wrapper.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NickManager implements Listener {

    /**
     * The markupAPI variable is an instance of the MarkupAPI class, used within the NickManager class.
     * It serves as a final reference for handling operations related to player nickname management,
     * including aspects of markup and metadata processing.
     */
    private final MarkupAPI markupAPI;

    /**
     * A thread-safe map that associates a unique identifier (UUID) with a player's metadata.
     * This map is used to manage metadata related to players, such as their nicknames,
     * original and modified textures, and other related information that allows for dynamic
     * alteration and management of player characteristics within the system.
     * Utilized by various processes in the NickManager class to store and retrieve player-specific data.
     */
    @Getter
    private final Map<UUID, PlayerMeta> playerMetaMap = new ConcurrentHashMap<>();

    /**
     * Constructs a new NickManager instance and registers it to listen for events.
     *
     * @param markupAPI An instance of MarkupAPI used to manage player nicknames and related actions.
     */
    public NickManager(MarkupAPI markupAPI) {
        this.markupAPI = markupAPI;
        Bukkit.getPluginManager().registerEvents(this, markupAPI);
    }

    /**
     * Determines if the specified player has a nickname assigned.
     *
     * @param player The player whose nickname status is to be checked.
     * @return true if the player has a nickname assigned; false otherwise.
     */
    public boolean hasNickName(Player player) {
        return this.playerMetaMap.containsKey(player.getUniqueId());
    }

    /**
     * Retrieves the metadata associated with a player using their unique identifier (UUID).
     *
     * @param uuid The UUID of the player whose metadata is to be retrieved.
     * @return The PlayerMeta object containing the player's metadata, or null if no metadata is found for the given UUID.
     */
    public PlayerMeta getPlayerMeta(UUID uuid) {
        return playerMetaMap.get(uuid);
    }

    /**
     * Applies a nickname and optional textures to a player, updating their metadata and firing related events.
     *
     * @param player The player to whom the nickname and textures are to be applied.
     * @param nickName The nickname to apply to the player. If longer than 16 characters, it will be truncated.
     * @param optionalUUID An optional UUID to assign to the player's nickname. If null, a new random UUID is generated.
     * @param nickTextures The textures property to apply with the nickname. If null, no texture changes are made.
     */
    public void apply(Player player, String nickName, UUID optionalUUID, Property nickTextures) {
        

        GameProfile profile = getGameProfile(player);

        PlayerMeta playerMeta = playerMetaMap.get(player.getUniqueId());
        
        PlayerPreNickEvent playerPreNickEvent = new PlayerPreNickEvent(player, playerMeta);
        Bukkit.getPluginManager().callEvent(playerPreNickEvent);
        if (playerPreNickEvent.isCancelled()) {
            return;
        }
        
        if (playerMeta == null) {
            Property realTextures = null;
            if (profile != null) {
                for (Property prop : profile.getProperties().get("textures")) {
                    if (prop.getName().equalsIgnoreCase("textures")) {
                        realTextures = prop;
                        break;
                    }
                }
            }
            UUID nickUUID = optionalUUID == null ? UUID.randomUUID() : optionalUUID;
            playerMeta = new PlayerMeta(player.getName(), realTextures, player.getUniqueId(), nickName, nickTextures, nickUUID);
            playerMetaMap.put(player.getUniqueId(), playerMeta);
        } else {
            playerMeta.updateMeta(nickName, nickTextures, UUID.randomUUID());
        }

        if (nickTextures != null) {
            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", nickTextures);
        }

        if (nickName != null) {
            nickName = nickName.length() > 16 ? nickName.substring(0, 16) : nickName;
            setGameProfileName(profile, nickName);
            setGameProfileUUID(profile, player.getUniqueId());
            player.setDisplayName(nickName);
        }

        refreshPlayer(player, playerMeta, true, false, false);

        PlayerPostNickEvent playerPostNickEvent = new PlayerPostNickEvent(player, playerMeta);
        Bukkit.getPluginManager().callEvent(playerPostNickEvent);
    }

    /**
     * Resets the player's profile to their original state by removing any nicknames or modified textures,
     * and optionally processes their departure from the game. This method also fires events before and
     * after the player's unnick process, allowing for event handling or cancellation.
     *
     * @param player The player whose profile is to be reset. This is required to identify the player
     *               and access their game profile and metadata.
     * @param leave  A boolean flag indicating if the player's departure from the game should be handled
     *               after resetting their profile. When true, additional cleanup operations related to
     *               the player's leave may be performed.
     */
    public void resetPlayer(Player player, boolean leave) {
        GameProfile profile = getGameProfile(player);
        PlayerMeta playerMeta = playerMetaMap.get(player.getUniqueId());

        if (playerMeta == null) {
            return;
        }

        PlayerPreUnnickEvent playerPreUnnickEvent = new PlayerPreUnnickEvent(player, playerMeta);
        Bukkit.getPluginManager().callEvent(playerPreUnnickEvent);
        if (playerPreUnnickEvent.isCancelled()) {
            return;
        }

        playerMetaMap.remove(player.getUniqueId());

        if (playerMeta.getRealName() != null) {
            setGameProfileName(profile, playerMeta.getRealName());
            setGameProfileUUID(profile, player.getUniqueId());
            player.setDisplayName(playerMeta.getRealName());
        }

        if (playerMeta.getRealTextures() != null) {
            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", playerMeta.getRealTextures());
        }

        refreshPlayer(player, playerMeta, false, false, leave);
        PlayerPostUnnickEvent playerPostUnnickEvent = new PlayerPostUnnickEvent(player, playerMeta);
        Bukkit.getPluginManager().callEvent(playerPostUnnickEvent);
    }

    /**
     * Refreshes the player's appearance and visibility status across the server,
     * handling changes in nickname and skin as specified.
     *
     * @param player         The player whose appearance is to be refreshed.
     * @param playerMeta     Metadata containing the player's real and nicked information.
     * @param nick           Specifies if the player's nickname should be used.
     * @param onlyChangeSkin If true, only the player's skin is changed without affecting nickname visibility.
     * @param leave          Specifies if the player is disconnecting, influencing visibility changes.
     */
    private void refreshPlayer(Player player, PlayerMeta playerMeta, boolean nick, boolean onlyChangeSkin, boolean leave) {
        WrapperPlayServerEntityDestroy destroy = createDestroy(player);
        WrapperPlayServerPlayerInfo removeInfoAll;
        WrapperPlayServerPlayerInfo addInfoAll;
        WrapperPlayServerPlayerInfo removeInfoPlayer;
        WrapperPlayServerPlayerInfo addInfoPlayer;

        if (!onlyChangeSkin) {
            if (nick) {
                removeInfoAll = createRemoveInfoReal(player, playerMeta);
                removeInfoPlayer = createRemoveInfoReal(player, playerMeta);

                addInfoAll = createAddInfoNick(false, player, playerMeta);
                addInfoPlayer = createAddInfoNick(true, player, playerMeta);
            } else {
                removeInfoAll = createRemoveInfoNick(false, player, playerMeta);
                removeInfoPlayer = createRemoveInfoNick(true, player, playerMeta);

                addInfoAll = createAddInfoReal(player, playerMeta);
                addInfoPlayer = createAddInfoReal(player, playerMeta);
            }
        } else {
            removeInfoAll = createRemoveInfoReal(player, playerMeta);
            removeInfoPlayer = createRemoveInfoReal(player, playerMeta);
            addInfoAll = createAddInfoNick(true, player, playerMeta);
            addInfoPlayer = createAddInfoNick(true, player, playerMeta);
        }

        List<Player> refreshedPlayers = new ArrayList<>();

        for (Player online : Bukkit.getOnlinePlayers()) {
            try {
                if (!online.equals(player) && online.canSee(player)) {
                    destroy.sendPacket(online);
                    removeInfoAll.sendPacket(online);
                    refreshedPlayers.add(online);
                } else if (online.equals(player)) {
                    destroy.sendPacket(online);
                    removeInfoPlayer.sendPacket(online);
                    addInfoPlayer.sendPacket(online);
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to refresh player visibility for: " + online.getName());
                e.printStackTrace();
            }
        }

        updateOwnSkin(player);

        WrapperPlayServerNamedEntitySpawn spawn = createSpawn(player, playerMeta);
        for (Player online : refreshedPlayers) {
            try {
                if (online.isOnline()) {
                    addInfoAll.sendPacket(online);
                    spawn.sendPacket(online);
                    online.showPlayer(player);
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to send spawn packet for: " + online.getName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the player's skin by refreshing their player state and reapplying conditions.
     * This involves teleporting the player to their current location, updating their
     * inventory, restoring flight status, health, food level, and active potion effects.
     * The player is briefly hidden and then shown to all online players to refresh the skin.
     *
     * @param player the player whose skin is to be updated
     */
    private void updateOwnSkin(Player player) {
        Location location = player.getLocation();
        boolean canFly = player.getAllowFlight();
        boolean isFlying = player.isFlying();
        double health = player.getHealth();
        int food = player.getFoodLevel();
        Collection<PotionEffect> activeEffects = player.getActivePotionEffects();

        try {
            WrapperPlayServerRespawn respawn = createRespawn(player);
            respawn.sendPacket(player);

            player.teleport(location);
            player.updateInventory();

            player.setAllowFlight(canFly);
            player.setFlying(isFlying);
            player.setHealth(health);
            player.setFoodLevel(food);
            player.addPotionEffects(activeEffects);

            Bukkit.getScheduler().runTaskLater(markupAPI, () -> {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.hidePlayer(player);
                    online.showPlayer(player);
                }
            }, 5L);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to update player skin: " + player.getName());
            e.printStackTrace();
        }
    }

    /**
     * Creates a new WrapperPlayServerEntityDestroy packet for the specified player,
     * setting the entity IDs to include only the player's entity ID.
     *
     * @param player the player whose entity is to be destroyed
     * @return a WrapperPlayServerEntityDestroy packet configured for the player
     */
    private WrapperPlayServerEntityDestroy createDestroy(Player player) {
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[]{player.getEntityId()});
        return destroy;
    }

    /**
     * Creates a WrapperPlayServerPlayerInfo object to remove a player's real information.
     *
     * @param player The Player instance for which the removal information is being created.
     * @param playerMeta The PlayerMeta instance containing the real player's metadata, including UUID, name, and textures.
     * @return A WrapperPlayServerPlayerInfo object configured to perform the action of removing the player's real information.
     */
    private WrapperPlayServerPlayerInfo createRemoveInfoReal(Player player, PlayerMeta playerMeta) {
        return createPlayerInfo(player, playerMeta.getRealUUID(), playerMeta.getRealName(), playerMeta.getRealTextures(), EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
    }

    /**
     * Creates a WrapperPlayServerPlayerInfo packet to add a player's nick information.
     *
     * @param self a boolean indicating whether to use the player's real UUID (true) or nick UUID (false)
     * @param player the Player object associated with the player's data
     * @param playerMeta the PlayerMeta containing the player's nick and texture information
     * @return a WrapperPlayServerPlayerInfo packet configured to add the player's nick details
     */
    private WrapperPlayServerPlayerInfo createAddInfoNick(boolean self, Player player, PlayerMeta playerMeta) {
        UUID uuid = self ? playerMeta.getRealUUID() : playerMeta.getNickUUID();
        String name = playerMeta.getNickName();
        Property textures = playerMeta.getNickTextures();
        return createPlayerInfo(player, uuid, name, textures, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
    }

    /**
     * Creates a player information packet to remove a player with their nickname details from the server's player list.
     *
     * @param self       indicates if the player is the one to be removed.
     * @param player     the player object representing the player in the server.
     * @param playerMeta contains metadata about the player's real and nicked identity, including UUID and textures.
     * @return a {@link WrapperPlayServerPlayerInfo} packet configured to remove the player with their nicked profile from the player list.
     */
    private WrapperPlayServerPlayerInfo createRemoveInfoNick(boolean self, Player player, PlayerMeta playerMeta) {
        UUID uuid = self ? playerMeta.getRealUUID() : playerMeta.getNickUUID();
        String name = playerMeta.getNickName();
        Property textures = playerMeta.getNickTextures();
        return createPlayerInfo(player, uuid, name, textures, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
    }

    /**
     * Creates a player information packet for adding a player with their real profile.
     * This method constructs a player info packet using the player's real UUID, name, and textures.
     *
     * @param player The player for whom the information is being created.
     * @param playerMeta The meta-information of the player, which includes the real UUID, name, and textures.
     * @return A WrapperPlayServerPlayerInfo packet prepared to add the player with real information.
     */
    private WrapperPlayServerPlayerInfo createAddInfoReal(Player player, PlayerMeta playerMeta) {
        return createPlayerInfo(player, playerMeta.getRealUUID(), playerMeta.getRealName(), playerMeta.getRealTextures(), EnumWrappers.PlayerInfoAction.ADD_PLAYER);
    }

    /**
     * Creates a new instance of WrapperPlayServerPlayerInfo with specified player data and action.
     * This method modifies the player's game profile by updating its texture properties and constructs
     * a PlayerInfoData object which is then used to prepare the player info packet.
     *
     * @param player The Player whose information is to be encapsulated into the PlayerInfo packet.
     * @param uuid The unique identifier (UUID) of the player.
     * @param name The display name of the player.
     * @param textures The property representing the player's textures which includes
     *                 a value and a signature.
     * @param action The PlayerInfoAction specifying the type of player info action to be applied (e.g., ADD_PLAYER, REMOVE_PLAYER).
     * @return A WrapperPlayServerPlayerInfo instance that encapsulates the constructed player info packet data.
     */
    private WrapperPlayServerPlayerInfo createPlayerInfo(Player player, UUID uuid, String name, Property textures, EnumWrappers.PlayerInfoAction action) {
        WrappedGameProfile profile = new WrappedGameProfile(uuid, name);
        if (textures != null) {
            WrappedSignedProperty signedProperty = new WrappedSignedProperty("textures", textures.getValue(), textures.getSignature());
            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", signedProperty);
        } else {
            profile.getProperties().removeAll("textures");
        }

        PlayerInfoData playerInfoData = new PlayerInfoData(profile, ((CraftPlayer) player).getHandle().playerConnection.player.ping, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromText(player.getDisplayName()));

        List<PlayerInfoData> playerInfoDataList = Collections.singletonList(playerInfoData);

        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        playerInfo.setAction(action);
        playerInfo.setData(playerInfoDataList);

        return playerInfo;
    }

    /**
     * Creates a spawn packet for a named entity using the provided player and player meta information.
     *
     * @param player the player for whom the spawn packet is being created
     * @param playerMeta the metadata associated with the player, including unique identifiers and other data
     * @return an instance of {@code WrapperPlayServerNamedEntitySpawn} representing the spawn packet for the specified player
     */
    private WrapperPlayServerNamedEntitySpawn createSpawn(Player player, PlayerMeta playerMeta) {
        WrapperPlayServerNamedEntitySpawn spawn = new WrapperPlayServerNamedEntitySpawn();
        spawn.setEntityID(player.getEntityId());
        spawn.setPlayerUUID(playerMeta.getRealUUID());
        spawn.setPosition(player.getLocation().toVector());
        spawn.setYaw(player.getLocation().getYaw());
        spawn.setPitch(player.getLocation().getPitch());
        spawn.setMetadata(WrappedDataWatcher.getEntityWatcher(player));
        return spawn;
    }

    /**
     * Creates and configures a WrapperPlayServerRespawn packet for the specified player.
     *
     * @param player The player for whom the respawn packet is to be created.
     *               The player's current world type, game mode, difficulty, and environment
     *               are used to set the corresponding fields in the respawn packet.
     * @return A configured WrapperPlayServerRespawn packet tailored to the player's current in-game conditions.
     */
    private WrapperPlayServerRespawn createRespawn(Player player) {
        WrapperPlayServerRespawn respawn = new WrapperPlayServerRespawn();
        if (player.getWorld().getWorldType() != null) {
            respawn.setLevelType(player.getWorld().getWorldType());
        }
        respawn.setGamemode(EnumWrappers.NativeGameMode.valueOf(player.getGameMode().name()));
        respawn.setDifficulty(EnumWrappers.Difficulty.valueOf(player.getWorld().getDifficulty().name()));
        respawn.setDimension(player.getWorld().getEnvironment().getId());
        return respawn;
    }

    /**
     * Handles the event when a player quits the game.
     * Performs necessary cleanup of player metadata and triggers related events.
     *
     * @param event the PlayerQuitEvent that represents the player quitting the game
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerMeta playerMeta = playerMetaMap.remove(event.getPlayer().getUniqueId());
        if (playerMeta != null) {
            resetPlayer(event.getPlayer(), true);
            Bukkit.getPluginManager().callEvent(new PlayerPostUnnickEvent(event.getPlayer(), playerMeta));
        }
    }

    /**
     * Retrieves the GameProfile of the specified player using reflection.
     * This method accesses private fields and methods, so it may break if the underlying library changes.
     *
     * @param player The player whose GameProfile is to be retrieved.
     * @return The GameProfile of the player, or null if an error occurs during retrieval.
     */
    private GameProfile getGameProfile(Player player) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);
            Method getProfile  = entityPlayer.getClass().getMethod("getProfile");
            getProfile.setAccessible(true);
            return (GameProfile) getProfile.invoke(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the name of the given GameProfile.
     *
     * @param profile the GameProfile whose name is to be set
     * @param name the new name to assign to the GameProfile
     */
    private void setGameProfileName(GameProfile profile, String name) {
        try {
            Field nameField = GameProfile.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(profile, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the UUID of the specified GameProfile instance.
     *
     * @param profile The GameProfile instance to modify.
     * @param uuid The new UUID to assign to the profile.
     */
    private void setGameProfileUUID(GameProfile profile, UUID uuid) {
        try {
            Field idField = GameProfile.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(profile, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}