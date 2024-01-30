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

import static de.teamholy.core.bungee.BungeeCore.RESTBASE;

public class ProxyManager {

    CoreAPI coreAPI;

    private final RListAsync<String> proxyCollection;

    public final String kickMessage;


    public ProxyManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        this.proxyCollection = coreAPI.getRedissonManager().getRedissonClient().getList("proxy_collection");
        this.kickMessage = createKickMessage();
    }

    public void addProxy(String proxy) {
        proxyCollection.addAsync(proxy);
    }

    public void removeProxy(String proxy) {
        proxyCollection.removeAsync(proxy);
    }

    public RFuture<Boolean> containsProxy(String proxy) {
        return proxyCollection.containsAsync(proxy);
    }

    public void checkProxy(String proxy, ContainsProxyCallback callback) {
        coreAPI.getExecutor().execute(() -> {
            try {
                URL url = new URL(RESTBASE + "holy/vpn/check/" + proxy + "/adasaisuoa2j2j2j2jnvalkooiwuhlkabvd");
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder stringBuilder = new StringBuilder();

                while (scanner.hasNext()) {
                    stringBuilder.append(scanner.next());
                }

                JSONObject json = new JSONObject(stringBuilder.toString());
                boolean isProxy = json.optString("isproxy", "false").equalsIgnoreCase("true") || json.optString("ishosting", "false").equalsIgnoreCase("true");
                String countryName = json.optString("countryname", "");
                String org = json.optString("org", "");

                callback.onResult(isProxy, countryName, org);


                scanner.close();

            } catch (Exception e) {
                e.printStackTrace();
            }


        });
    }

    public void checkZplays(String proxy, LucaZplaysCallback callback) {
        coreAPI.getExecutor().execute(() -> {
            try {
                URL url = new URL("http://ipcheck.skydb.de/residental?ip=" + proxy);
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder stringBuilder = new StringBuilder();

                while (scanner.hasNext()) {
                    stringBuilder.append(scanner.next());
                }

                callback.onResult(stringBuilder.toString().contains("false"));


                scanner.close();

            } catch (Exception e) {
                e.printStackTrace();
            }


        });
    }

    public void checkASN(String proxy, ASNCallback callback) {
        coreAPI.getExecutor().execute(() -> {
            try {
                URL url = new URL("http://ipcheck.skydb.de/getinfo?ip=" + proxy);
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder stringBuilder = new StringBuilder();

                while (scanner.hasNext()) {
                    stringBuilder.append(scanner.next());
                }

                JSONObject json = new JSONObject(stringBuilder.toString());
                int asnInt = json.optInt("ASN", -1);
                String asn = asnInt == -1 ? "" : String.valueOf(asnInt);

                callback.onResult(asn);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }




    public interface ContainsProxyCallback {
        void onResult(boolean isProxy, String countryName, String org);
    }

    public interface LucaZplaysCallback {
        void onResult(boolean isProxy);
    }

    public interface ASNCallback {
        void onResult(String asn);
    }

    public String createKickMessage() {
        return "§cYou got kicked from the network! §7[§cError: 403§7]\n\n§7If nothing is wrong with your connection, please just open a ticket on our Discord.";
    }


    public void sendProxyWarning(ProxiedPlayer proxiedPlayer, String proxy) {

        coreAPI.getExecutor().execute(() -> {
            checkProxy(proxy, (isProxy, countryName, org) -> {
                    DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1071587864330125372/hvtEqts6aBI7wTWq5DUp13QiWD9byAU-XGEN8hJTsAv1PyEl4tITwSO9kxgADkcMsCC6");
                    webhook.setAvatarUrl("https://i.imgur.com/k3mtKpE.png");
                    webhook.setUsername("ProxyFilter");
                    webhook.addEmbed(
                        new DiscordWebhook.EmbedObject()
                            .setTitle("Proxyfilter")
                            .addField("Name", proxiedPlayer.getName(), true)
                            .addField("UUID", proxiedPlayer.getUniqueId().toString(), true)
                            .addField("Proxy", proxy + ", " + countryName + " (" + org + ")", false)
                            .setThumbnail("https://minotar.net/helm/" + proxiedPlayer.getUniqueId().toString() + "/100.png")
                            .setColor(Color.orange)
                            .setFooter("TeamHolyDE", "https://i.imgur.com/0w7sO7f.png")

                    );

                    webhook.execute();
                }

            );

        });
    }

    public void sendAsnWarning(ProxiedPlayer proxiedPlayer, String proxy, String asn) {

        coreAPI.getExecutor().execute(() -> {
            checkProxy(proxy, (isProxy, countryName, org) -> {
                    DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1071587864330125372/hvtEqts6aBI7wTWq5DUp13QiWD9byAU-XGEN8hJTsAv1PyEl4tITwSO9kxgADkcMsCC6");
                    webhook.setAvatarUrl("https://i.imgur.com/k3mtKpE.png");
                    webhook.setUsername("ProxyFilter");
                    webhook.addEmbed(
                        new DiscordWebhook.EmbedObject()
                            .setTitle("Proxyfilter (AS Blacklist)")
                            .setDescription("User " + proxiedPlayer.getName() + " (" + proxiedPlayer.getUniqueId().toString() + ") hit our AS blacklist")
                            .addField("Name", proxiedPlayer.getName(), true)
                            .addField("UUID", proxiedPlayer.getUniqueId().toString(), true)
                            .addField("Proxy", proxy + ", " + countryName + " (" + org + ")", false)
                            .addField("AS", asn, false)
                            .setThumbnail("https://minotar.net/helm/" + proxiedPlayer.getUniqueId().toString() + "/100.png")
                            .setColor(Color.orange)
                            .setFooter("TeamHolyDE", "https://i.imgur.com/0w7sO7f.png")

                    );

                    webhook.execute();
                }

            );

        });
    }
}
