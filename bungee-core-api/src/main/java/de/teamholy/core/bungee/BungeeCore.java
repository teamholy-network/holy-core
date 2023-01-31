package de.teamholy.core.bungee;

import de.teamholy.core.api.CoreAPI;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class BungeeCore extends Plugin {

    @Getter
    private static BungeeCore instance;
    BungeePlayerManager bungeePlayerManager;
    PartyManager partyManager;


    @Getter
    CoreAPI coreAPI;

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

        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new FriendCommand("friend",null,"friends"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new FriendListCommand("friendlist","fl"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new MSGCommand("msg"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new ReplyCommand("r"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new PartyChatCommand("partychat","pc","pchat"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new PartyCommand("party",null,"parties"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new ReportCommand("report"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new ReportStaffCommand("reportstaff"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new TokensCommand("tokens"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new JoinMECommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new TeamChatCommand("teamchat","teamholy.team","tc"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new TeamCommand("team","teamholy.team","teamlist"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new TeamNotifyCommand("teamnotify","teamholy.team","notify"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new KickCommand("kick","teamholy.kick","kim"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new NickListCommand("nicklist","teamholy.team","nicks"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new RankCommand("rank","teamholy.rang","rang"));
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), new OnlinetimeCommand("onlinetime"));
    }

    @Override
    public void onDisable() {
        coreAPI.onDisable();
    }

    public static CoreAPI getAPI() {
        return instance.getCoreAPI();
    }
}
