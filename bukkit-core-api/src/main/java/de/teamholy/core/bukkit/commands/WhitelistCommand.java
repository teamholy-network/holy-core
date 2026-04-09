package de.teamholy.core.bukkit.commands;

//import de.skydb.translateapi.bindings.BukkitTranslateAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/* copyright by Yassino */
public class WhitelistCommand implements CommandExecutor {

    public static List<String> WHITELIST = new ArrayList<>();
    public static boolean ISWHITELIST = false;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return false;
        }
        Player player = (Player) commandSender;

        if (!player.hasPermission("teamholy.whitelist")) return false;

        switch (strings.length) {
            case 0 -> {
                sendHelp(player);
                player.sendMessage("");
                player.sendMessage("§a"+"Whitelist is currently"+ " "+ (ISWHITELIST ? "§a§l"+"on" : "§a§l"+"off"));
                StringBuilder builder = new StringBuilder();
                for (String s1 : WHITELIST) {
                    builder.append(s1).append(", ");
                }
                player.sendMessage("§a"+ "Whitelisted players"+": §e" + builder);
            }

            case 1 -> {

                if (strings[0].equalsIgnoreCase("on")) {
                    ISWHITELIST = true;
                    player.sendMessage("§a"+"Whitelist is now"+" §a§l"+"on");
                    player.sendMessage("§a"+"You were added to the whitelist §eautomatically");
                    WHITELIST.add(player.getName());
                } else if (strings[0].equalsIgnoreCase("off")) {
                    ISWHITELIST = false;
                    player.sendMessage("§a"+"Whitelist is now "+"§a§l"+"off");
                } else if (strings[0].equalsIgnoreCase("clear")) {
                    WHITELIST.clear();
                    player.sendMessage("§a"+"Whitelist cleared!");
                } else {
                    sendHelp(player);
                }

            }


            case 2 -> {

                if (strings[0].equalsIgnoreCase("add")) {
                    WHITELIST.add(strings[1]);
                    player.sendMessage("§aPlayer §e" + strings[1] + " §aadded to the whitelist");
                } else if (strings[0].equalsIgnoreCase("remove")) {
                    WHITELIST.remove(strings[1]);
                    player.sendMessage("§a"+"Player"+" §e" + strings[1] + "§a" + " removed from the whitelist");
                } else {
                    sendHelp(player);
                }

            }
        }

        return false;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§c/whitelist add <"+"player"+">");
        player.sendMessage("§c/whitelist remove <"+"player"+">");
        player.sendMessage("§c/whitelist on");
        player.sendMessage("§c/whitelist off");
    }
}
