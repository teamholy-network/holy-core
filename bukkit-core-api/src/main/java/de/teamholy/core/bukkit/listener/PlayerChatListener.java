package de.teamholy.core.bukkit.listener;

import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerChatListener implements Listener {

    BukkitCore bukkitCore;



    public PlayerChatListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        Bukkit.getPluginManager().registerEvents(this, bukkitCore);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage().replace("%", "%%");

        if (!message.contains("@")) return;

        int index = message.indexOf("@");

        if (index < message.length() - 1) {
            String name = message.substring(index + 1).split(" ", 2)[0];

            List<Player> players = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getName().toLowerCase().startsWith(name.toLowerCase()))
                .collect(Collectors.toList());

            if (players.size() == 1) {
                Player target = players.get(0);
                String string = ChatColor.AQUA + "@§l" + target.getName() + ChatColor.RESET;
                message = message.replaceFirst("@" + name, string);
                event.setMessage(message);
            }

        }








    }
}
