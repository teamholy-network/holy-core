package de.teamholy.core.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class PingCommand extends Command {

    public PingCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (args.length > 0) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

            if (target != null) {
                
                if (!player.hasPermission("teamholy.team")) {
                    player.sendMessage(" §8-> §7You don't have the permission for that");
                    return;
                }
                player.sendMessage(" §8-> §7" + target.getName() + "'s ping§8: §6" + target.getPing() + "§7ms");
            } else {

                player.sendMessage(" §8-> §7Player not online");
            }
        } else {

            player.sendMessage(" §8-> §7Your ping§8: §6" + player.getPing() + "§7ms");
        }

    }
}
