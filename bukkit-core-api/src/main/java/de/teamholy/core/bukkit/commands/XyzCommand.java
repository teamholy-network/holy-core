package de.teamholy.core.bukkit.commands;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.CustomBannerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class XyzCommand implements CommandExecutor {

    private CustomBannerManager customBannerManager;

    public XyzCommand(BukkitCore bukkitCore) {
        this.customBannerManager = new CustomBannerManager(bukkitCore);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        Player player = (Player) commandSender;

        String instruction = strings[0];



        if (!player.hasPermission("*")) {
            player.sendMessage("Unknown command. Type \"/help\" for help.");
        }


        if (instruction.equalsIgnoreCase("update")) {
            player.sendMessage("§aBanners updated");
            return false;
        } else if (instruction.equalsIgnoreCase("set")) {
            String target = strings[1];
            String bannerId = strings[2];

            if (target == null) {

            }

            Player targetPlayer = Bukkit.getPlayer(target);

            if (targetPlayer == null) {
                player.sendMessage("§cPlayer not found");
                return false;
            }

            player.sendMessage("§aBanner set for " + targetPlayer.getName());
        }

        return false;
    }
}
