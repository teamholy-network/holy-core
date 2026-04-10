package de.teamholy.core.bungee.commands;

import de.teamholy.core.translation.BungeeTranslateAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class YoutuberCommand extends Command {


    public YoutuberCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        player.sendMessage("     §7" + BungeeTranslateAPI.translate(player, "Join our discord to apply"));
        TextComponent message = new TextComponent("§7" + BungeeTranslateAPI.translate(player, "Bewerben") + " §8» §a§l*" + BungeeTranslateAPI.translate(player, "Click") + "*");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "dc.teamholy.de"));
        player.sendMessage(message);
    }
}
