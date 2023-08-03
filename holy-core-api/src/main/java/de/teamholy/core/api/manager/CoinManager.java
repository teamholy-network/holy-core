package de.teamholy.core.api.manager;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.player.PlayerProfile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.text.DecimalFormat;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CoinManager {

    CoreAPI coreAPI;

    public void getCoinsAsync(UUID uuid, Consumer<Long> consumer) {
        coreAPI.getExecutor().execute(() -> consumer.accept(getCoins(uuid)));
    }

    public long getCoins(UUID uuid) {

        PlayerProfile playerProfile = coreAPI.getPlayerService().getEntity(uuid,
            () -> coreAPI.getPlayerService().getRepository().findFirstById(uuid));
        if (playerProfile == null) {
            return 0L;
        }
        return playerProfile.getCoins();
    }

    public void setCoins(UUID uuid, long coins, boolean forceCache) {
        PlayerProfile playerProfile = coreAPI.getPlayerService().getEntity(uuid,
            () -> coreAPI.getPlayerService().getRepository().findFirstById(uuid));
        if (playerProfile == null)
            return;
        long newCoins = coins;
        playerProfile.setCoins(newCoins);
        sendMessage(uuid, newCoins);
        coreAPI.getPlayerService().saveEntity(playerProfile, forceCache, true);
    }

    public void setCoinsAsync(UUID uuid, long coins, boolean forceCache) {
        coreAPI.getExecutor().execute(() -> setCoins(uuid, coins, forceCache));
    }

    public void addCoins(UUID uuid, long coins, boolean forceCache) {
        PlayerProfile playerProfile = coreAPI.getPlayerService().getEntity(uuid,
            () -> coreAPI.getPlayerService().getRepository().findFirstById(uuid));
        if (playerProfile == null) {
            return;
        }
        long newCoins = playerProfile.getCoins() + coins;
        playerProfile.setCoins(newCoins);
        sendMessage(uuid, newCoins);
        coreAPI.getPlayerService().saveEntity(playerProfile, forceCache, true);
    }

    public void addCoinsAsync(UUID uuid, long coins, boolean forceCache) {
        coreAPI.getExecutor().execute(() -> addCoinsAsync(uuid, coins, forceCache));
    }

    public void removeCoins(UUID uuid, long coins, boolean forceCache) {
        PlayerProfile playerProfile = coreAPI.getPlayerService().getEntity(uuid,
            () -> coreAPI.getPlayerService().getRepository().findFirstById(uuid));
        if (playerProfile == null) {
            return;
        }
        long newCoins = playerProfile.getCoins() - coins;
        playerProfile.setCoins(newCoins);
        sendMessage(uuid, newCoins);
        coreAPI.getPlayerService().saveEntity(playerProfile, forceCache, true);
    }

    public void removeCoinsAsync(UUID uuid, long coins, boolean forceCache) {
        coreAPI.getExecutor().execute(() -> addCoinsAsync(uuid, coins, forceCache));
    }

    public String formatInteger(long integer) {
        String pattern = "###,###,###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return decimalFormat.format(integer);
    }

    private void sendMessage(UUID uuid, long coins) {
        coreAPI.getCloudManager().sendCloudMessage("bukkit", "coins_update", JsonDocument.newDocument("uuid", uuid.toString()).append("coins", coins));
    }
}
