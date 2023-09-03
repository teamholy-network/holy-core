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


        customBannerManager.setAndPlaceCustomBanner1(player, "RED", "{BlockEntityTag:{Base:0,Patterns:[{Pattern:cs,Color:11},{Pattern:hhb,Color:14},{Pattern:bs,Color:11},{Pattern:bo,Color:11},{Pattern:ms,Color:14},{Pattern:ts,Color:11}]}}");

        player.sendMessage("test" + " " + patternCode);

        return false;
    }
}
