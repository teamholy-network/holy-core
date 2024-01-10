package eu.koboo.markup.commands;

import com.mojang.authlib.properties.Property;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.util.PlayerMeta;
import eu.koboo.markup.util.SkinPreset;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CommandNickPreset implements CommandExecutor {

    private final MarkupAPI markupAPI;

    public CommandNickPreset(MarkupAPI markupAPI) {
        this.markupAPI = markupAPI;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You're not a player.");
            return false;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("markupapi.nickpreset")) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§cYou don't have permission to do that!");
            return false;
        }
        if (!markupAPI.getPresetManager().isLoad()) {
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§cThe nicksystem is currently disabled!");
            return false;
        }
        if (strings.length != 1) {
            StringBuilder builder = new StringBuilder();
            for (SkinPreset preset : SkinPreset.values()) {
                builder.append(", ").append(preset.getName());
            }
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§cUsage: §7/nickpreset <Preset>");
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§cPresets: §7" + builder.toString().replaceFirst(", ", ""));
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

            Property nickTextures;
            try {
                SkinPreset preset = SkinPreset.valueOf(nickName.toUpperCase(Locale.ROOT));
                nickTextures = preset.getProperty();
                nickName = preset.getName();
            } catch (Exception e) {
                player.sendMessage(MarkupAPI.NICK_PREFIX + "§cError while fetching textures of §e" + nickName);
                return false;
            }

            markupAPI.getNickManager().apply(player, nickName, null, nickTextures);
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§7You're now known as§8: §a" + nickName);
        } else {
            markupAPI.getNickManager().resetPlayer(player);
            player.sendMessage(MarkupAPI.NICK_PREFIX + "§7You're now known as§8: §a" + player.getName());
        }

        return true;
    }
}
