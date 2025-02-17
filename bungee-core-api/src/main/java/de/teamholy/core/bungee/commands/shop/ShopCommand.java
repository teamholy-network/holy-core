package de.teamholy.core.bungee.commands.shop;

import de.teamholy.core.api.constants.Message;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ShopCommand extends Command {
    public ShopCommand(String name) {
        super(name, "", "store");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent header = new TextComponent(Message.TOPLINE);
        TextComponent shopLink = new TextComponent("\n" + Message.HELP_BULLET + "Shop: §a shop.teamholy.de");
        shopLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://shop.teamholy.de"));
        header.addExtra(shopLink);
        sender.sendMessage(header);
    }
}
