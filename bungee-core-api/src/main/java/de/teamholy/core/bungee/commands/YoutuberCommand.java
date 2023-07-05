package de.teamholy.core.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
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
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        player.sendMessage("     §7Join our discord to apply");
        TextComponent message = new TextComponent("§7Apply §8» §a§l*Click*");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "dc.teamholy.de"));
        player.sendMessage(message);
    }
}
