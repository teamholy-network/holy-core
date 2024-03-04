package de.teamholy.core.bukkit.npc.models;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Reflection;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Setter
@Getter
@SuppressWarnings("unchecked")
public class NPCEntry extends Reflection {
    private List<Player> players = new ArrayList<>();
    private Player player;
    private String displayName;
    private UUID skinUUID, uuid;
    private GameProfile gameProfile;
    private int entityId, maxSeeRange, maxTargetRange;
    private Location location;
    private ItemStack helmet, chestplate, leggings, boots, heldItem;
    private boolean looker, kickBack;
    private Hologram hologram;

    public NPCEntry(String displayName, UUID skinUUID, Location location, int maxSeeRange, int maxTargetRange, boolean looker, boolean kickBack) {

        if (displayName.length() > 16) {
            this.displayName = displayName.substring(0, 16);
        } else {
            this.displayName = displayName;
        }

        this.skinUUID = skinUUID;

        this.uuid = new UUID(new Random().nextLong(), 0);
        this.gameProfile = new GameProfile(uuid, this.displayName);

        this.entityId = new Random().nextInt(10000000);
        this.location = location;

        this.maxSeeRange = maxSeeRange;
        this.maxTargetRange = maxTargetRange;

        this.looker = looker;
        this.kickBack = kickBack;

        setSkin(skinUUID);
        update();
    }

    public NPCEntry setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
        return this;
    }

    public NPCEntry addHolo(List<String> lines) {
        if (lines.isEmpty()) return this;
        Location locationTemp = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        double i = 0;
        switch (lines.size()) {
            case 1:
                i = 2.6;
                break;
            case 2:
                i = 2.9;
                break;
            case 3:
                i = 3.1;
                break;
            case 4:
                i = 3.4;
                break;
        }
        hologram = HologramsAPI.createHologram(BukkitCore.getInstance(), locationTemp.add(0, i, 0));
        hologram.getVisibilityManager().showTo(player);
        hologram.getVisibilityManager().setVisibleByDefault(false);
        for (String line : lines) {
            hologram.appendTextLine(line);
        }
        return this;
    }

    public NPCEntry setPlayer(Player player) {
        this.player = player;
        return this;
    }

    private boolean hasSkin() {
        return gameProfile.getProperties().get("textures") != null;
    }

    int updateTry = 0;

    public void updateSkin() {
        if (updateTry > 15) {
            return;
        }

        if (!hasSkin())
            setSkin(skinUUID);

        updateTry++;
    }

    public void setSkin(UUID uuid) {
        if (uuid == null) {
            System.out.println("NPC Skin UUID is null");
            return;
        }
        SkinEntry skinEntry = BukkitHolyAPI.getInstance().getBukkitCacheHandler().getSkinEntryHashMap().get(uuid);

        PropertyMap properties = this.gameProfile.getProperties();

        if (skinEntry != null) {
            if (skinEntry.getUuid() == null) {
                skinEntry.setUuid(uuid);
            }
            properties.put("textures", new Property("textures", skinEntry.getValue(), skinEntry.getSignature()));
        } else {
            skinEntry = new SkinEntry();
            skinEntry.setUuid(uuid);
            skinEntry.fetch(temp -> properties.put("textures", new Property("textures", temp.getValue(), temp.getSignature())));
        }
    }


    public void sendPacket(Player player, Packet<?> packet) {
        sendPacket(packet, player);
    }


    public void spawn() {
        if (!this.isInRange(player) && this.players.contains(player)) {
            this.remove();
            updateTry = 0;

        }

        if (this.isInRange(player) && !this.players.contains(player)) {
            updateTry = 0;

            DataWatcher dataWatcher = new DataWatcher(null);
            dataWatcher.a(6, (float) 20);
            dataWatcher.a(10, (byte) 127);

            PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
            setValue(packet, "a", this.entityId);
            setValue(packet, "b", this.gameProfile.getId());
            setValue(packet, "c", intMaker(location.getX()));
            setValue(packet, "d", intMaker(location.getY()));
            setValue(packet, "e", intMaker(location.getZ()));
            setValue(packet, "f", byteMaker(location.getYaw()));
            setValue(packet, "g", byteMaker(location.getPitch()));
            setValue(packet, "h", 0);
            setValue(packet, "i", dataWatcher);

            this.toTablist(player);

            sendPacket(packet, player);

            this.look(this.location.getYaw(), this.location.getPitch(), player);
            this.players.add(player);


            new BukkitRunnable() {

                @Override
                public void run() {
                    removeTablist(player);
                    teleport(location, player);
                }
            }.runTaskLater(BukkitCore.getInstance(), 2);
        }
    }

    public void update() {
        if (player == null) return;
        if (!this.isInRange(player) && this.players.contains(player)) {
            this.remove();
            updateTry = 0;
        }

        if (this.isInRange(player) && !this.players.contains(player)) {
            updateTry = 0;

            DataWatcher dataWatcher = new DataWatcher(null);
            dataWatcher.a(6, (float) 20);
            dataWatcher.a(10, (byte) 127);

            PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
            setValue(packet, "a", this.entityId);
            setValue(packet, "b", this.gameProfile.getId());
            setValue(packet, "c", intMaker(this.location.getX()));
            setValue(packet, "d", intMaker(this.location.getY()));
            setValue(packet, "e", intMaker(this.location.getZ()));
            setValue(packet, "f", byteMaker(this.location.getYaw()));
            setValue(packet, "g", byteMaker(this.location.getPitch()));
            setValue(packet, "h", 0);
            setValue(packet, "i", dataWatcher);

            this.toTablist(player);

            sendPacket(packet, player);
            this.players.add(player);


            this.look(this.location.getYaw(), this.location.getPitch(), player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    removeTablist(player);
                    teleport(location, player);
                    updateEquipment(player);
                    updateHeldItem(player);
                }
            }.runTaskLater(BukkitCore.getInstance(), 2);
        }
    }

    public void setEquipment(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack heldItem) {
        this.heldItem = heldItem;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public void updateEquipment(Player player) {
        PacketPlayOutEntityEquipment[] packet = {
                new PacketPlayOutEntityEquipment(this.entityId, 1, CraftItemStack.asNMSCopy(this.helmet)),
                new PacketPlayOutEntityEquipment(this.entityId, 2, CraftItemStack.asNMSCopy(this.chestplate)),
                new PacketPlayOutEntityEquipment(this.entityId, 3, CraftItemStack.asNMSCopy(this.leggings)),
                new PacketPlayOutEntityEquipment(this.entityId, 4, CraftItemStack.asNMSCopy(this.boots)),
                new PacketPlayOutEntityEquipment(this.entityId, 0, CraftItemStack.asNMSCopy(this.heldItem))};

        if (player == null) {
            return;
        }

        for (int i = 0; i < packet.length; i++) {
            sendPacket(packet[i], player);
        }
    }

    public void setHeldItem(ItemStack heldItem) {
        this.heldItem = heldItem;
    }

    public void updateHeldItem(Player player) {
        if (player == null) {
            return;
        }
        sendPacket(new PacketPlayOutEntityEquipment(this.entityId, 0, CraftItemStack.asNMSCopy(this.heldItem)), player);
    }

    public void remove() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.entityId);

        sendPacket(packet, player);
        this.removeTablist(player);
        this.players.remove(player);
    }

    public void animation(Player player, int animation) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        setValue(packet, "a", this.entityId);
        setValue(packet, "b", (byte) animation);
        sendPacket(player, packet);
    }


    public void toTablist(Player player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameProfile, 1, EnumGamemode.NOT_SET,
                CraftChatMessage.fromString(" ")[0]);

        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(
                packet, "b");
        players.add(data);

        setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        setValue(packet, "b", players);
        sendPacket(packet, player);
    }

    public void removeTablist(Player player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameProfile, 1, EnumGamemode.NOT_SET,
                CraftChatMessage.fromString(" ")[0]);

        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(
                packet, "b");
        players.add(data);

        setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        setValue(packet, "b", players);
        sendPacket(packet, player);
    }

    public void teleport(Location location, Player player) {
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        setValue(packet, "a", this.entityId);
        setValue(packet, "b", (int) (location.getX() * 32.0D));
        setValue(packet, "c", (int) (location.getY() * 32.0D));
        setValue(packet, "d", (int) (location.getZ() * 32.0D));
        setValue(packet, "e", byteMaker(location.getYaw()));
        setValue(packet, "f", byteMaker(location.getPitch()));

        if (player != null) {
            sendPacket(packet, player);
        }
        this.location = location.clone();
    }

    public void look(float yaw, float pitch, Player player) {
        if (player != null) {
            PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(
                    this.entityId, byteMaker(yaw), byteMaker(pitch), true);
            PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();

            setValue(packetHead, "a", this.entityId);
            setValue(packetHead, "b", byteMaker(yaw));

            sendPacket(packet, player);
            sendPacket(packetHead, player);
        }
        this.location.setYaw(yaw);
        this.location.setPitch(pitch);
    }

    public void createTargetLocation(Player player) {
        Location playerLocation = player.getLocation().clone();
        Location npcLocation = this.location.clone();

        double xDifference = playerLocation.getX() - npcLocation.getX();
        double yDifference = playerLocation.getY() - npcLocation.getY();
        double zDifference = playerLocation.getZ() - npcLocation.getZ();

        double xzDistance = Math.sqrt(xDifference * xDifference + zDifference * zDifference);
        double yDistance = Math.sqrt(xzDistance * xzDistance + yDifference * yDifference);


        double endYaw = Math.acos(xDifference / xzDistance) * 180D / 3.141592653589793D;
        double endPitch = Math.acos(yDifference / yDistance) * 180D / 3.141592653589793D - 90D;

        if (zDifference < 0D) {
            endYaw += Math.abs(180D - endYaw) * 2D;
        }
        double newYaw = ((float) endYaw - 90.0D);

        this.look((float) newYaw, (float) endPitch, player);
    }

    public boolean isInRange(Player player) {
        return this.location != null && this.location.getWorld().getUID().equals(player.getWorld().getUID()) && this.location.distance(player.getLocation()) <= this.maxSeeRange;
    }

    private int intMaker(double value) {
        return (int) Math.floor(value * 32.0D);
    }

    private byte byteMaker(float value) {
        return (byte) ((int) (value * 256.0F / 360.0F));
    }
}