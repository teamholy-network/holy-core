package eu.koboo.markup.manager;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.events.PlayerPostNickEvent;
import eu.koboo.markup.events.PlayerPostUnnickEvent;
import eu.koboo.markup.events.PlayerPreNickEvent;
import eu.koboo.markup.events.PlayerPreUnnickEvent;
import eu.koboo.markup.util.GameProfileReflection;
import eu.koboo.markup.util.PlayerMeta;
import eu.koboo.markup.wrapper.WrapperPlayServerEntityDestroy;
import eu.koboo.markup.wrapper.WrapperPlayServerNamedEntitySpawn;
import eu.koboo.markup.wrapper.WrapperPlayServerPlayerInfo;
import eu.koboo.markup.wrapper.WrapperPlayServerRespawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NickManager implements Listener {

    private final MarkupAPI markupAPI;
    private final Map<UUID, PlayerMeta> playerMetaMap = new ConcurrentHashMap<>();

    public NickManager(MarkupAPI markupAPI) {
        this.markupAPI = markupAPI;
        Bukkit.getPluginManager().registerEvents(this, markupAPI);
    }

    public boolean hasNickName(Player player) {
        return this.playerMetaMap.containsKey(player.getUniqueId());
    }

    public PlayerMeta getPlayerMeta(UUID uuid) {
        return playerMetaMap.get(uuid);
    }

    public Map<UUID, PlayerMeta> getPlayerMetaMap() {
        return playerMetaMap;
    }

    public void apply(Player player, String nickName, UUID optionalUUID, Property nickTextures) {

        CraftPlayer craftPlayer = (CraftPlayer) player;
        GameProfile profile = craftPlayer.getProfile();

        PlayerMeta playerMeta = playerMetaMap.get(player.getUniqueId());
        if (playerMeta == null) {
            Property realTextures = null;
            for (Property prop : profile.getProperties().get("textures")) {
                if (prop.getName().equalsIgnoreCase("textures")) {
                    realTextures = prop;
                    break;
                }
            }
            UUID nickUUID = optionalUUID == null ? UUID.randomUUID() : optionalUUID;
            playerMeta = new PlayerMeta(player.getName(), realTextures, player.getUniqueId(), nickName, nickTextures, nickUUID);
            playerMetaMap.put(player.getUniqueId(), playerMeta);
        } else {
            playerMeta.updateMeta(nickName, nickTextures, UUID.randomUUID());
        }

        PlayerPreNickEvent playerPreNickEvent = new PlayerPreNickEvent(player, playerMeta);
        Bukkit.getPluginManager().callEvent(playerPreNickEvent);
        if (playerPreNickEvent.isCancelled()) {
            return;
        }

        // Should we change skin data?
        if (nickTextures != null) {
            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", nickTextures);
        }

        // Should we change name data?
        if (nickName != null) {
            nickName = nickName.length() > 16 ? nickName.substring(0, 16) : nickName;
            GameProfileReflection.setName(profile, nickName);
            GameProfileReflection.setUUID(profile, player.getUniqueId());
            player.setDisplayName(nickName);
        }

        refreshPlayer(player, playerMeta, true, false);

        PlayerPostNickEvent playerPostNickEvent = new PlayerPostNickEvent(player, playerMeta);
        Bukkit.getPluginManager().callEvent(playerPostNickEvent);
    }

    public void resetPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        GameProfile profile = craftPlayer.getProfile();
        PlayerMeta playerMeta = playerMetaMap.get(player.getUniqueId());

        //ArrayList<Player> cantSee = Bukkit.getOnlinePlayers().stream().filter(all -> !all.canSee(player)).collect(Collectors.toCollection(ArrayList::new));


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
            GameProfileReflection.setName(profile, playerMeta.getRealName());
            GameProfileReflection.setUUID(profile, player.getUniqueId());
            player.setDisplayName(playerMeta.getRealName());
        }

        if (playerMeta.getRealTextures() != null) {
            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", playerMeta.getRealTextures());
        }

        refreshPlayer(player, playerMeta, false, false);
        PlayerPostUnnickEvent playerPostUnnickEvent = new PlayerPostUnnickEvent(player, playerMeta);
        Bukkit.getPluginManager().callEvent(playerPostUnnickEvent);
        //cantSee.forEach(cantSeePlayer -> cantSeePlayer.hidePlayer(craftPlayer));
    }

    @SuppressWarnings("all")
    private void refreshPlayer(Player player, PlayerMeta playerMeta, boolean nick, boolean onlyChangeSkin) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        WrapperPlayServerEntityDestroy destroy = createDestroy(player);
        WrapperPlayServerPlayerInfo removeInfoAll;
        WrapperPlayServerPlayerInfo addInfoAll;
        WrapperPlayServerPlayerInfo removeInfoPlayer;
        WrapperPlayServerPlayerInfo addInfoPlayer;


        if (!onlyChangeSkin) {
            if (nick) {
                // If we are going to nick, remove the real player
                removeInfoAll = createRemoveInfoReal(player, playerMeta);
                removeInfoPlayer = createRemoveInfoReal(player, playerMeta);
                // and add the nicked player
                addInfoAll = createAddInfoNick(false, player, playerMeta);
                addInfoPlayer = createAddInfoNick(true, player, playerMeta);
            } else {
                // If we are NOT going to nick, remove the previous nick player
                removeInfoAll = createRemoveInfoNick(false, player, playerMeta);
                removeInfoPlayer = createRemoveInfoNick(true, player, playerMeta);
                // and add the real player
                addInfoAll = createAddInfoReal(player, playerMeta);
                addInfoPlayer = createAddInfoReal(player, playerMeta);
            }
        } else {
            // If we are going to nick, remove the real player
            removeInfoAll = createRemoveInfoReal(player, playerMeta);
            removeInfoPlayer = createRemoveInfoReal(player, playerMeta);
            // and add the nicked player
            addInfoAll = createAddInfoNick(true, player, playerMeta);
            addInfoPlayer = createAddInfoNick(true, player, playerMeta);
        }

        List<Player> refreshedPlayers = new ArrayList<>();

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getEntityId() != player.getEntityId()) {
                if (online.canSee(player)) {
                    destroy.sendPacket(online);
                    removeInfoAll.sendPacket(online);
                    refreshedPlayers.add(online);
                }
            } else {
                destroy.sendPacket(online);
                removeInfoPlayer.sendPacket(online);
                addInfoPlayer.sendPacket(online);
            }
        }

        updateOwnSkin(player);

        WrapperPlayServerNamedEntitySpawn spawn = createSpawn(player, playerMeta);
        for (Player online : refreshedPlayers) {
            if (online != null && online.isOnline()) {
                addInfoAll.sendPacket(online);
                spawn.sendPacket(online);
                online.showPlayer(player);
            }
        }


    }

    private void updateOwnSkin(Player player) {
        Location location = player.getLocation();
        boolean canFly = player.getAllowFlight();
        boolean isFlying = player.isFlying();
        double health = player.getHealth();
        int food = player.getFoodLevel();
        Collection<PotionEffect> activeEffects = player.getActivePotionEffects();
        WrapperPlayServerRespawn respawn = createRespawn(player);
        respawn.sendPacket(player);
        player.teleport(location);
        player.updateInventory();
        player.setAllowFlight(canFly);
        player.setFlying(isFlying);
        player.setHealth(health);
        player.setFoodLevel(food);
        player.addPotionEffects(activeEffects);
    }

    private WrapperPlayServerEntityDestroy createDestroy(Player player) {
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[]{player.getEntityId()});
        return destroy;
    }

    private WrapperPlayServerPlayerInfo createRemoveInfoReal(Player player, PlayerMeta playerMeta) {

        WrappedGameProfile profile = new WrappedGameProfile(playerMeta.getRealUUID(), playerMeta.getRealName());
        Property property = playerMeta.getRealTextures();

        WrappedSignedProperty signedProperty = new WrappedSignedProperty("textures", property.getValue(), property.getSignature());
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", signedProperty);

        PlayerInfoData playerInfoData = new PlayerInfoData(
            profile, ((CraftPlayer) player).getHandle().playerConnection.player.ping,
            EnumWrappers.NativeGameMode.valueOf(player.getGameMode().name()), null);

        List<PlayerInfoData> playerInfoDataList = new ArrayList<>();
        playerInfoDataList.add(playerInfoData);

        WrapperPlayServerPlayerInfo removeInfo = new WrapperPlayServerPlayerInfo();
        removeInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        removeInfo.setData(playerInfoDataList);

        return removeInfo;
    }

    private WrapperPlayServerPlayerInfo createAddInfoNick(boolean self, Player player, PlayerMeta playerMeta) {

        WrappedGameProfile profile = new WrappedGameProfile(self ? playerMeta.getRealUUID() : playerMeta.getNickUUID(), playerMeta.getNickName());
        Property property = playerMeta.getNickTextures();

        WrappedSignedProperty signedProperty = new WrappedSignedProperty("textures", property.getValue(), property.getSignature());
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", signedProperty);

        PlayerInfoData playerInfoData = new PlayerInfoData(
            profile, ((CraftPlayer) player).getHandle().playerConnection.player.ping,
            EnumWrappers.NativeGameMode.valueOf(player.getGameMode().name()), null);

        List<PlayerInfoData> playerInfoDataList = new ArrayList<>();
        playerInfoDataList.add(playerInfoData);

        WrapperPlayServerPlayerInfo addInfo = new WrapperPlayServerPlayerInfo();
        addInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        addInfo.setData(playerInfoDataList);

        return addInfo;
    }

    private WrapperPlayServerPlayerInfo createRemoveInfoNick(boolean self, Player player, PlayerMeta playerMeta) {

        WrappedGameProfile profile = new WrappedGameProfile(self ? playerMeta.getRealUUID() : playerMeta.getNickUUID(), playerMeta.getNickName());

        Property property = playerMeta.getNickTextures();

        WrappedSignedProperty signedProperty = new WrappedSignedProperty("textures", property.getValue(), property.getSignature());
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", signedProperty);

        PlayerInfoData playerInfoData = new PlayerInfoData(
            profile, ((CraftPlayer) player).getHandle().playerConnection.player.ping,
            EnumWrappers.NativeGameMode.valueOf(player.getGameMode().name()), null);

        List<PlayerInfoData> playerInfoDataList = new ArrayList<>();
        playerInfoDataList.add(playerInfoData);

        WrapperPlayServerPlayerInfo removeInfo = new WrapperPlayServerPlayerInfo();
        removeInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        removeInfo.setData(playerInfoDataList);

        return removeInfo;
    }

    private WrapperPlayServerPlayerInfo createAddInfoReal(Player player, PlayerMeta playerMeta) {

        WrappedGameProfile profile = new WrappedGameProfile(playerMeta.getRealUUID(), playerMeta.getRealName());
        Property property = playerMeta.getRealTextures();

        WrappedSignedProperty signedProperty = new WrappedSignedProperty("textures", property.getValue(), property.getSignature());
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", signedProperty);

        PlayerInfoData playerInfoData = new PlayerInfoData(
            profile, ((CraftPlayer) player).getHandle().playerConnection.player.ping,
            EnumWrappers.NativeGameMode.valueOf(player.getGameMode().name()), null);

        List<PlayerInfoData> playerInfoDataList = new ArrayList<>();
        playerInfoDataList.add(playerInfoData);

        WrapperPlayServerPlayerInfo addInfo = new WrapperPlayServerPlayerInfo();
        addInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        addInfo.setData(playerInfoDataList);

        return addInfo;
    }

    private WrapperPlayServerNamedEntitySpawn createSpawn(Player player, PlayerMeta playerMeta) {
        WrapperPlayServerNamedEntitySpawn spawn = new WrapperPlayServerNamedEntitySpawn();
        spawn.setEntityID(player.getEntityId());
        spawn.setPlayerUUID(playerMeta.getRealUUID());
        spawn.setX(player.getLocation().getX());
        spawn.setY(player.getLocation().getY());
        spawn.setZ(player.getLocation().getZ());
        spawn.setYaw(player.getLocation().getYaw());
        spawn.setPitch(player.getLocation().getPitch());
        spawn.setCurrentItem(0);
        return spawn;
    }

    private WrapperPlayServerRespawn createRespawn(Player player) {
        WrapperPlayServerRespawn respawn = new WrapperPlayServerRespawn();
        respawn.setLevelType(player.getWorld().getWorldType());
        respawn.setGamemode(EnumWrappers.NativeGameMode.valueOf(player.getGameMode().name()));
        respawn.setDifficulty(EnumWrappers.Difficulty.valueOf(player.getWorld().getDifficulty().name()));
        respawn.setDimension(player.getWorld().getEnvironment().getId());
        return respawn;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerMeta playerMeta = playerMetaMap.remove(event.getPlayer().getUniqueId());
        if (playerMeta != null) {
            resetPlayer(event.getPlayer());
            Bukkit.getPluginManager().callEvent(new PlayerPostUnnickEvent(event.getPlayer(), playerMeta));
        }

    }
}