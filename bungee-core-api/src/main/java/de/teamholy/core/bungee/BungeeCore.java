package de.teamholy.core.bungee;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.manager.MetricsManager;
import de.teamholy.core.bungee.commands.*;
import de.teamholy.core.bungee.commands.apply.ApplyCommand;
import de.teamholy.core.bungee.commands.ban.BanCommand;
import de.teamholy.core.bungee.commands.ban.UnbanCommand;
import de.teamholy.core.bungee.commands.clan.AdminClanCommand;
import de.teamholy.core.bungee.commands.clan.ClanChatCommand;
import de.teamholy.core.bungee.commands.clan.ClanCommand;
import de.teamholy.core.bungee.commands.discord.DiscordCommand;
import de.teamholy.core.bungee.commands.friend.FriendCommand;
import de.teamholy.core.bungee.commands.friend.FriendListCommand;
import de.teamholy.core.bungee.commands.friend.MSGCommand;
import de.teamholy.core.bungee.commands.friend.ReplyCommand;
import de.teamholy.core.bungee.commands.lens.LensCommand;
import de.teamholy.core.bungee.commands.link.LinkV2Command;
import de.teamholy.core.bungee.commands.mute.MuteCommand;
import de.teamholy.core.bungee.commands.mute.UnmuteCommand;
import de.teamholy.core.bungee.commands.party.PartyChatCommand;
import de.teamholy.core.bungee.commands.party.PartyCommand;
import de.teamholy.core.bungee.commands.punish.CustomPunishCommand;
import de.teamholy.core.bungee.commands.punish.EvidenceCommand;
import de.teamholy.core.bungee.commands.punish.KickCommand;
import de.teamholy.core.bungee.commands.punish.PunishReduceCommand;
import de.teamholy.core.bungee.commands.report.ReportCommand;
import de.teamholy.core.bungee.commands.report.ReportStaffCommand;
import de.teamholy.core.bungee.commands.shop.ShopCommand;
import de.teamholy.core.bungee.commands.staff.StaffInfoCommand;
import de.teamholy.core.bungee.commands.team.AdminChatCommand;
import de.teamholy.core.bungee.commands.team.TeamChatCommand;
import de.teamholy.core.bungee.commands.team.TeamCommand;
import de.teamholy.core.bungee.commands.team.TeamNotifyCommand;
import de.teamholy.core.bungee.commands.website.WebsiteCommand;
import de.teamholy.core.bungee.listener.*;
import de.teamholy.core.bungee.manager.*;
import de.teamholy.core.bungee.util.Helpers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents the main plugin class for BungeeCore, extending the functionality
 * for a BungeeProxy server. This class is responsible for initializing the core
 * components, managing command registration, and setting up listeners and scheduled tasks.
 * It serves as the entry point of the plugin and handles global operations such as player
 * management, chat filtering, party management, public broadcasting, metric collection, and more.
 *
 * The plugin integrates various functionalities and utilities necessary for managing
 * a BungeeProxy, simplifying operations such as punishment management, clan commands,
 * staff tools, utility commands, and informational commands.
 *
 * Key Features:
 * - Initializes and manages multiple managers for specific server operations.
 * - Registers various listeners to handle server events.
 * - Provides a wide range of commands tailored for players, staff, and utility tasks.
 * - Manages scheduled tasks for operations like broadcasting and metric updates.
 * - Offers integration with Redis for advanced messaging and caching mechanisms.
 * - Supports additional functionality, including online time tracking, chat logging, cloud communication, and filtered word management.
 *
 * Thread Safety:
 * This class is not guaranteed to be thread-safe. Ensure proper synchronization if the plugin
 * interacts with external threads or APIs outside the main thread.
 *
 * Usage Guidelines:
 * - This class should not be instantiated manually. It is initialized by the BungeeCord plugin framework.
 * - Ensure any external configurations (e.g., Redis settings) are properly set up before running the server.
 * - Modify the plugin responsibly to maintain stability and compatibility with other plugins.
 *
 * Dependencies:
 * - Requires compatibility with the BungeeCord API.
 *
 * Constants:
 * - Several static constants are defined for task intervals and delays to manage plugin scheduling.
 * - `RESTBASE` defines the base URL for REST interactions.
 *
 * Initialization:
 * - The `onEnable` method initializes all managers, registers listeners and commands, and starts required tasks.
 * - Relies on helper methods to organize the setup process for better modularity and maintainability.
 *
 * Note:
 * - This javadoc outlines the intended use case and features of this class. For specific details about each
 *   method or feature, refer to dedicated method documentation.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class BungeeCore extends Plugin {

    private static final long ONLINETIME_UPDATE_INTERVAL = 1;
    private static final long BROADCAST_FIRST_DELAY = 10;
    private static final long BROADCAST_INTERVAL = 30;
    private static final long METRICS_INTERVAL = 2;

    @Getter
    private static BungeeCore instance;

    public static String RESTBASE = "http://185.14.92.243:3004/";

    CoreAPI coreAPI;
    BungeePlayerManager bungeePlayerManager;
    PartyManager partyManager;
    ChatLogManager chatLogManager;
    ChatFilterManager chatFilterManager;
    PublicBroadcastManager publicBroadcastManager;
    MetricsManager metricsManager;
    Helpers helpers;
    LinkManager linkManager;
    RedisQueueListener redisQueueListener;
    ProxyManager proxyManager;
    LensRedisManager lensRedisManager;
    PlayerColorCacheManager playerColorCacheManager;

    public BungeeCore() {
        instance = this;
    }

    @Override
    public void onEnable() {
        initializeManagers();
        registerListeners();
        registerCommands();
        startScheduledTasks();

        getLogger().info("BungeeCore successfully enabled!");
    }

    private void initializeManagers() {
        coreAPI = new CoreAPI();
        bungeePlayerManager = new BungeePlayerManager(coreAPI);
        partyManager = new PartyManager();
        chatLogManager = new ChatLogManager();
        publicBroadcastManager = new PublicBroadcastManager();
        chatFilterManager = new ChatFilterManager();
        metricsManager = new MetricsManager(coreAPI);
        helpers = new Helpers();
        linkManager = new LinkManager();
        proxyManager = new ProxyManager(coreAPI);
        lensRedisManager = new LensRedisManager(coreAPI);
        playerColorCacheManager = new PlayerColorCacheManager();

        if (!isTestProxy()) {
            initializeRedisListener();
        }

        chatFilterManager.loadFilteredWords();
    }

    private boolean isTestProxy() {
        return ProxyServer.getInstance().getName().startsWith("TestProxy");
    }

    private void initializeRedisListener() {
        redisQueueListener = new RedisQueueListener(
            "127.0.0.1",
            6379,
            coreAPI.getConfig().getRedisPassword()
        );
        redisQueueListener.init();
    }

    private void registerListeners() {
        new LoginListener();
        new BanLoginListener(this);
        new MuteChatListener();
        new ServerConnectedListener();
        new PostLoginListener(proxyManager);
        new PostDisconnectListener();
        new PartyListener();
        new ChatFilterListener(this);
        new CloudMessageListener(coreAPI);

        CloudNetDriver.getInstance().getEventManager().registerListener(new CloudRankUpdateListener());

        ProxyServer proxy = ProxyServer.getInstance();
        proxy.getPluginManager().registerListener(this, new ChatLogListener());
        proxy.getPluginManager().registerListener(this, new CommandListener());
        proxy.getPluginManager().registerListener(this, new MaxIPListener());
        proxy.getPluginManager().registerListener(this, new PlayerListListener());
    }

    private void registerCommands() {
        registerPunishmentCommands();
        registerClanCommands();
        registerFriendCommands();
        registerPartyCommands();
        registerStaffCommands();
        registerUtilityCommands();
        registerInformationCommands();
    }

    private void registerPunishmentCommands() {
        new BanCommand();
        new UnbanCommand();
        new MuteCommand();
        new UnmuteCommand();
        new CustomPunishCommand();
        new PunishReduceCommand();
        new EvidenceCommand();
        new KickCommand(new String[]{"kick", "kim"}, "teamholy.kick");
        new LookupCommand();
    }

    private void registerClanCommands() {
        new ClanCommand();
        new AdminClanCommand();
        registerCommand(new ClanChatCommand("cc", "cchat", "clanc"));
    }

    private void registerFriendCommands() {
        registerCommand(new FriendCommand("friend", null, "friends"));
        registerCommand(new FriendListCommand("friendlist", "fl"));
        registerCommand(new MSGCommand("msg"));
        registerCommand(new ReplyCommand("r"));
    }

    private void registerPartyCommands() {
        registerCommand(new PartyCommand("party", null, "parties"));
        registerCommand(new PartyChatCommand("partychat", "pc", "pchat"));
    }

    private void registerStaffCommands() {
        registerCommand(new TeamCommand("team", "teamholy.team", "teamlist"));
        registerCommand(new TeamChatCommand("teamchat", "teamholy.team", "tc"));
        registerCommand(new TeamNotifyCommand("teamnotify", "teamholy.team", "notify"));
        registerCommand(new AdminChatCommand("adminchat"));
        registerCommand(new NickListCommand("nicklist", "teamholy.team", "nicks"));
        registerCommand(new StaffInfoCommand());
        registerCommand(new ReportCommand("report"));
        registerCommand(new ReportStaffCommand("reportstaff"));
        registerCommand(new ChatLogCommand("chatlog"));
    }

    private void registerUtilityCommands() {
        new CoinsCommand(coreAPI);
        new StatsCommand(new String[]{"stats", "mstats", "astats", "dstats"}, null);

        registerCommand(new TokensCommand("tokens"));
        registerCommand(new JoinMECommand());
        registerCommand(new JumpCommand("jump"));
        registerCommand(new PingCommand("ping"));
        registerCommand(new BroadcastCommand("broadcast", "teamholy.broadcast", "bc"));
        registerCommand(new GiveawayCommand("giveaway"));
        registerCommand(new DeletePlayerCommand());
        registerCommand(new ClearPlayerFromCacheCommand("clearfromcache", "cfcp"));
        registerCommand(new EasyPermissionCommand("easypermission", "", "eperms", "easyperms"));
        registerCommand(new LensCommand("lens"));
        registerCommand(new LinkV2Command("link"));
    }

    private void registerInformationCommands() {
        registerCommand(new RankCommand("rank", "", "rang"));
        registerCommand(new OnlinetimeCommand("onlinetime"));
        registerCommand(new YoutuberCommand("Youtube", "", "yt", "premium+", "p+"));
        registerCommand(new HelpCommand("help", "", "hile", "?", "hilfe"));
        registerCommand(new NameMCCommand("namemc", "", "vote", "rewards", "like", "premium", "freepremium"));
        registerCommand(new ShopCommand("shop"));
        registerCommand(new WebsiteCommand("website"));
        registerCommand(new DiscordCommand("discord"));
        registerCommand(new ApplyCommand("apply"));
    }

    private void registerCommand(Object command) {
        if (command instanceof net.md_5.bungee.api.plugin.Command) {
            ProxyServer.getInstance().getPluginManager().registerCommand(
                this,
                (net.md_5.bungee.api.plugin.Command) command
            );
        }
    }

    private void startScheduledTasks() {
        startOnlineTimeUpdateTask();
        startBroadcastTasks();
        startMetricsTask();
    }

    private void startOnlineTimeUpdateTask() {
        ProxyServer.getInstance().getScheduler().schedule(
            this,
            this::updateOnlineTimeForAllPlayers,
            ONLINETIME_UPDATE_INTERVAL,
            ONLINETIME_UPDATE_INTERVAL,
            TimeUnit.MINUTES
        );
    }

    private void updateOnlineTimeForAllPlayers() {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            updatePlayerOnlineTime(player);
        }

        coreAPI.getCloudManager().sendCloudMessage("bukkit", "onlineTime_update", null);

        clearCooldownMaps();
    }

    private void clearCooldownMaps() {
        if (chatFilterManager != null) {
            chatFilterManager.clearLastMessages();
        }
        CommandListener.COOLDOWNS.clear();
    }

    private void updatePlayerOnlineTime(ProxiedPlayer player) {
        PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(
            player.getUniqueId(),
            () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId())
        );

        if (playerProfile != null) {
            playerProfile.setOnlineTime(playerProfile.getOnlineTime() + 60000L);
            BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile, true, true);
        }
    }

    private void startBroadcastTasks() {
        startLinkBroadcast();
        startLanguageBroadcast();
        startApplicationBroadcast();
    }

    private void startLinkBroadcast() {
        ProxyServer.getInstance().getScheduler().schedule(
            this,
            () -> broadcastToAllPlayers("Did you know that you can do {} to get free {}?", "&6/link&7", "&ecoins&7"),
            BROADCAST_FIRST_DELAY,
            BROADCAST_INTERVAL,
            TimeUnit.MINUTES
        );
    }

    private void startLanguageBroadcast() {
        ProxyServer.getInstance().getScheduler().schedule(
            this,
            () -> broadcastToAllPlayers("You can change the language using {}", "&6/language&7"),
            BROADCAST_FIRST_DELAY + 10,
            BROADCAST_INTERVAL,
            TimeUnit.MINUTES
        );
    }

    private void startApplicationBroadcast() {
        ProxyServer.getInstance().getScheduler().schedule(
            this,
            () -> broadcastToAllPlayers("Apply on the {} §7page to join the team", "§6teamholy.de/apply"),
            BROADCAST_FIRST_DELAY + 20,
            BROADCAST_INTERVAL,
            TimeUnit.MINUTES
        );
    }

    private void broadcastToAllPlayers(String messageKey, String... placeholders) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            String message = "§7" + BungeeTranslateAPI.translatePlaceholder(player, messageKey, placeholders);
            publicBroadcastManager.sendGeneral(player, message);
        }
    }

    private void startMetricsTask() {
        ProxyServer.getInstance().getScheduler().schedule(
            this,
            this::collectAndSaveMetrics,
            0,
            METRICS_INTERVAL,
            TimeUnit.SECONDS
        );
    }

    private void collectAndSaveMetrics() {
        JsonDocument document = helpers.getMetrics(ProxyServer.getInstance());
        coreAPI.getMetricsManager().saveMetric(document);
    }

    public String getPlayerColor(UUID uuid) {
        if (playerColorCacheManager.contains(uuid)) {
            return playerColorCacheManager.get(uuid);
        }

        String color = coreAPI.getCloudManager().getColor(uuid);
        playerColorCacheManager.put(uuid, color);
        return color;
    }

    @Override
    public void onDisable() {
        coreAPI.getMetricsManager().removeMetric(CloudNetDriver.getInstance().getComponentName());
        coreAPI.onDisable();

        getLogger().info("BungeeCore successfully disabled!");
    }

    public static CoreAPI getAPI() {
        return instance.getCoreAPI();
    }
}