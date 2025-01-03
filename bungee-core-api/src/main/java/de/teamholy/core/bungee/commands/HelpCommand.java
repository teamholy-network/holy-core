package de.teamholy.core.bungee.commands;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
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
        sender.sendMessage("§8§m---------§f§l" + "HELP" + "§8§m---------");
        sender.sendMessage("§7Web§8: §6teamholy.de");
        sender.sendMessage("§7Discord§8: §3dc.teamholy.de");
        sender.sendMessage("§7" + BungeeTranslateAPI.translate(author, "Commands") + "§8:");
        sender.sendMessage(" §7- §3/link");
        sender.sendMessage(" §7- §5/party");
        sender.sendMessage(" §7- §6/clan");
        sender.sendMessage(" §7- §6/friend");
        sender.sendMessage(" §7- §c/stats");
        sender.sendMessage("§8§m-----------------------");
    }
}
