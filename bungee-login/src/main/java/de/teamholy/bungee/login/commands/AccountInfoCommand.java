package de.teamholy.bungee.login.commands;

import de.teamholy.bungee.login.BungeeLogin;
import de.teamholy.bungee.login.api.TaskAPI;
import de.teamholy.bungee.login.model.PlayerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map.Entry;

public class AccountInfoCommand extends Command implements TabExecutor {

    public AccountInfoCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (!player.hasPermission("holylogin.accountinfo")) {
                player.sendMessage(TextComponent.fromLegacyText("§cKeine Rechte"));
                return;
            }
            if (args.length < 1) {
                player.sendMessage(TextComponent.fromLegacyText("§c/accountinfo <player>"));
                return;
            }

            TaskAPI.runAsync(() -> {
                String name = args[0].toLowerCase(Locale.ROOT);
                if (BungeeLogin.repo.existsById(name)) {
                    PlayerObject playerobj = BungeeLogin.repo.findFirstById(name);
                    player.sendMessage(TextComponent.fromLegacyText("§8§m-------§f§l Team§6§lHoly§8 §m-------"));
                    player.sendMessage(TextComponent.fromLegacyText("§7Name §8» §e" + name));
                    player.sendMessage(TextComponent.fromLegacyText("§7Account Type §8» " + PlayerListCommand.getVersion(playerobj.getUuid(), playerobj.getName()).format));
                    if (playerobj.getHostname() != null) {
                        player.sendMessage(TextComponent.fromLegacyText("§7Hostname §8» §e" + playerobj.getHostname()));
                    }
                    player.sendMessage(TextComponent.fromLegacyText("§8 "));
                    player.sendMessage(TextComponent.fromLegacyText("§7IPs"));
                    for (Entry<String, Long> entry : playerobj.getIps().entrySet()) {
                        TextComponent text = new TextComponent();
                        text.setExtra(Arrays.asList(TextComponent.fromLegacyText("§7 §8- §e" + entry.getKey())));
                        text.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/ipinfo " + entry.getKey()));
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§f§lIPInfo")));
                        player.sendMessage(text);
                    }
                    player.sendMessage(TextComponent.fromLegacyText("§8 "));
                    player.sendMessage(TextComponent.fromLegacyText("§8§m-------§f§l Team§6§lHoly§8 §m-------"));
                }
            });
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        ArrayList<String> sug = new ArrayList<String>();
        String complete = "" + args[args.length - 1];
        if (args.length == 1) {
            if (sender.hasPermission("holylogin.accountinfo")) {
                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                    if (all.getName().toLowerCase().startsWith(complete.toLowerCase())) {
                        sug.add(all.getName());
                    }
                }
            }
        }
        return sug;
    }
}
