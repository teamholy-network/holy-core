package de.teamholy.core.bukkit.commands;

import de.skydb.translateapi.bindings.BukkitTranslateAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class GamemodeCommand implements CommandExecutor {
    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(
                ChatColor.RED + "This command can only be executed by a player.");
            return false;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("minecraft.command.gamemode")) return false;
        if (args.length == 0) {
            player.sendMessage("§c/"+command.getLabel() + " §a1§7/§a2§7/§a3");
            if (player.hasPermission("minecraft.command.gamemode.others")) player.sendMessage("§c/"+command.getLabel() + " §a1§7/§a2§7/§a3 ("+BukkitTranslateAPI.translate(player,"player")+")");
        } else if (args.length == 1) {
            if (!isValidNumber(args[0])) {
                player.sendMessage("§c"+BukkitTranslateAPI.translate(player,"Ungültige zahl!"));
                return false;
            }
            GameMode gameMode = GameMode.getByValue(Integer.parseInt(args[0]));
            if (gameMode == null) {
                player.sendMessage("§c"+BukkitTranslateAPI.translate(player,"Gamemode")+" §e" + args[0] + " §c"+ BukkitTranslateAPI.translate(player,"gibt es nicht!"));
                return false;
            }
            setGamemode(gameMode,player, player);
        } else if (args.length == 2 && player.hasPermission("minecraft.command.gamemode.others")) {
            if (!isValidNumber(args[0])) {
                player.sendMessage("§c"+BukkitTranslateAPI.translate(player,"Ungültige zahl!"));
                return false;
            }
            GameMode gameMode = GameMode.getByValue(Integer.parseInt(args[0]));
            if (gameMode == null) {
                player.sendMessage("§c" + BukkitTranslateAPI.translate(player,"Gamemode") + " §e" + args[0] + " §c"+BukkitTranslateAPI.translate(player,"gibt es nicht!"));
                return false;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage("§c"+BukkitTranslateAPI.translate(player,"Spieler nicht online!"));
                return false;
            }
            setGamemode(gameMode,player, target);
        }
        return false;
    }


    private void setGamemode(GameMode gamemode, Player player, Player target) {
        target.setGameMode(gamemode);
        target.sendMessage("§a"+BukkitTranslateAPI.translate(target,"Du bist nun im Gamemode") + " §e" + BukkitTranslateAPI.translate(target, gamemode.toString()));
        if (target != player) {
            player.sendMessage(BukkitTranslateAPI.translatePlaceholder(player,"§aDu hast §7{} §ain den gamemode §e{} §agesetzt", target.getName(), gamemode.toString()));
        }
    }

    private boolean isValidNumber(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
