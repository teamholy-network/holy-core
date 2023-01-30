package de.teamholy.core.bungee;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.bungee.commands.LookupCommand;
import de.teamholy.core.bungee.commands.ban.BanCommand;
import de.teamholy.core.bungee.commands.ban.UnbanCommand;
import de.teamholy.core.bungee.commands.clan.AdminClanCommand;
import de.teamholy.core.bungee.commands.clan.ClanCommand;
import de.teamholy.core.bungee.commands.mute.MuteCommand;
import de.teamholy.core.bungee.commands.mute.UnmuteCommand;
import de.teamholy.core.bungee.commands.punish.EvidenceCommand;
import de.teamholy.core.bungee.commands.punish.PunishReduceCommand;
import de.teamholy.core.bungee.listener.*;
import de.teamholy.core.bungee.manager.BungeePlayerManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.plugin.Plugin;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class BungeeCore extends Plugin {

    @Getter
    private static BungeeCore instance;
    BungeePlayerManager bungeePlayerManager;


    @Getter
    CoreAPI coreAPI;

    public BungeeCore() {
        instance = this;
    }

    @Override
    public void onEnable() {
        coreAPI = new CoreAPI();

        bungeePlayerManager = new BungeePlayerManager(this.coreAPI);


        new LoginListener();
        new BanLoginListener(this);
        new MuteChatListener();
        new ServerConnectedListener();
        new PostLoginListener();
        new PostDisconnectListener();


        new BanCommand();
        new UnbanCommand();
        new PunishReduceCommand();
        new EvidenceCommand();
        new MuteCommand();
        new UnmuteCommand();
        new LookupCommand();
        new AdminClanCommand();
        new ClanCommand();
    }

    @Override
    public void onDisable() {
        coreAPI.onDisable();
    }

    public static CoreAPI getAPI() {
        return instance.getCoreAPI();
    }
}
