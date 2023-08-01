package de.teamholy.core.bungee.commands;

import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Greg */


public class ChatFilterCommand extends Command {
    public ChatFilterCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return;
        }

        BungeeCore.getInstance().getChatFilterManager().loadFilteredWords();
        sender.sendMessage("§cChatFilter §8× §7The chat filter has been reloaded!");
    }
}
