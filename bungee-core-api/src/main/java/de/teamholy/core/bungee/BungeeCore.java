package de.teamholy.core.bungee;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.manager.CloudManager;
import de.teamholy.core.bungee.commands.*;
import de.teamholy.core.bungee.commands.ban.BanCommand;
import de.teamholy.core.bungee.commands.ban.UnbanCommand;
import de.teamholy.core.bungee.commands.clan.AdminClanCommand;
import de.teamholy.core.bungee.commands.clan.ClanCommand;
import de.teamholy.core.bungee.commands.friend.FriendCommand;
import de.teamholy.core.bungee.commands.friend.FriendListCommand;
import de.teamholy.core.bungee.commands.friend.MSGCommand;
import de.teamholy.core.bungee.commands.friend.ReplyCommand;
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
import de.teamholy.core.bungee.commands.team.TeamChatCommand;
import de.teamholy.core.bungee.commands.team.TeamCommand;
import de.teamholy.core.bungee.commands.team.TeamNotifyCommand;
import de.teamholy.core.bungee.listener.*;
import de.teamholy.core.bungee.manager.BungeePlayerManager;
import de.teamholy.core.bungee.manager.PartyManager;
import eu.koboo.en2do.Credentials;
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

    @Getter
    CoreAPI coreAPI;

    BungeePlayerManager bungeePlayerManager;
    PartyManager partyManager;

    public BungeeCore() {
        instance = this;
    }

    @Override
    public void onEnable() {
        coreAPI = new CoreAPI();

        bungeePlayerManager = new BungeePlayerManager(this.coreAPI);
        partyManager = new PartyManager();


        new LoginListener();
        new BanLoginListener(this);
        new MuteChatListener();
        new ServerConnectedListener();
        new PostLoginListener();
        new PostDisconnectListener();
        new PartyListener();


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
        new CloudMessageListener();

        new StatsCommand(new String[]{"stats","mstats","astats"},null);
        new KickCommand(new String[]{"kick","kim"}, "teamholy.kick");

        ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatFilterListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new CommandListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new MaxIPListener());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new FriendCommand("friend", null, "friends"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new FriendListCommand("friendlist", "fl"));
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
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new RankCommand("rank", "teamholy.rang", "rang"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new OnlinetimeCommand("onlinetime"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this,new PingCommand("ping"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this,new BroadcastCommand("broadcast","teamholy.broadcast","bc"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this,new JumpCommand("jump"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this,new GiveawayCommand("giveaway"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this,new YoutuberCommand("Youtube","","yt","premium+","p+"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this,new HelpCommand("help","","hile","dc","shop","?","discord","apply","forum"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this,new NameMCCommand("namemc","","vote","rewards","like","premium","freepremium"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this,new EasyPermissionCommand("easypermission","","eperms","easyperms"));


        ProxyServer.getInstance().getScheduler().schedule(this,() -> {

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(player.getUniqueId(),() -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));
                playerProfile.setOnlineTime(playerProfile.getOnlineTime() + 60000L);
                BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile,true,true);
            }

            coreAPI.getCloudManager().sendCloudMessage("bukkit","onlineTime_update",null);

            ChatFilterListener.LASTMESSAGES.clear();

            CommandListener.COOLDOWNS.clear();
        },1,1, TimeUnit.MINUTES);

    }

    @Override
    public void onDisable() {
        coreAPI.onDisable();
    }

    public static CoreAPI getAPI() {
        return instance.getCoreAPI();
    }
}
