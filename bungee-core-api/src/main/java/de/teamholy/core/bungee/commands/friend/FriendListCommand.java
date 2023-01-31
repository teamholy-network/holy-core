package de.teamholy.core.bungee.commands.friend;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class FriendListCommand extends Command {

    public FriendListCommand(String name, String... aliases) {
        super(name, null, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        FriendCommand.printFriendList(proxiedPlayer);
    }

}
