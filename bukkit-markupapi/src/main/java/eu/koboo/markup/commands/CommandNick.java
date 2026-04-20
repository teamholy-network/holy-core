package eu.koboo.markup.commands;

import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.util.PlayerPreset;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class CommandNick implements CommandExecutor {

    private final MarkupAPI markupAPI;
    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    public CommandNick(MarkupAPI markupAPI) {
        this.markupAPI = markupAPI;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You're not a player.");
            return false;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("markupapi.nick")) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§c" + "You don't have permission to do that!");
            return false;
        }

        if (!markupAPI.getPresetManager().isLoad()) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§c" + "The nicksystem is currently disabled!");
            return false;
        }

        if (strings.length != 0) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§c"+"Usage"+": §7/nick");
            return false;
        }

        if (cooldown.containsKey(player.getUniqueId()) && System.currentTimeMillis() < cooldown.get(player.getUniqueId())) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§c"+"Please wait!");
            return false;
        }

        if (!markupAPI.getNickManager().hasNickName(player)) {
            PlayerPreset playerPreset = markupAPI.getPresetManager().getPlayerPreset();
            markupAPI.getNickManager().apply(player, playerPreset.getName(), playerPreset.getUuid(), MarkupAPI.getProperty(playerPreset.getValue(), playerPreset.getSignature()));
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§7"+"You're now known as"+"§8: §a" + playerPreset.getName());
        } else {
            markupAPI.getNickManager().resetPlayer(player,false);
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§7"+"You're now unnicked!");
        }

        cooldown.put(player.getUniqueId(),System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));

        return true;
    }
}
