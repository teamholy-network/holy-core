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
        sender.sendMessage("§7Discord§8: §3dc.teamholy.de");
        sender.sendMessage("§7Shop§8: §ashop.teamholy.de");
        sender.sendMessage("§7Forum§8: §bforum.teamholy.de");
        sender.sendMessage("§7Commands§8:");
        sender.sendMessage(" §7- §3/dcverify");
        sender.sendMessage(" §7- §5/party");
        sender.sendMessage(" §7- §6/clan");
        sender.sendMessage(" §7- §6/friend");
        sender.sendMessage("§8§m-----------------------");
    }
}
