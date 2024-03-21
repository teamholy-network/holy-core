package de.teamholy.core.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class GamemodeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        if (!player.hasPermission("minecraft.command.gamemode")) return false;
        if (args.length == 0) {
            player.sendMessage("§c/" + command.getLabel() + "§a1§7/§a2§7/§a3");
            if (player.hasPermission("minecraft.command.gamemode.others"))
                player.sendMessage("§c/" + command.getLabel() + "§a1§7/§a2§7/§a3 (player)");
        } else if (args.length == 1) {
            try {
                GameMode gameMode = GameMode.getByValue(Integer.parseInt(args[0]));
                if (gameMode == null) {
                    player.sendMessage("§cGamemode §e" + args[0] + " §cgibt es nicht!");
                    return false;
                }
                setGamemode(gameMode, player, player);
            } catch (NumberFormatException e) {
                player.sendMessage("§cUngültige zahl!");
            }
        } else if (args.length == 2 && player.hasPermission("minecraft.command.gamemode.others")) {
            try {
                GameMode gameMode = GameMode.getByValue(Integer.parseInt(args[0]));
                if (gameMode == null) {
                    player.sendMessage("§cGamemode §e" + args[0] + " §cgibt es nicht!");
                    return false;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§cSpieler nicht online!");
                    return false;
                }
                setGamemode(gameMode, player, target);
            } catch (NumberFormatException e) {
                player.sendMessage("§cUngültige zahl!");
            }
        }
        return false;
    }


    private void setGamemode(GameMode gamemode, Player player, Player target) {
        target.setGameMode(gamemode);
        target.sendMessage("§aDu bist nun im Gamemode §e" + gamemode);
        if (target != player) {
            player.sendMessage("§aDu hast §7" + target.getName() + " §ain den gamemode §e" + gamemode.toString() + " §agesetzt");
        }
    }

}
