package de.teamholy.core.bukkit.listener;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.skin.SkinProfile;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.api.utility.UUIDUtility;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.commands.WhitelistCommand;
import de.teamholy.core.bukkit.event.CachedPlayerJoinEvent;
import de.teamholy.core.bukkit.manager.CustomBannerManager;
import de.teamholy.core.bukkit.manager.PacketManager;
import de.teamholy.core.bukkit.manager.PlayerCacheManager;
import de.teamholy.core.bukkit.npc.event.PlayerInteractAtNPCEvent;
import de.teamholy.core.bukkit.npc.models.NPCPlayer;
import de.teamholy.core.bukkit.perks.model.Perk;
import de.teamholy.core.bukkit.perks.enums.PerkRankType;
import eu.koboo.markup.MarkupAPI;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerJoinQuitListener implements Listener {

    BukkitCore bukkitCore;

    private CustomBannerManager customBannerManager;
    private PacketManager packetmanager;


    public PlayerJoinQuitListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        this.customBannerManager = new CustomBannerManager(bukkitCore);
        this.packetmanager = new PacketManager(bukkitCore);
        Bukkit.getPluginManager().registerEvents(this, bukkitCore);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {

        if (WhitelistCommand.ISWHITELIST && !WhitelistCommand.WHITELIST.contains(event.getPlayer().getName())) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cThis server is currently in §c§lwhitelist §cmode");
        }

        if (BukkitCore.RESTART) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cServer restart");
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {

        event.setJoinMessage(null);

        bukkitCore.getCoreAPI().getExecutor().execute(() -> {

            Player player = event.getPlayer();
            if (player == null) return;

            PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));
            ClanPlayerProfile clanPlayerProfile = BukkitCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
            PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getCoreAPI().getPerkPlayerService().getEntity(player.getUniqueId(), () -> BukkitCore.getInstance().getCoreAPI().getPerkPlayerService().getRepository().findFirstById(player.getUniqueId()));


            PlayerCacheManager.CachedBukkitPlayer cachedBukkitPlayer = new PlayerCacheManager.CachedBukkitPlayer(
                player,
                PlayerRank.valueOf(playerProfile.getRank()),
                new NPCPlayer(player),
                (clanPlayerProfile == null ? null : bukkitCore.getCoreAPI().getClanManager().getClanById(clanPlayerProfile.getClanId())),
                perkPlayerProfile
            );


            bukkitCore.getPlayerCacheManager().getCachedPlayers().put(player.getUniqueId(), cachedBukkitPlayer);

            Bukkit.getScheduler().runTask(bukkitCore, () -> Bukkit.getScheduler().runTask(BukkitCore.getInstance(), () -> Bukkit.getPluginManager().callEvent(new CachedPlayerJoinEvent(cachedBukkitPlayer))));

            SkinProfile skinProfile = BukkitCore.getAPI().getSkinService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getSkinService().getRepository().findFirstById(player.getUniqueId()));





            String[] supportedServers = new String[]{"Lobby", "PremiumLobby", "MLGRush", "Clutches", "TestLobby", "Bridge"};

            if (perkPlayerProfile.getCustomBanner().isActivated()) {
                for (String supportedServer : supportedServers) {
                    if (BukkitCore.getInstance().getGroup().startsWith(supportedServer)) {
                        customBannerManager.setAndPlaceCustomBanner1(player, perkPlayerProfile.getCustomBanner());
                    }
                }
            }





            String value;
            String signature;
            if (UUIDUtility.isCracked(player.getUniqueId(), player.getName())) {
                value = "ewogICJ0aW1lc3RhbXAiIDogMTY4ODc0NTgyNzAxOSwKICAicHJvZmlsZUlkIiA6ICIwMDdkNWE2ZGQxNmU0Y2IxOTdhZTQ3NDBhMjUzMWJlMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJJbUJvb18iLAogIC" +
                    "JzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTdjMDZ" +
                    "iMGZlNWE2MjQ0OTA4NGYzODg4ZTYxNDgzZmExNDM2MDQ5YjZhNWRkNmYwYzdkZTU2MGVjMjQ0ZjBjOCIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA" +
                    "6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0=";

                signature = "PlH8r5lPXfm0gxnd9Kw7o7GFA41vVG29pqAMGI+clYbqMZjPrYSKWhoVOFnpVEMhCMPjGEVyn9wB7xzMX4bG44AnFPPkjys3IIIMaZBHh64vakkLHkbB" +
                    "BhPcMYIYrH3l03SblVFaYX0rdgq1xNZXMrW0rMIdj0xtwWZSfb8JNk6jLmM1s5o0kI+j7jXaZplZgXmT/B9e4wpMPxUYmgXR2sQWD4djWMEtXZP95KqkFg0HJRNm916iD6iX6XpUC1HKw7KM7L07xYURR" +
                    "Qn5F4AyETNeUnAHnwZzQC5CQVXoCuwmRtG7ZmtCA2foo6XJ9OUrExo3IcNzFo+sG+hE0eg2Ioa/QzjzGnmSwyTBDsERp/Mj3YEofVO+GjlfywZOt/vULf8o9zPW2t1e22KO6BnaA5FpD4UNtYqMHI5mOqwgI0cI/j3wO+vUI4Y" +
                    "cfsY9im1iUPYZ8dFyoyC45c/a9OegQvOvcalSq2z0dMeA5mb2HsvoiyJjD3CUJeBRWQESha47m2SMhwVlqJ48Gff6sNI" +
                    "gm7+6vPRuySmdmXfCqZmPUkbAwqcWsYoUQ+ZhXn4XU7qiqPoeHrE0dt5rab9D6Mj454rhMZJnaI+7MpylVqvEZ7N88xajgOULFV4anj5jCsyCqQPfb7D1Znqfb028RaNTTi1ZvsvHjyzTKk42v5df0tk=";

            } else {
                EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
                GameProfile gameProfile = entityPlayer.getProfile();
                Property property = gameProfile.getProperties().get("textures").iterator().next();
                value = property.getValue();
                signature = property.getSignature();
            }


            if (skinProfile == null) skinProfile = new SkinProfile();
            if (skinProfile.getValue() == null || !skinProfile.getSignature().equals(signature) || !skinProfile.getValue().equals(value)) {
                skinProfile.setPlayerId(player.getUniqueId());
                skinProfile.setSignature(signature);
                skinProfile.setValue(value);
                BukkitCore.getAPI().getSkinService().saveEntity(skinProfile, true, true);
            } else {
                value = skinProfile.getValue();
                signature = skinProfile.getSignature();
            }

            Bukkit.getScheduler().runTask(bukkitCore, () -> {
                if (!BukkitCore.getInstance().getGroup().toLowerCase().contains("lobby")) {
                    if (playerProfile.isAutoNick()) {
                        Bukkit.getScheduler().runTaskLater(bukkitCore,() -> player.chat("/nick"),1);
                    } else {
                        Bukkit.getScheduler().runTaskLater(bukkitCore,() -> MarkupAPI.updateNameTag(player),7);
                    }
                } else {
                    Bukkit.getScheduler().runTaskLater(bukkitCore,() -> MarkupAPI.updateNameTag(player),7);
                }
            });


        });
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        event.setQuitMessage(null);
        PlayerCacheManager.CachedBukkitPlayer cachedBukkitPlayer = bukkitCore.getPlayerCacheManager().getCachedPlayers().remove(playerUUID);
        if (cachedBukkitPlayer != null) {
            cachedBukkitPlayer.getNpcPlayer().getNpcs().forEach((s, npcEntry) -> {
                if (npcEntry.getHologram() != null) npcEntry.getHologram().delete();
            });
        }
        customBannerManager.removeCustomBanner(event.getPlayer());
        if (packetmanager.gameStatePacketLoopTask.containsKey(playerUUID)) {
            packetmanager.gameStatePacketLoopTask.get(playerUUID).cancel();
            packetmanager.gameStatePacketLoopTask.remove(playerUUID);
        }

    }


}