package de.teamholy.core.bungee.commands;

//import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.manager.PublicBroadcastManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class BroadcastCommand extends Command {

    PublicBroadcastManager publicBroadcastManager = new PublicBroadcastManager();

    public BroadcastCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(
                ChatColor.RED).create());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission("teamholy.broadcast"))
            return;

        if (strings.length == 0) {
            player.sendMessage("§6Broadcast §8× §7/broadcast (" + "message" + ")");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < strings.length; a++) {
            stringBuilder.append(strings[a] + " ");
        }

        publicBroadcastManager.sendPublicBroadcast(stringBuilder.toString(), PublicBroadcastManager.BroadcastType.GENERAL, null);

    }
}
