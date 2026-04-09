package eu.koboo.markup.commands;

//import de.skydb.translateapi.bindings.BukkitTranslateAPI;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.util.PlayerMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNickList implements CommandExecutor {

    private final MarkupAPI markupAPI;

    public CommandNickList(MarkupAPI markupAPI) {
        this.markupAPI = markupAPI;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You're not a player.");
            return false;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("teamholy.team")) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§c"+ "You don't have permission to do that!");
            return false;
        }
        if (!markupAPI.getPresetManager().isLoad()) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§c"+"The nicksystem is currently disabled!");
            return false;
        }
        if (strings.length != 0) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§c"+"Usage"+": §7/nicklist");
            return false;
        }
        player.sendMessage(MarkupAPI.NICK_PREFIX + "§7Nicked players§8:");

        for (PlayerMeta playerMeta : markupAPI.getNickManager().getPlayerMetaMap().values()) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "  §8- §7" + playerMeta.getRealName() + " §8-> §e" + playerMeta.getNickName());
        }

        return true;
    }
}
