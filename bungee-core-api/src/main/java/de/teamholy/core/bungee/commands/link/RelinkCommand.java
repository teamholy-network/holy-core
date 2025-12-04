/*
 * Copyright (c) 2023. Gin337 (Greg)
 */

package de.teamholy.core.bungee.commands.link;

import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.LinkManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RelinkCommand extends Command {

    LinkManager linkManager;

    public RelinkCommand(String name) {
        super(name);
        linkManager = BungeeCore.getInstance().getLinkManager();
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(
                ChatColor.RED).create());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        linkManager.relinkPlayer(player);
    }
}
