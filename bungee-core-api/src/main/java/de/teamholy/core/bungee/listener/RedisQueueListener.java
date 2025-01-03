/*
 * Copyright (c) 2023. Gin337 (Greg)
 */

package de.teamholy.core.bungee.listener;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.api.manager.CloudManager;
import de.teamholy.core.api.manager.CoinManager;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.ChatFilterManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;

import java.awt.*;
import java.util.List;


public class RedisQueueListener {

    Jedis jedis; /* TODO: 10.08.2021 Anstatt Jedis, RedissonManager benutzen.
     * Kann ich grad nicht machen, weil ich irgendein hurensohn error bekomme
     * Also halts maul bitte */

    ChatFilterManager chatFilterManager;
    CoinManager coinManager;
    CloudManager cloudManager;

    public RedisQueueListener(String host, int port, String auth) {
        jedis = new Jedis(host, port);
        if (!auth.isEmpty()) {
            jedis.auth(auth);
        }
        chatFilterManager = BungeeCore.getInstance().getChatFilterManager();
        coinManager = BungeeCore.getAPI().getCoinManager();
        cloudManager = BungeeCore.getAPI().getCloudManager();
    }


    public void init() {
        BungeeCore.getAPI().getExecutor().execute(() -> {
            while (true) {

                try {

                    List<String> jobData = jedis.brpop(10, "commandsQueue");
                    if (jobData != null) {
                        String msg = jobData.get(1);
                        String[] args = msg.split(" ");


                        switch (args[0]) {
                            case "coinsadd" -> {
                                String target = args[1];
                                String amount = args[2];
                                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);
                                if (targetPlayer != null) {
                                    coinManager.addCoins(targetPlayer.getUniqueId(), Integer.parseInt(amount), true);
                                    targetPlayer.sendMessage(" ");
                                    targetPlayer.sendMessage("§6Web §8× §7You received §6" + amount + " Coins§7!");
                                    targetPlayer.sendMessage(" ");
                                    sendDiscordWebhook(msg);
                                }
                            }
                            case "refreshChatFilter" -> {
                                chatFilterManager.loadFilteredWords();
                                sendDiscordWebhook(msg);
                            }
                            case "weblinked" -> {
                                String target = args[1];

                                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);
                                coinManager.addCoins(targetPlayer.getUniqueId(), 500, true);

                                ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> {
                                    proxiedPlayer.sendMessage(" ");
                                    TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&8[&c&l!&8] " + BungeeCore.getInstance().getPlayerColor(targetPlayer.getUniqueId()) + targetPlayer.getName() + " &alinked &7his account with our website &7and received &e500 &7Coins! &7Get your &ecoins&7 by &alinking &7your profile with "));
                                    TextComponent linkMessage = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6/link&7!"));
                                    linkMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/link"));
                                    linkMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GOLD + "/link").create()));
                                    message.addExtra(linkMessage);
                                    proxiedPlayer.sendMessage(message);
                                    proxiedPlayer.sendMessage(" ");
                                });

                            }
                            case "updateCustomBanner" -> {
                                String type = args[1];
                                String target = args[2];
                                String instruction = args[3];
                                JsonDocument command = new JsonDocument().append("type", type).append("target", target).append("instruction", instruction);

                                cloudManager.sendCloudMessage("bukkit", "banner", command);
                                sendDiscordWebhook("Update Custom Banner for " + target);
                            }
                            default -> {
                                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), msg);
                                sendDiscordWebhook(msg);
                            }
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void sendDiscordWebhook(String msg) {
        DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1136069824842309663/TK307wKJKBf4qgtx_0tkZL0SOUeeMKlNgpOXdxDsCsUKtbFuheIzWV_kryrSTUsCH1Ak");
        webhook.setAvatarUrl("https://i.imgur.com/k3mtKpE.png");
        webhook.setUsername("Redis");
        webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Redis").addField("New command queued", msg, true).setColor(Color.ORANGE).setThumbnail("https://i.imgur.com/0w7sO7f.png").setFooter("TeamHolyDE", ""));


        webhook.execute();
    }
}
