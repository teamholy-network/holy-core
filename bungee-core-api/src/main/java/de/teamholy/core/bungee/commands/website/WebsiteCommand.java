package de.teamholy.core.bungee.commands.website;

import de.teamholy.core.api.constants.Message;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class WebsiteCommand extends Command {
    public WebsiteCommand(String name) {
        super(name, "", "web", "site");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent header = new TextComponent(Message.TOPLINE);
        TextComponent websiteLink = new TextComponent("\n" + Message.HELP_BULLET + "Website: §a teamholy.de");
        websiteLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://teamholy.de"));
        header.addExtra(websiteLink);
        sender.sendMessage(header);
    }
}