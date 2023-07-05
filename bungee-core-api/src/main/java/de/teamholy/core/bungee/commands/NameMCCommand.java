package de.teamholy.core.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class NameMCCommand extends Command {
    public NameMCCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        commandSender.sendMessage("§8§m---------§f§lNAMEMC§8§m---------");
        commandSender.sendMessage("§7Link§8: §fteamholy.de/go/namemc");
        commandSender.sendMessage("§7Like us to get §6Premium §7& §dJoinme tokens");
        commandSender.sendMessage("§7Claim your rewards in the lobby at the npc");
        commandSender.sendMessage("§8§m-------------------------");
    }
}
