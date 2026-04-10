package de.teamholy.core.bungee.commands;

import de.teamholy.core.translation.BungeeTranslateAPI;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/* copyright by Yassino */
public class HelpCommand extends Command {

    public HelpCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        UUID author = BungeeUtil.parseAuthorUUID(sender);

        // show main help menu
        TextComponent main = new TextComponent(Message.TOPLINE);
        TextComponent commandsLabel = new TextComponent("\n"+Message.HELP_BULLET+BungeeTranslateAPI.translate(author, "Alle Befehle") + ":\n");
        main.addExtra(commandsLabel);
        
        // Create single hover text instance
        Text hoverText = new Text("§7» §a" + BungeeTranslateAPI.translate(author, "Click Here") + " §7«");
        
        TextComponent link = new TextComponent(Message.HELP_BULLET + "/link\n");
        link.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/link"));
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        main.addExtra(link);

        TextComponent party = new TextComponent(Message.HELP_BULLET + "/party\n");
        party.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party"));
        party.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        main.addExtra(party);

        TextComponent clan = new TextComponent(Message.HELP_BULLET + "/clan\n");
        clan.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan"));
        clan.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        main.addExtra(clan);

        TextComponent friends = new TextComponent(Message.HELP_BULLET + "/friends\n");
        friends.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends"));
        friends.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        main.addExtra(friends);

        TextComponent stats = new TextComponent(Message.HELP_BULLET + "/stats\n");
        stats.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stats"));
        stats.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        main.addExtra(stats);

        // Update links with updated symbols
        TextComponent website = new TextComponent(Message.HELP_BULLET + "/website\n");
        website.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/website"));
        website.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        main.addExtra(website);

        TextComponent discord = new TextComponent(Message.HELP_BULLET + "/discord\n");
        discord.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/discord"));
        discord.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        main.addExtra(discord);

        TextComponent shop = new TextComponent(Message.HELP_BULLET + "/shop\n");
        shop.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/shop"));
        shop.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        main.addExtra(shop);

        TextComponent apply = new TextComponent(Message.HELP_BULLET + "/apply\n");
        apply.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/apply"));
        apply.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        main.addExtra(apply);

        sender.sendMessage(main);
    }
}
