package de.teamholy.core.bungee.commands.link;

import de.teamholy.core.api.entities.stats.StatsProfile;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.LinkManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LinkCommand extends Command {
    public LinkCommand(String name) {
        super(name);
    }

    private LinkManager linkManager = new LinkManager();

    @Override
    public void execute(CommandSender sender, String[] args) {

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("relink")) {
            linkManager.relinkPlayer(player);
            return;
        }

        if (!linkManager.playerExists(player)) {
            linkManager.insertPlayer(player);
            return;
        }

        if (linkManager.getPlayerLastTimeLoggedIn(player) != 0 && System.currentTimeMillis() - linkManager.getPlayerLastTimeLoggedIn(player) > 24 * 60 * 60 * 1000) {
            linkManager.sendConfirmRelinkOrLinkMessageToPlayer(player);
            return;
        }


        if (linkManager.playerLinked(player)) {
            player.sendMessage("§6Web §8× §7You are already linked!");
            return;
        }


        String linkCode = linkManager.getPlayerLinkCode(player);

        if (linkCode == null || linkCode.isEmpty()) {
            player.sendMessage("§cError: Your code could not be generated or is emtpy. Please open a ticket on our discord");
            return;
        }

        linkManager.sendLinkMessageToPlayer(player, linkCode);




    }
}
