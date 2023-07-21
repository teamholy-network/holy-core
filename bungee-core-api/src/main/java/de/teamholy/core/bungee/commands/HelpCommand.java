package de.teamholy.core.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class HelpCommand extends Command {

    public HelpCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§8§m---------§f§lHELP§8§m---------");
        sender.sendMessage("§7Web§8: §6teamholy.de");
        sender.sendMessage("§7Commands§8:");
        sender.sendMessage(" §7- §3/link");
        sender.sendMessage(" §7- §5/party");
        sender.sendMessage(" §7- §6/clan");
        sender.sendMessage(" §7- §6/friend");
        sender.sendMessage(" §7- §c/stats");
        sender.sendMessage("§8§m-----------------------");
    }
}
