package de.teamholy.bungee.login;

import de.skydb.updater.BungeeUpdaterAPI;
import de.teamholy.bungee.login.commands.*;
import de.teamholy.bungee.login.listener.EventListener;
import de.teamholy.bungee.login.manager.BotManager;
import de.teamholy.bungee.login.manager.CaptchaManager;
import de.teamholy.bungee.login.model.CrackedProfiles;
import de.teamholy.bungee.login.model.PlayerObject;
import de.teamholy.bungee.login.repositories.PlayerConnectRepository;
import de.teamholy.core.bungee.BungeeCore;
import eu.koboo.en2do.MongoManager;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class BungeeLogin extends Plugin {

    @Getter
    private static BungeeLogin instance;

    public static MongoManager manger;

    public static PlayerConnectRepository repo;

    public static Set<ProxiedPlayer> loggedin = ConcurrentHashMap.newKeySet();

    public static ConcurrentHashMap<String, PlayerObject> cache = new ConcurrentHashMap<String, PlayerObject>();

    public static ConcurrentHashMap<String, CrackedProfiles> crackedipnames = new ConcurrentHashMap<String, CrackedProfiles>();

    public static ConcurrentHashMap<String, String> iphostname = new ConcurrentHashMap<String, String>();

    public static String APIKEY = "adasaisuoa2j2j2j2jnvalkooiwuhlkabvd";
    public static String PREFIX = "§6Teamholy §8× §7";

    CaptchaManager captchaManager;


    @Override
    public void onEnable() {
        new BungeeUpdaterAPI(this,"9f58dc68-29b8-4f53-ab69-4d51bcabd801", "NGQ1MWJjYWJkODAx").setHibernat(true).setOnlyempty(true).setNightupdates(true);

        instance = this;

        manger = BungeeCore.getAPI().getMongoManager();

        repo = manger.create(PlayerConnectRepository.class);

        BotManager.init();

        captchaManager = new CaptchaManager();
        captchaManager.init();

        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§7Loaded §a" + this.getDescription().getName()));

        ProxyServer.getInstance().getPluginManager().registerListener(this, new EventListener());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new LoginCommand("login"));

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new RegisterCommand("register"));

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ResetCommand("reset"));

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PremiumOnCommand("setpremium"));

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new AccountInfoCommand("accountinfo"));

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new IPInfoCommand("ipinfo"));

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PlayerListCommand("playerlist"));


    }

    @Override
    public void onDisable() {
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§7Unloaded §c" + this.getDescription().getName()));
        manger.close();

    }

    @SuppressWarnings("deprecation")
    public static void login(ProxiedPlayer player) {
        if (!loggedin.contains(player)) {
            loggedin.add(player);
        }

        String name = player.getName().toLowerCase(Locale.ROOT);
        PlayerObject object = repo.findFirstById(name.toLowerCase(Locale.ROOT));
        object.getIps().put(player.getAddress().getAddress().getHostAddress(),System.currentTimeMillis());
        setiphostname(player, object);
        repo.save(object);
        player.connect(ProxyServer.getInstance().getServerInfo("Lobby-1"));
    }

    @SuppressWarnings("deprecation")
    public static void setiphostname(ProxiedPlayer player, PlayerObject object) {
        if (iphostname.containsKey(player.getAddress().getAddress().getHostAddress())) {
            object.setHostname(iphostname.get(player.getAddress().getAddress().getHostAddress()));
        }
    }

    public static String hash(String pw) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = messageDigest.digest(pw.getBytes());

            StringBuilder stringBuilder = new StringBuilder();
            for (byte hashByte : hashBytes) {
                stringBuilder.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

//	public static boolean isCracked(UUID uuid, String username) {
//		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8)).equals(uuid);
//	}

}
