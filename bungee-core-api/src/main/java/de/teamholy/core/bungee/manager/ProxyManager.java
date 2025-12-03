package de.teamholy.core.bungee.manager;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.utility.DiscordWebhook;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONObject;
import org.redisson.api.RFuture;
import org.redisson.api.RListAsync;

import java.awt.*;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.teamholy.core.bungee.BungeeCore.RESTBASE;

/**
 * The ProxyManager class provides functionality for managing and checking proxy IPs,
 * sending Discord notifications, and performing ASN lookups.
 *
 * It integrates with a CoreAPI instance for executing tasks asynchronously
 * and uses Redisson for working with a distributed list of proxies. The class also
 * sends warnings and notifications via Discord webhooks based on certain
 * conditions such as proxy detection, ASN blacklists, or duplicate profiles.
 */
public class ProxyManager {

    private static final String DISCORD_WEBHOOK_URL = "https://discord.com/api/webhooks/1071587864330125372/hvtEqts6aBI7wTWq5DUp13QiWD9byAU-XGEN8hJTsAv1PyEl4tITwSO9kxgADkcMsCC6";
    private static final String API_KEY = "adasaisuoa2j2j2j2jnvalkooiwuhlkabvd";
    private static final String ASN_CHECK_URL = "http://ipcheck.skydb.de/getinfo?ip=";
    private static final int ERROR_CODE_FORBIDDEN = 403;
    private static final int ERROR_CODE_CONFLICT = 409;

    private final CoreAPI coreAPI;
    private final RListAsync<String> proxyCollection;
    private final Logger logger;

    public final String kickMessage;
    public final String kickMessageProfileExists;

    public ProxyManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        this.proxyCollection = coreAPI.getRedissonManager().getRedissonClient().getList("proxy_collection");
        this.logger = Logger.getLogger(ProxyManager.class.getName());
        this.kickMessage = createKickMessage(ERROR_CODE_FORBIDDEN);
        this.kickMessageProfileExists = createKickMessage(ERROR_CODE_CONFLICT);
    }

    public void addProxy(String ipAddress) {
        proxyCollection.addAsync(ipAddress);
    }

    public void removeProxy(String ipAddress) {
        proxyCollection.removeAsync(ipAddress);
    }

    public RFuture<Boolean> containsProxy(String ipAddress) {
        return proxyCollection.containsAsync(ipAddress);
    }

    public void checkProxy(String ipAddress, ContainsProxyCallback callback) {
        coreAPI.getExecutor().execute(() -> {
            try {
                URL url = new URL(RESTBASE + "holy/vpn/check/" + ipAddress + "/" + API_KEY);
                String response = fetchUrlContent(url);

                JSONObject json = new JSONObject(response);
                boolean isProxy = isProxyOrHosting(json);
                String countryName = json.optString("countryname", "Unknown");
                String org = json.optString("org", "Unknown");

                callback.onResult(isProxy, countryName, org);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error checking proxy for IP: " + ipAddress, e);
                callback.onResult(false, "Error", "Error");
            }
        });
    }

    private boolean isProxyOrHosting(JSONObject json) {
        String isProxy = json.optString("isproxy", "false");
        String isHosting = json.optString("ishosting", "false");
        return isProxy.equalsIgnoreCase("true") || isHosting.equalsIgnoreCase("true");
    }

    public void checkASN(String ipAddress, ASNCallback callback) {
        coreAPI.getExecutor().execute(() -> {
            try {
                URL url = new URL(ASN_CHECK_URL + ipAddress);
                String response = fetchUrlContent(url);

                JSONObject json = new JSONObject(response);
                int asnInt = json.optInt("ASN", -1);
                String asn = asnInt == -1 ? "" : String.valueOf(asnInt);

                callback.onResult(asn);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error checking ASN for IP: " + ipAddress, e);
                callback.onResult("");
            }
        });
    }

    private String fetchUrlContent(URL url) throws Exception {
        try (Scanner scanner = new Scanner(url.openStream())) {
            StringBuilder content = new StringBuilder();
            while (scanner.hasNext()) {
                content.append(scanner.next());
            }
            return content.toString();
        }
    }

    private String createKickMessage(int errorCode) {
        return "§cYou got kicked from the network! §7[§cError: " + errorCode + "§7]\n\n" +
            "§7If nothing is wrong with your connection, please open a ticket on our Discord.";
    }

    public void sendProxyWarning(ProxiedPlayer player, String ipAddress) {
        coreAPI.getExecutor().execute(() ->
            checkProxy(ipAddress, (isProxy, countryName, org) ->
                sendDiscordWebhook(
                    player,
                    "Proxyfilter",
                    null,
                    ipAddress,
                    countryName,
                    org,
                    null
                )
            )
        );
    }

    public void sendDuplicateWarning(ProxiedPlayer player) {
        coreAPI.getExecutor().execute(() ->
            sendDiscordWebhook(
                player,
                "Proxyfilter",
                "User " + player.getName() + " (" + player.getUniqueId() + ") tried to join with a new uuid but the same name",
                null,
                null,
                null,
                null
            )
        );
    }

    public void sendAsnWarning(ProxiedPlayer player, String ipAddress, String asn) {
        coreAPI.getExecutor().execute(() ->
            checkProxy(ipAddress, (isProxy, countryName, org) ->
                sendDiscordWebhook(
                    player,
                    "Proxyfilter (AS Blacklist)",
                    "User " + player.getName() + " (" + player.getUniqueId() + ") hit our AS blacklist",
                    ipAddress,
                    countryName,
                    org,
                    asn
                )
            )
        );
    }

    private void sendDiscordWebhook(ProxiedPlayer player, String title, String description,
                                    String ipAddress, String countryName, String org, String asn) {
        try {
            DiscordWebhook webhook = new DiscordWebhook(DISCORD_WEBHOOK_URL);
            webhook.setAvatarUrl("https://i.imgur.com/k3mtKpE.png");
            webhook.setUsername("ProxyFilter");

            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setTitle(title)
                .addField("Name", player.getName(), true)
                .addField("UUID", player.getUniqueId().toString(), true)
                .setThumbnail("https://minotar.net/helm/" + player.getUniqueId() + "/100.png")
                .setColor(Color.ORANGE)
                .setFooter("TeamHolyDE", "https://i.imgur.com/0w7sO7f.png");

            if (description != null) {
                embed.setDescription(description);
            }

            if (ipAddress != null && countryName != null && org != null) {
                embed.addField("Proxy", ipAddress + ", " + countryName + " (" + org + ")", false);
            }

            if (asn != null) {
                embed.addField("AS", asn, false);
            }

            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending Discord webhook", e);
        }
    }

    @FunctionalInterface
    public interface ContainsProxyCallback {
        void onResult(boolean isProxy, String countryName, String org);
    }

    @FunctionalInterface
    public interface ASNCallback {
        void onResult(String asn);
    }
}