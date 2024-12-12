package de.teamholy.core.bungee.manager;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.team.AdminChatCommand;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/* copyright by Yassino */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BungeePlayerManager {

    CoreAPI coreAPI;

    public boolean isOnline(String name) {
        return isOnline(coreAPI.getUuidManager().getUUID(name));
    }

    public boolean isOnline(UUID uuid) {
        if (uuid == null) {
            return false;
        }
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uuid);
        return proxiedPlayer != null && proxiedPlayer.isConnected();
    }

    public String parseOnlinePrefix(String name) {
        return parseOnlinePrefix(coreAPI.getUuidManager().getUUID(name));
    }

    public String parseOnlinePrefix(UUID uuid) {
        if (uuid == null) {
            return "§c";
        }
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uuid);
        if (proxiedPlayer != null && proxiedPlayer.isConnected()) {
            return "§a";
        }
        return "§c";
    }

    public void sendClanMessage(Clan clan, String message) {
        for (UUID member : clan.getMembers()) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(member);
            if (player != null && player.isConnected()) {
                player.sendMessage(message);
            }
        }
    }

    public Set<ProxiedPlayer> getClanMessagePlayers(Clan clan) {
        Set<ProxiedPlayer> uuids = ConcurrentHashMap.newKeySet();
        for (UUID member : clan.getMembers()) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(member);
            if (player != null && player.isConnected()) {
                uuids.add(player);
            }
        }
        return uuids;
    }

    public Set<ProxiedPlayer> getStaffNotifyPlayers() {
        Set<ProxiedPlayer> uuids = ConcurrentHashMap.newKeySet();
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (proxiedPlayer.hasPermission("teamholy.team")) {
                StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(proxiedPlayer.getUniqueId(),
                    () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
                if (staffProfile.isNotify()) uuids.add(proxiedPlayer);
            }
        }
        return uuids;
    }

    public void notifyStaff(String message) {
        ProxyServer.getInstance().getConsole().sendMessage(message);
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (proxiedPlayer.hasPermission("teamholy.team")) {
                StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(proxiedPlayer.getUniqueId(),
                    () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
                if (staffProfile.isNotify()) proxiedPlayer.sendMessage(message);
            }
        }
    }

    public void notifyAdmin(String message) {
        ProxyServer.getInstance().getConsole().sendMessage(message);
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {

            PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
            if (AdminChatCommand.adminRanks.contains(playerProfile.getRank())) {
                StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(proxiedPlayer.getUniqueId(),
                    () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
                if (staffProfile.isNotify()) proxiedPlayer.sendMessage(message);
            }
        }
    }

    public void notifyStaff(BaseComponent message) {
        ProxyServer.getInstance().getConsole().sendMessage(message);
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (proxiedPlayer.hasPermission("teamholy.team")) {
                StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(proxiedPlayer.getUniqueId(),
                    () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
                if (staffProfile.isNotify()) proxiedPlayer.sendMessage(message);
            }
        }
    }


}
