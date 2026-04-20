package de.teamholy.core.bungee.commands;

import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.model.ChatLog;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ChatLogCommand extends Command {
    public ChatLogCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(
                ChatColor.RED).create());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (args.length == 0) {
            commandSender.sendMessage("§cChatlog §8× §7" + "Please specify a player!" + " §8(§7/chatlog <" + "Player" + ">§8)");
            return;
        }

        if (args[0].equalsIgnoreCase(player.getName())) {
            commandSender.sendMessage("§cChatlog §8× §7" + "Own chatlog is not allowed!");
            return;
        }

        ProxiedPlayer chatlogPlayer = ProxyServer.getInstance().getPlayer(args[0]);

        if (chatlogPlayer == null) {
            commandSender.sendMessage("§cChatlog §8× §7" + "Player not found!");
            return;
        }

        ChatLog chatLog;
        chatLog = BungeeCore.getInstance().getChatLogManager().createChatlog(player.getUniqueId(), chatlogPlayer);

        if (chatLog == null) {
            commandSender.sendMessage("§cChatlog §8× §7" + "Chatlog failed to create!");
            return;
        }


        TextComponent message = new TextComponent("§cChatlog §8× §7Chatlog for §c" + chatlogPlayer.getName() + " §7has been generated! (");
        TextComponent clickText = new TextComponent("Click");
        clickText.setColor(ChatColor.YELLOW);
        clickText.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://teamholy.de/chatlog/" + chatLog.getChatLogId()));
        clickText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to open the chatlog").create()));
        message.addExtra(clickText);
        message.addExtra("§8)");
        player.sendMessage(message);


    }
}
