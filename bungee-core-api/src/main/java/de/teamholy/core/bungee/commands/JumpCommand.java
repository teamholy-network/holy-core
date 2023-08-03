package de.teamholy.core.bungee.commands;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class JumpCommand extends Command {

    public String prefix = "§cJump §8× §7";

    public JumpCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission("teamholy.jump"))
            return;

        if (args.length == 1) {

            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(prefix + "This player isn't online");
                return;
            }

            player.connect(target.getServer().getInfo());

            player.sendMessage(prefix + "You jumped to " + BungeeCore.getAPI().getCloudManager().getColor(target.getUniqueId()) + target.getName());
            BungeeCore.getAPI().getCloudManager().sendCloudMessage("bukkit", "report", JsonDocument.newDocument("targetUuid", target.getUniqueId())
                .append("jumperUuid", player.getUniqueId()));
        } else {
            player.sendMessage(prefix + "/Jump (name)");
        }
    }
}
