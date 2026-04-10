package eu.koboo.markup.commands;

import de.teamholy.core.translation.BukkitTranslateAPI;
import eu.koboo.markup.MarkupAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandReloadPresets implements CommandExecutor {

    private final MarkupAPI markupAPI;

    public CommandReloadPresets(MarkupAPI markupAPI) {
        this.markupAPI = markupAPI;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You're not a player.");
            return false;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("markupapi.reloadpresets")) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§c"+ BukkitTranslateAPI.translate(player,"You don't have permission to do that!"));
            return false;
        }
        if (strings.length != 0) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§c"+BukkitTranslateAPI.translate(player,"Usage")+": §7/reloadpresets");
            return false;
        }

        markupAPI.getPresetManager().reloadPresets();
        player.sendMessage(MarkupAPI.NICK_PREFIX + "§a"+BukkitTranslateAPI.translate(player,"Reloaded player skin-presets!"));

        return true;
    }
}
