package de.teamholy.core.bukkit.commands;

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
        Player player = (Player) commandSender;

        if (!player.hasPermission("teamholy.whitelist")) return false;

        switch (strings.length) {
            case 0 -> {
                sendHelp(player);
                player.sendMessage("");
                player.sendMessage("§aWhitelist is currently " + (ISWHITELIST ? "§a§lon" : "§a§loff"));
                StringBuilder builder = new StringBuilder();
                for (String s1 : WHITELIST) {
                    builder.append(s1).append(", ");
                }
                player.sendMessage("§aWhitelisted players: §e" + builder);
            }

            case 1 -> {

                if (strings[0].equalsIgnoreCase("on")) {
                    ISWHITELIST = true;
                    player.sendMessage("§aWhitelist is now §a§lon");
                    player.sendMessage("§aYou were added to the whitelist §eautomatically");
                    WHITELIST.add(player.getName());
                } else if (strings[0].equalsIgnoreCase("off")) {
                    ISWHITELIST = false;
                    player.sendMessage("§aWhitelist is now §a§loff");
                } else if (strings[0].equalsIgnoreCase("clear")) {
                    WHITELIST.clear();
                    player.sendMessage("§aWhitelist cleared!");
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
                    player.sendMessage("§aPlayer §e" + strings[1] + " §aremoved from the whitelist");
                } else {
                    sendHelp(player);
                }

            }
        }

        return false;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§c/whitelist add <player>");
        player.sendMessage("§c/whitelist remove <player>");
        player.sendMessage("§c/whitelist on");
        player.sendMessage("§c/whitelist off");
    }
}
