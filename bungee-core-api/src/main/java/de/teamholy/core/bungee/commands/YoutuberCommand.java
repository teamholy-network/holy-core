package de.teamholy.core.bungee.commands;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
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
        player.sendMessage("     §7"+ BungeeTranslateAPI.translate(player,"Join our discord to apply"));
        TextComponent message = new TextComponent("§7"+BungeeTranslateAPI.translate(player,"Bewerben")+" §8» §a§l*"+BungeeTranslateAPI.translate(player,"Click")+"*");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "dc.teamholy.de"));
        player.sendMessage(message);
    }
}
