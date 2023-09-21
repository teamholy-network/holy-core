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

        if (!player.hasPermission("*")) {
            player.sendMessage("§6Web §8× §7This command is currently disabled. We dont know when it will be back. Hopefully soon!");
            return;
        }



        if (player == null) {
            return;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("relink")) {
            player.sendMessage("Debug: Relink your account");
            linkManager.relinkPlayer(player);
            return;
        }

        if (!linkManager.playerExists(player)) {
            player.sendMessage("Debug: Insert player");
            linkManager.insertPlayer(player);
            return;
        }

        if (System.currentTimeMillis() - linkManager.getPlayerLastTimeLoggedIn(player) > 24 * 60 * 60 * 1000) {
            player.sendMessage("Debug: Send confirm message");
            linkManager.sendConfirmRelinkOrLinkMessageToPlayer(player);
            return;
        }

        if (linkManager.playerLinked(player)) {
            player.sendMessage("Debug: Player is already linked");
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
