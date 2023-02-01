package de.teamholy.core.bungee.manager;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/* copyright by Yassino */
public class BungeePlayerManager {

    private final CoreAPI coreAPI;

    public BungeePlayerManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
    }

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
