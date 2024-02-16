package de.teamholy.core.bungee.commands.friend;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import static de.teamholy.core.bungee.commands.friend.FriendCommand.prefix;

/* copyright by Yassino */
public class FriendListCommand extends Command {

    public FriendListCommand(String name, String... aliases) {
        super(name, null, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

        if (args.length == 0) {
            FriendCommand.printFriendList(proxiedPlayer, 1);
        } else if (args.length == 1) {
            int page = 1;
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                proxiedPlayer.sendMessage("§cPlease enter a valid number!");
            }

            if (page < 1) page = 1;
            FriendCommand.printFriendList(proxiedPlayer, page);
        } else {
            proxiedPlayer.sendMessage(prefix + "/friend list (page)");
        }

    }

}
