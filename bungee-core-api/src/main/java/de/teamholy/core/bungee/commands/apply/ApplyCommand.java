package de.teamholy.core.bungee.commands.apply;

import de.teamholy.core.api.constants.Message;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ApplyCommand extends Command {
    public ApplyCommand(String name) {
        super(name, "", "bewerbung", "bewerben");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent header = new TextComponent(Message.TOPLINE);
        TextComponent applyLink = new TextComponent("\n" + Message.HELP_BULLET + "Apply: §a teamholy.de/apply");
        applyLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://teamholy.de/apply"));
        header.addExtra(applyLink);
        sender.sendMessage(header);
    }
}