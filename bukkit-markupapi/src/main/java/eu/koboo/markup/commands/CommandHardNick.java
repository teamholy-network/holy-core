package eu.koboo.markup.commands;

import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.util.PlayerMeta;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHardNick implements CommandExecutor {

    private final MarkupAPI markupAPI;

    public CommandHardNick(MarkupAPI markupAPI) {
        this.markupAPI = markupAPI;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You're not a player.");
            return false;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("markupapi.hardnick")) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§cYou don't have permission to do that!");
            return false;
        }
        if (!markupAPI.getPresetManager().isLoad()) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§cThe nicksystem is currently disabled!");
            return false;
        }
        if (strings.length != 1) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§cUsage: §7/hardnick <Name>");
            return false;
        }

        if (!markupAPI.getNickManager().hasNickName(player)) {

            String nickName = strings[0];
            Player target = Bukkit.getPlayer(nickName);
            if (target != null) {
                player.sendMessage(MarkupAPI.NICK_PREFIX + "§cThis nickname is already taken!");
                return false;
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                PlayerMeta playerMeta = markupAPI.getNickManager().getPlayerMeta(online.getUniqueId());
                if (playerMeta == null) {
                    continue;
                }
                if (playerMeta.getRealName().equalsIgnoreCase(nickName)) {
                    player.sendMessage(MarkupAPI.NICK_PREFIX + "§cThis nickname is already taken!");
                    return false;
                }
            }

            try {
                markupAPI.getPresetManager().loadPreset(nickName, preset -> {
                    if (preset == null) {
                        player.sendMessage("§cError while getting skin of §e" + nickName + "§c!");
                        return;
                    }
                    Bukkit.getScheduler().runTask(markupAPI, () -> {
                        markupAPI.getNickManager().apply(player, preset.getName(), preset.getUuid(), MarkupAPI.getProperty(preset.getValue(), preset.getSignature()));
                        player.sendMessage(MarkupAPI.NICK_PREFIX + "§7You're now known as§8: §a" + nickName);
                    });
                });
            } catch (Exception e) {
                player.sendMessage("§cError while getting skin of §e" + nickName + "§c!");
                return false;
            }
        } else {
            markupAPI.getNickManager().resetPlayer(player, false);
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§7You're now unnicked!");
        }

        return true;
    }
}
