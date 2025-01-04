package de.teamholy.core.bungee.commands;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.CloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.PublicBroadcastManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/* copyright by Yassino */
public class BroadcastCommand extends Command {

    PublicBroadcastManager publicBroadcastManager = new PublicBroadcastManager();

    public BroadcastCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission("teamholy.broadcast"))
            return;

        if (strings.length == 0) {
            player.sendMessage("§6Broadcast §8× §7/broadcast (" + BungeeTranslateAPI.translate(player, "message") + ")");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < strings.length; a++) {
            stringBuilder.append(strings[a] + " ");
        }

        publicBroadcastManager.sendPublicBroadcast(stringBuilder.toString(), PublicBroadcastManager.BroadcastType.GENERAL, null);

    }
}
