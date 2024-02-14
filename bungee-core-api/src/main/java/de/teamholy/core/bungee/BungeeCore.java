package de.teamholy.core.bungee;

import com.google.common.collect.Lists;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.manager.MetricsManager;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bungee.commands.*;
import de.teamholy.core.bungee.commands.ban.BanCommand;
import de.teamholy.core.bungee.commands.ban.UnbanCommand;
import de.teamholy.core.bungee.commands.clan.AdminClanCommand;
import de.teamholy.core.bungee.commands.clan.ClanCommand;
import de.teamholy.core.bungee.commands.friend.FriendCommand;
import de.teamholy.core.bungee.commands.friend.FriendListCommand;
import de.teamholy.core.bungee.commands.friend.MSGCommand;
import de.teamholy.core.bungee.commands.friend.ReplyCommand;
import de.teamholy.core.bungee.commands.link.LinkCommand;
import de.teamholy.core.bungee.commands.link.RelinkCommand;
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
import de.teamholy.core.bungee.commands.team.AdminChatCommand;
import de.teamholy.core.bungee.commands.team.TeamChatCommand;
import de.teamholy.core.bungee.commands.team.TeamCommand;
import de.teamholy.core.bungee.commands.team.TeamNotifyCommand;
import de.teamholy.core.bungee.listener.*;
import de.teamholy.core.bungee.manager.*;
import de.teamholy.core.bungee.util.Helpers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class BungeeCore extends Plugin {

    @Getter
    private static BungeeCore instance;

    public static String RESTBASE = "http://45.90.97.163:3004/"; //quickfix, removed later

    @Getter
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

    public BungeeCore() {
        instance = this;
    }

    @Override
    public void onEnable() {
        coreAPI = new CoreAPI();

        bungeePlayerManager = new BungeePlayerManager(this.coreAPI);
        partyManager = new PartyManager();
        chatLogManager = new ChatLogManager();
        publicBroadcastManager = new PublicBroadcastManager();
        chatFilterManager = new ChatFilterManager();
        metricsManager = new MetricsManager(this.coreAPI);
        helpers = new Helpers();
        linkManager = new LinkManager();
        proxyManager = new ProxyManager(this.coreAPI);


        new LoginListener();
        new BanLoginListener(this);
        new MuteChatListener();
        new ServerConnectedListener();
        new PostLoginListener(this.proxyManager);
        new PostDisconnectListener();
        new PartyListener();

        if (!ProxyServer.getInstance().getName().startsWith("TestProxy")) {
            redisQueueListener = new RedisQueueListener("127.0.0.1", 6379, "ashGbdkLcxasHvcjsh#aihvb!jsbbbvksddfc");
            redisQueueListener.init(); // Glaub so ist besser habs davor im constructor gemacht
        }

        new BanCommand();
        new UnbanCommand();
        new PunishReduceCommand();
        new EvidenceCommand();
        new MuteCommand();
        new UnmuteCommand();
        new LookupCommand();
        new AdminClanCommand();
        new ClanCommand();
        new CoinsCommand();
        new CustomPunishCommand();
        new CloudMessageListener(this.coreAPI);

        new StatsCommand(new String[]{"stats", "mstats", "astats", "dstats"}, null);
        new KickCommand(new String[]{"kick", "kim"}, "teamholy.kick");


        new ChatFilterListener(this);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatLogListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new CommandListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new MaxIPListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new TabCompleteListener());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new FriendCommand("friend", null, "friends"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new FriendListCommand("friendlist", "fl"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new DeletePlayerCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new MSGCommand("msg"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReplyCommand("r"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PartyChatCommand("partychat", "pc", "pchat"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PartyCommand("party", null, "parties"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReportCommand("report"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReportStaffCommand("reportstaff"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TokensCommand("tokens"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new JoinMECommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TeamChatCommand("teamchat", "teamholy.team", "tc"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TeamCommand("team", "teamholy.team", "teamlist"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TeamNotifyCommand("teamnotify", "teamholy.team", "notify"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new NickListCommand("nicklist", "teamholy.team", "nicks"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new RankCommand("rank", "", "rang"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new OnlinetimeCommand("onlinetime"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PingCommand("ping"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BroadcastCommand("broadcast", "teamholy.broadcast", "bc"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new JumpCommand("jump"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new GiveawayCommand("giveaway"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new YoutuberCommand("Youtube", "", "yt", "premium+", "p+"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new HelpCommand("help", "", "hile", "dc", "shop", "?", "discord", "apply", "forum"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new NameMCCommand("namemc", "", "vote", "rewards", "like", "premium", "freepremium"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new EasyPermissionCommand("easypermission", "", "eperms", "easyperms"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ChatLogCommand("chatlog"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new LinkCommand("link"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new RelinkCommand("relink"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ClearPlayerFromCacheCommand("clearfromcache", "cfcp"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new AdminChatCommand("adminchat"));


        chatFilterManager.loadFilteredWords();


        ProxyServer.getInstance().getScheduler().schedule(this, () -> {

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(player.getUniqueId(), () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));
                playerProfile.setOnlineTime(playerProfile.getOnlineTime() + 60000L);
                BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile, true, true);
            }


            coreAPI.getCloudManager().sendCloudMessage("bukkit", "onlineTime_update", null);


            ChatFilterListener.LASTMESSAGES.clear();

            CommandListener.COOLDOWNS.clear();
        }, 1, 1, TimeUnit.MINUTES);


        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
           publicBroadcastManager.sendPublicBroadcast("§7Did you know that you can do &6/link &7&7to get free &ecoins&7?", PublicBroadcastManager.BroadcastType.GENERAL, null);
        }, 30, 30, TimeUnit.MINUTES);

        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            JsonDocument document = helpers.getMetrics(ProxyServer.getInstance());
            coreAPI.getMetricsManager().saveMetric(document);
        }, 0, 2, TimeUnit.SECONDS);

        //System.out.println(coreAPI.getRankingManager().getUUIDFromRank(Gamemodes.SGFFA, StatsType.ALLTIME,1) + "------------------------------");
        //System.out.println(coreAPI.getRankingManager().getUUIDFromRank(Gamemodes.SGFFA, StatsType.ALLTIME,2) + "------------------------------");
        //System.out.println(coreAPI.getRankingManager().getUUIDFromRank(Gamemodes.SGFFA, StatsType.ALLTIME,0) + "------------------------------");

    }


    @Override
    public void onDisable() {
        coreAPI.getMetricsManager().removeMetric(CloudNetDriver.getInstance().getComponentName());
        coreAPI.onDisable();
    }

    public static CoreAPI getAPI() {
        return instance.getCoreAPI();
    }
}
