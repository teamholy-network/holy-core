package de.teamholy.core.bungee.commands;

import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

/* copyright by Yassino */
public abstract class SenderCommand extends Command {

    public SenderCommand(String[] commands,String permission) {
        super(Arrays.asList(commands).get(0), permission, commands);
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeCore.getInstance(), this);
    }

    public abstract void execute(CommandSender sender, String[] args);

}
