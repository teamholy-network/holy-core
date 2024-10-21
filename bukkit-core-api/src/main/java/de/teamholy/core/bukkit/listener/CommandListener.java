package de.teamholy.core.bukkit.listener;

import de.teamholy.core.bukkit.BukkitCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Locale;

/* copyright by Yassino */
@Getter
public class CommandListener implements Listener {

    public CommandListener(BukkitCore bukkitCore) {
        Bukkit.getPluginManager().registerEvents(this, bukkitCore);
    }

    public static String[] blockedCommands = {"holograms","pl","plugins","tell","me","?","about","icanhasbukkit","ver","version","help"};

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if ((event.getMessage().toLowerCase(Locale.ROOT).startsWith("/vulcan") || event.getMessage().toLowerCase().startsWith("/karhu")) && !player.hasPermission("teamholy.anticheat")) {
            player.sendMessage("Unknown command. Type \"/help\" for help.");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onCommandList(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("teamholy.plugins")) return;
        for (String blockedCommand : blockedCommands) {
            if (event.getMessage().toLowerCase(Locale.ROOT).startsWith("/" + blockedCommand) || event.getMessage().contains(":")) {
                player.sendMessage("Unknown command. Type \"/help\" for help.");
                event.setCancelled(true);
                return;
            }
        }
    }

}
