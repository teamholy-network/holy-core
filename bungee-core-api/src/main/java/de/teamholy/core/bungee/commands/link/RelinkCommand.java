/*
 * Copyright (c) 2023. Gin337 (Greg)
 */

package de.teamholy.core.bungee.commands.link;

import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.LinkManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RelinkCommand extends Command {

    LinkManager linkManager;

    public RelinkCommand(String name) {
        super(name);
        linkManager = BungeeCore.getInstance().getLinkManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        linkManager.relinkPlayer(player);
    }
}
