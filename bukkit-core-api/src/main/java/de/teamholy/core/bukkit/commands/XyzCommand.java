package de.teamholy.core.bukkit.commands;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.CustomBannerManager;
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

        String patternCode = strings[0];

        if (!player.hasPermission("*")) {
            player.sendMessage("Unknown command. Type \"/help\" for help.");
        }


        customBannerManager.loadBanners(); // DEBUG


        customBannerManager.setAndPlaceCustomBanner1(player, Integer.parseInt(patternCode));

        player.sendMessage("test" + " " + patternCode);

        return false;
    }
}
