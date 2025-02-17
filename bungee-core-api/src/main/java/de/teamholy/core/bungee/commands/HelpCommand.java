package de.teamholy.core.bungee.commands;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/* copyright by Yassino */
public class HelpCommand extends Command {

    public HelpCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Always show main help menu regardless of args
        TextComponent main = new TextComponent(Message.TOPLINE);
        TextComponent commandsLabel = new TextComponent("\n§e✦ §aAlle Befehle:" + "\n");
        main.addExtra(commandsLabel);

        // Changed symbols and colors to use Message.HELP_BULLET and §7 for command text
        TextComponent link = new TextComponent(Message.HELP_BULLET + "/link\n");
        link.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/link"));
        main.addExtra(link);

        TextComponent party = new TextComponent(Message.HELP_BULLET + "/party\n");
        party.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party"));
        main.addExtra(party);

        TextComponent clan = new TextComponent(Message.HELP_BULLET + "/clan\n");
        clan.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan"));
        main.addExtra(clan);

        TextComponent friends = new TextComponent(Message.HELP_BULLET + "/friends\n");
        friends.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends"));
        main.addExtra(friends);

        TextComponent stats = new TextComponent(Message.HELP_BULLET + "/stats\n");
        stats.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stats"));
        main.addExtra(stats);

        // Update links with updated symbols
        TextComponent website = new TextComponent(Message.HELP_BULLET + "Website: §a teamholy.de\n");
        website.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://teamholy.de"));
        main.addExtra(website);

        TextComponent discord = new TextComponent(Message.HELP_BULLET + "Discord: §a dc.teamholy.de\n");
        discord.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://dc.teamholy.de"));
        main.addExtra(discord);

        TextComponent shop = new TextComponent(Message.HELP_BULLET + "Shop: §a shop.teamholy.de\n");
        shop.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://shop.teamholy.de"));
        main.addExtra(shop);

        sender.sendMessage(main);
    }
}
