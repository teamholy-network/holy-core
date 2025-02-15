package de.teamholy.core.bungee.commands.shop;

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
        // Build enhanced shop message with colors and symbols
        TextComponent header = new TextComponent("§8§m---------§r §6§lTeamHoly.de §8§m---------\n");
        TextComponent shopLink = new TextComponent("§e✦ §bShop: §a shop.teamholy.de");
        shopLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://shop.teamholy.de"));
        header.addExtra(shopLink);
        sender.sendMessage(header);
    }
}
