package de.teamholy.core.bukkit.commands;

import de.skydb.translateapi.bindings.BukkitTranslateAPI;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

/* copyright by Yassino */
public class ChatclearCommand implements CommandExecutor {

    private ArrayList<String> strings = new ArrayList<>();

    public ChatclearCommand() {
        for (int i = 0; i < 500; i++) {
            strings.add(ChatColor.values()[new Random().nextInt(ChatColor.values().length)] + getSaltString());
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(
                ChatColor.RED + "This command can only be executed by a player.");
            return false;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("teamholy.chatclear"))
            return false;
        Bukkit.getOnlinePlayers().forEach(all ->  {
            if (!all.hasPermission("teamholy.chatclear")) {
                for (String string : this.strings) {
                    all.sendMessage(string);
                }
            }
        });
        Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(BukkitCore.PREFIX + "§6§l"+ BukkitTranslateAPI.translate(all,"The chat was cleared!")));
        return false;
    }


    private String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
