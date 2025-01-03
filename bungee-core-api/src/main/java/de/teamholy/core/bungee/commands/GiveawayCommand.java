package de.teamholy.core.bungee.commands;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class GiveawayCommand extends Command implements Listener {

    private String prefix = "§dGiveaway §8× §f";
    private boolean chat;
    private int winnerNumber;

    public GiveawayCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("teamholy.giveaway"))
            return;

        if (!(args.length >= 2)) {
            player.sendMessage(prefix + "/giveaway ("+ BungeeTranslateAPI.translate(player,"maxnumber")+") ("+BungeeTranslateAPI.translate(player,"price")+")");
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int a = 1; a < args.length; a++) {
                stringBuilder.append(args[a] + " ");
            }

            startGiveaway(Integer.parseInt(args[0]), player, stringBuilder.toString());
        }
    }


    private void startGiveaway(Integer max, ProxiedPlayer player, String price) {
        winnerNumber = new Random().nextInt(max);
        chat = false;
        TextComponent message = new TextComponent("§c§kwsd §f"+BungeeTranslateAPI.translate(player,"Hover me to see the number")+" §c§kwsd");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§f§l" + winnerNumber)));

        player.sendMessage(message);
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            proxiedPlayer.sendMessage("§8§m---------§f§lGIVEAWAY§8§m---------");
            proxiedPlayer.sendMessage(prefix + "§l" + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer,"The number is between {} - {}","§c§l0", "§c§l0"+max));
            proxiedPlayer.sendMessage(prefix + "§l" + BungeeTranslateAPI.translate(proxiedPlayer,"Price") + ": §a§n" + price.replace("&", "§"));
            proxiedPlayer.sendMessage(prefix + "§l" + BungeeTranslateAPI.translate(proxiedPlayer,"Try to guess the number in the chat!"));
            proxiedPlayer.sendMessage(prefix + "§l"+ BungeeTranslateAPI.translate(proxiedPlayer,"Good luck, the chat is enabled in 10 seconds!"));
            proxiedPlayer.sendMessage("§8§m--------------------------");
        }
        ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), () -> {
            chat = true;
            ProxyServer.getInstance().getPlayers().forEach(players -> players.sendMessage(prefix + "§f"+BungeeTranslateAPI.translate(players,"The chat is now enabled!")));
        }, 10, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (event.getMessage().startsWith("/")) return;

        if (!chat && !player.hasPermission("*")) {
            player.sendMessage(prefix + BungeeTranslateAPI.translate(player,"The chat is disabled!"));
            event.setCancelled(true);
        } else {
            if (event.getMessage().equalsIgnoreCase(String.valueOf(winnerNumber))) {
                chat = false;
                ProxyServer.getInstance().getPlayers().forEach(players -> {
                    players.sendMessage(prefix + "§6§l"+BungeeTranslateAPI.translatePlaceholder(players,"The Player {} §6§lhas won!", BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName()));
                    players.sendMessage(prefix + "§6§l"+BungeeTranslateAPI.translate(players,"Number")+"§l§8: §c§l" + winnerNumber);
                    players.sendMessage(prefix + "§7§l"+BungeeTranslateAPI.translate(players,"The chat is enabled in 10 seconds!"));
                });
                ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), () -> {
                    ProxyServer.getInstance().getPluginManager().unregisterListener(this);
                    ProxyServer.getInstance().getPlayers().forEach(players -> players.sendMessage(prefix + "§6"+BungeeTranslateAPI.translate(players,"The chat is now enabled!")));
                }, 10, TimeUnit.SECONDS);
            }
        }

    }

}
