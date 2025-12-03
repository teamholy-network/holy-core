package de.teamholy.core.bungee.listener;

import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/**
 * LoginListener listens for player login events on a BungeeCord server.
 * It performs actions such as registering the player's data and validating
 * their clan membership upon login.
 *
 * This class implements the Listener interface to handle events,
 * specifically the LoginEvent from the server.
 */
public class LoginListener implements Listener {

    public LoginListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }

    @EventHandler
    public void onLogin(LoginEvent loginEvent) {
        UUID playerId = loginEvent.getConnection().getUniqueId();
        String playerName = loginEvent.getConnection().getName();

        registerPlayer(playerName, playerId);
        validateClanMembership(playerId);
    }

    private void registerPlayer(String name, UUID uuid) {
        BungeeCore.getAPI().getUuidManager().register(name, uuid);
    }

    private void validateClanMembership(UUID playerId) {
        BungeeCore.getAPI().getClanPlayerService().getEntityAsync(
            playerId,
            () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(playerId),
            this::handleClanPlayerProfile
        );
    }

    private void handleClanPlayerProfile(ClanPlayerProfile clanPlayerProfile) {
        if (clanPlayerProfile == null) {
            return;
        }

        boolean isValidClanMember = BungeeCore.getAPI().getClanManager()
            .loadAndForce(clanPlayerProfile.getPlayerId(), clanPlayerProfile.getClanId());

        if (!isValidClanMember) {
            BungeeCore.getAPI().getClanPlayerService().deleteEntity(clanPlayerProfile);
        }
    }
}