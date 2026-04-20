package de.teamholy.core.bungee.commands;

import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/* copyright by Yassino */
public class NameMCCommand extends Command {
    public NameMCCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        UUID author = BungeeUtil.parseAuthorUUID(commandSender);
        commandSender.sendMessage("§8§m---------§6§lVOTE§8§m---------");
        commandSender.sendMessage("§7Link§8: §fteamholy.de/vote");
        commandSender.sendMessage("§7" + "Claim your rewards on the website");
        commandSender.sendMessage("§8§m-------------------------");
    }
}
