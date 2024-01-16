package de.teamholy.bungee.login.listener;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import de.teamholy.bungee.login.BungeeLogin;
import de.teamholy.bungee.login.api.TaskAPI;
import de.teamholy.bungee.login.manager.BotManager;
import de.teamholy.bungee.login.manager.CaptchaManager;
import de.teamholy.bungee.login.model.CrackedProfiles;
import de.teamholy.bungee.login.model.PlayerObject;
import de.teamholy.bungee.login.repositories.PlayerConnectRepository;
import de.teamholy.bungee.login.util.UUIDUtility;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.login.filter.NameFilter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@SuppressWarnings("deprecation")
public class EventListener implements Listener {

    PlayerConnectRepository repo = BungeeLogin.repo;

    //ConcurrentHashMap<String, PlayerObject> cache = TeamHolyLogin.cache;

    ConcurrentHashMap<String, CrackedProfiles> crackedipnames = BungeeLogin.crackedipnames;

    ConcurrentHashMap<String, String> iphostname = BungeeLogin.iphostname;

    Set<ProxiedPlayer> loggedin = BungeeLogin.loggedin;

    CaptchaManager captchaManager;

    public EventListener(CaptchaManager captchaManager) {

        this.captchaManager = captchaManager;

        ProxyServer.getInstance().getScheduler().schedule(BungeeLogin.getInstance(), () -> {
            crackedipnames.forEach((key, value) -> {
                if (value.getTime() < System.currentTimeMillis() - 20000) {
                    crackedipnames.remove(key);
                }
            });
        }, 1, 1, TimeUnit.SECONDS);
        ProxyServer.getInstance().getScheduler().schedule(BungeeLogin.getInstance(), () -> {
            iphostname.clear();
        }, 3, 3, TimeUnit.HOURS);
    }

    public CrackedProfiles getCrackedProfile(PendingConnection user) {
        String ip = user.getAddress().getAddress().getHostAddress();
        if (!crackedipnames.containsKey(ip)) {
            CrackedProfiles profile = new CrackedProfiles();
            profile.setIp(ip);
            crackedipnames.put(ip, profile);
        } else {
            crackedipnames.get(ip).setTime(System.currentTimeMillis());
        }
        return crackedipnames.get(ip);
    }

    @EventHandler
    public void onLogin(PreLoginEvent event) {
        if (!NameFilter.isValidName(event.getConnection().getName())) {
            event.setCancelled(true);
            BaseComponent[] text = TextComponent.fromLegacyText("Invalid Name");
            event.setCancelReason(text);
            event.getConnection().disconnect(text);
        }


    }

    @EventHandler
    public void onHandle(LoginEvent event) {
        PendingConnection pendingConnection = event.getConnection();
        String name = pendingConnection.getName();
        if (!containsLoggedIn(name)) {
            if (!pendingConnection.isOnlineMode() && !UUIDUtility.isBedrock(pendingConnection.getUniqueId(), name)) {
                TaskAPI.runAsync(() -> {
                    PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getRepository().findFirstByPlayerName(name);

                    if (playerProfile != null && !playerProfile.getPlayerId().equals(pendingConnection.getUniqueId())) {
                        event.setCancelled(true);

                        String alreadyRegistered = "§cThis name is registered as a §6§lPremium §7user" +
                            "\n §cChange your name to play on §6Team§fHoly" +
                            "\n §7if you are online with Minecraft premium join on §6§lpremium.teamholy.de " +
                            "\n" +
                            "§7if you have any problems regarding your name or account join §cdc.teamholy.de §7for support";

                        if (repo.existsById(name)) {
                            PlayerObject object = repo.findFirstById(name);

                            if (object.isPremium()) {
                                event.setCancelled(true);
                                pendingConnection.disconnect(TextComponent.fromLegacyText(alreadyRegistered));
                                return;
                            }
                        }
                        if (UUIDUtility.isCracked(pendingConnection.getUniqueId(), name)) {
                            if (UUIDUtility.isPremium(playerProfile.getPlayerId(), playerProfile.getPlayerName())) {
                                event.setCancelled(true);
                                pendingConnection.disconnect(TextComponent.fromLegacyText(alreadyRegistered));
                                return;
                            }
                        }


//                        event.setCancelReason(TextComponent.fromLegacyText(alreadyRegistered));
                        //                      pendingConnection.disconnect(TextComponent.fromLegacyText(alreadyRegistered));

                        //                    System.out.println("Cancelled login for " + name + " because of already registered");
                    }
                });
            }
        }
    }

    private boolean containsLoggedIn(String name) {
        for (ProxiedPlayer player : loggedin) {
            if (player.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onLogin(PlayerHandshakeEvent event) {
        if (event.getHandshake().getRequestedProtocol() == 2) {
            String hostname = event.getHandshake().getHost().toLowerCase(Locale.ROOT);
            CrackedProfiles profile = getCrackedProfile(event.getConnection());
            iphostname.put(event.getConnection().getAddress().getAddress().getHostAddress(), hostname);
            if (hostname.contains("premium")) {
                event.getConnection().setOnlineMode(true);
                profile.setPremium(true);
                return;
            } else if (hostname.contains("cracked")) {
                event.getConnection().setOnlineMode(false);
                profile.setPremium(false);
                return;
            } else if (hostname.contains("bedrock")) {
                event.getConnection().setOnlineMode(false);
                profile.setBedrock(true);
                return;
            }
            event.getConnection().setOnlineMode(profile.isPremium());
            if (profile.isPremium()) {
                profile.setPremium(false);
            }
        }
    }

    @EventHandler
    public void onLogin(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String name = player.getName().toLowerCase(Locale.ROOT);
        if (!loggedin.contains(player)) {
            CrackedProfiles profile = getCrackedProfile(player.getPendingConnection());

            if (!player.getPendingConnection().isOnlineMode() && !UUIDUtility.isBedrock(player.getUniqueId(), player.getName())) {
                ServerInfo info = ProxyServer.getInstance().constructServerInfo("login-limbo", new InetSocketAddress("127.0.0.1", 65535), "", false);
                event.setTarget(info);
                TaskAPI.runAsync(() -> {
                    PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getRepository().findFirstByPlayerName(name);

                    if (playerProfile != null) {
                        String alreadyRegistered = "§cThis name is registered as a §6§lPremium §7user" +
                            "\n §cChange your name to play on §6Team§fHoly" +
                            "\n §7if you are online with Minecraft premium join on §6§lpremium.teamholy.de " +
                            "\n" +
                            "§7if you have any problems regarding your name or account join §cdc.teamholy.de §7for support";

                        if (repo.existsById(name)) {
                            PlayerObject object = repo.findFirstById(name);

                            if (object.isPremium()) {
                                event.setCancelled(true);
                                player.disconnect(TextComponent.fromLegacyText(alreadyRegistered));
                                return;
                            }
                        }
                        if (UUIDUtility.isCracked(player.getUniqueId(), name)) {
                            if (UUIDUtility.isPremium(playerProfile.getPlayerId(), playerProfile.getPlayerName())) {
                                event.setCancelled(true);
                                player.disconnect(TextComponent.fromLegacyText(alreadyRegistered));
                                return;
                            }
                        }
                    }

                    if (repo.existsById(name)) {
                        PlayerObject object = repo.findFirstById(name);

                        if (object.isPremium()) {
                            event.setCancelled(true);

                            player.disconnect(TextComponent.fromLegacyText(
                                "§cThis name is registered as a §6§lPremium §7user" +
                                    "\n §cChange your name to play on §6Team§fHoly" +
                                    "\n §7if you are online with Minecraft premium join on §6§lpremium.teamholy.de " +
                                    "\n" +
                                    "§7if you have any problems regarding your name or account join §cdc.teamholy.de §7for support"));
                            return;
                        }
                        for (Entry<String, Long> entry : object.getIps().entrySet()) {
                            if (entry.getValue() > System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2) {
                                if (player.getAddress().getAddress().getHostAddress().equals(entry.getKey())) {
                                    BungeeLogin.login(player);
                                    return;
                                }
                            }
                        }

                    } else {
                        PlayerObject object = new PlayerObject();
                        object.setName(name);
                        object.setUuid(player.getUniqueId());
                        object.setPremium(false);
                        BungeeLogin.setiphostname(player, object);
                        repo.save(object);
                    }

                    PlayerObject object = repo.findFirstById(name);
                    if (object.getPasswordhash() == null) {
                        if (BotManager.isBotProtectionEnableRegister()) {
                            captchaManager.createCaptcha(player);
                        }
                        player.sendMessage(BungeeLogin.PREFIX + "/register (password) (password)");
                    } else {
                        if (BotManager.isBotProtectionEnableLogin()) {
                            captchaManager.createCaptcha(player);
                        }
                        player.sendMessage(BungeeLogin.PREFIX + "/login (password)");
                    }
                });
            } else {
                TaskAPI.runAsync(() -> {
                    profile.setPremium(true);
                    loggedin.add(player);
                    if (repo.existsById(name)) {
                        PlayerObject object = repo.findFirstById(name);
                        if (!object.isPremium()) {
                            object.setPremium(!profile.isBedrock());
                            object.setBedrock(profile.isBedrock());
                            object.setUuid(player.getUniqueId());
                            object.setIps(new HashMap<String, Long>());
                        }

                        object.getIps().put(player.getAddress().getAddress().getHostAddress(), System.currentTimeMillis());
                        BungeeLogin.setiphostname(player, object);
                        repo.save(object);
                    } else {
                        PlayerObject object = new PlayerObject();
                        object.getIps().put(player.getAddress().getAddress().getHostAddress(), System.currentTimeMillis());
                        object.setName(name);
                        object.setPremium(!profile.isBedrock());
                        object.setBedrock(profile.isBedrock());
                        object.setUuid(player.getUniqueId());
                        BungeeLogin.setiphostname(player, object);
                        repo.save(object);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer player) {
            if (captchaManager.getCapcha(player).isPresent()) {
                event.setCancelled(true);
                return;
            }


            System.out.println("Chat is player");
        }

        if (!loggedin.contains(event.getSender())) {
            String message = event.getMessage().toLowerCase(Locale.ROOT);
            if (message.startsWith("/login ")) {
                return;
            }
            if (message.startsWith("/register ")) {
                return;
            }
            event.setCancelled(true);
        }

    }
}