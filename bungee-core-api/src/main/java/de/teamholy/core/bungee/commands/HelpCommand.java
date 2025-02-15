package de.teamholy.core.bungee.commands;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
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
        UUID author = BungeeUtil.parseAuthorUUID(sender);
        
        if (args.length == 0) {
            // Build main help menu with clickable subcommands and fancy formatting
            TextComponent main = new TextComponent("§8§m--------- §6Teamholy.de §8§m---------\n");
            TextComponent commandsLabel = new TextComponent("§e✦ §a"+"Alle Befehle"+":\n");
            main.addExtra(commandsLabel);
            
            TextComponent link = new TextComponent("§e✦ §a/link\n");
            link.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help link"));
            main.addExtra(link);
            
            TextComponent party = new TextComponent("§e✦ §a/party\n");
            party.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help party"));
            main.addExtra(party);
            
            TextComponent clan = new TextComponent("§e✦ §a/clan\n");
            clan.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help clan"));
            main.addExtra(clan);
            
            TextComponent friends = new TextComponent("§e✦ §a/friends\n");
            friends.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help friend"));
            main.addExtra(friends);
            
            TextComponent stats = new TextComponent("§e✦ §a/stats\n");
            stats.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help stats"));
            main.addExtra(stats);
            
            // Add extras as separate components with fancy symbols and colors
            TextComponent website = new TextComponent("§e✦ §bWebsite: §a teamholy.de\n");
            main.addExtra(website);
            TextComponent discord = new TextComponent("§e✦ §bDiscord: §a dc.teamholy.de\n");
            main.addExtra(discord);
            TextComponent shop = new TextComponent("§e✦ §bShop: §a shop.teamholy.de\n");
            main.addExtra(shop);
            
            sender.sendMessage(main);
        } else if (args[0].equalsIgnoreCase("friend")) {
            TextComponent friendMenu = new TextComponent("----- Teamholy.de: Friend System -----\n");
            TextComponent add = new TextComponent("/friend add\n");
            add.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend add"));
            friendMenu.addExtra(add);
            
            TextComponent remove = new TextComponent("/friend remove\n");
            remove.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend remove"));
            friendMenu.addExtra(remove);
            sender.sendMessage(friendMenu);
        } else if (args[0].equalsIgnoreCase("link")) {
            TextComponent linkMenu = new TextComponent("----- Teamholy.de: Link System -----\n");
            TextComponent connect = new TextComponent("/link connect\n");
            connect.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/link connect"));
            linkMenu.addExtra(connect);
            TextComponent info = new TextComponent("/link info\n");
            info.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/link info"));
            linkMenu.addExtra(info);
            sender.sendMessage(linkMenu);
        } else if (args[0].equalsIgnoreCase("party")) {
            TextComponent partyMenu = new TextComponent("----- Teamholy.de: Party System -----\n");
            TextComponent create = new TextComponent("/party create\n");
            create.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party create"));
            partyMenu.addExtra(create);
            TextComponent invite = new TextComponent("/party invite\n");
            invite.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party invite"));
            partyMenu.addExtra(invite);
            TextComponent leave = new TextComponent("/party leave\n");
            leave.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party leave"));
            partyMenu.addExtra(leave);
            sender.sendMessage(partyMenu);
        } else if (args[0].equalsIgnoreCase("clan")) {
            TextComponent clanMenu = new TextComponent("----- Teamholy.de: Clan System -----\n");
            TextComponent create = new TextComponent("/clan create\n");
            create.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan create"));
            clanMenu.addExtra(create);
            TextComponent info = new TextComponent("/clan info\n");
            info.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan info"));
            clanMenu.addExtra(info);
            TextComponent invite = new TextComponent("/clan invite\n");
            invite.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan invite"));
            clanMenu.addExtra(invite);
            sender.sendMessage(clanMenu);
        } else if (args[0].equalsIgnoreCase("stats")) {
            TextComponent statsMenu = new TextComponent("----- Teamholy.de: Stats System -----\n");
            TextComponent check = new TextComponent("/stats check\n");
            check.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stats check"));
            statsMenu.addExtra(check);
            sender.sendMessage(statsMenu);
                } else {
            sender.sendMessage("§8§m---------§6§lTeamHoly§8§m---------");
            sender.sendMessage("§7" + BungeeTranslateAPI.translate(author, "Commands") + " §8: " +
                "/link, /party, /clan, /friends, /stats, /shop");
            sender.sendMessage("Website: teamholy.de | Discord: dc.teamholy.de | Shop: shop.teamholy.de");
        }
    }
}
