package de.teamholy.core.bungee.commands.discord;

//import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.constants.Message;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.plugin.Command;

public class DiscordCommand extends Command {
    public DiscordCommand(String name) {
        super(name, "", "dc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent header = new TextComponent(Message.TOPLINE);
        TextComponent discordLink = new TextComponent("\n" + Message.HELP_BULLET + "Discord: §a dc.teamholy.de");
        discordLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://dc.teamholy.de"));
        header.addExtra(discordLink);
        sender.sendMessage(header);
    }
}