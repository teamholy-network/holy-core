package de.teamholy.core.bungee.listener;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.permission.PermissionUpdateUserEvent;
import de.dytanic.cloudnet.driver.permission.IPermissionManagement;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bungee.BungeeCore;

import java.util.Arrays;
import java.util.Optional;

public class CloudRankUpdateListener {

    @EventListener
    public void onRankUpdate(PermissionUpdateUserEvent event) {
        IPermissionUser permissionUser = event.getPermissionUser();
        IPermissionManagement permissionManagement = event.getPermissionManagement();

        Optional<PlayerRank> playerRank = Arrays.stream(PlayerRank.values()).filter(rank -> rank.getName().equals(permissionManagement.getHighestPermissionGroup(permissionUser).getName())).findFirst();

        playerRank.ifPresent(rank -> BungeeCore.getInstance().getPlayerColorCacheManager().put(permissionUser.getUniqueId(), rank.getColorCode()));
    }

}
