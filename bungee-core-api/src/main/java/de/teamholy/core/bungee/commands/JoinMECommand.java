package de.teamholy.core.bungee.commands;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.PublicBroadcastManager;
import de.teamholy.core.bungee.model.JoinME;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class JoinMECommand extends Command {

    private String prefix = "§dJoinME §8× §7";
    private CoreAPI coreAPI = BungeeCore.getAPI();
    public static HashMap<UUID, JoinME> joinMEHashMap = new HashMap<>();

    PublicBroadcastManager publicBroadcastManager = new PublicBroadcastManager();

    public JoinMECommand() {
        super("joinme");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;


        if (args.length == 0) {

            PlayerProfile playerProfile = coreAPI.getPlayerService().getEntity(proxiedPlayer.getUniqueId(),
                    () -> coreAPI.getPlayerService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));


            int tokens = (int) playerProfile.getJoinMeTokens();

            if (tokens == 0 && !proxiedPlayer.hasPermission("teamholy.joinme")) {
                proxiedPlayer.sendMessage(prefix + "You currently §cdont §7have any joinme tokens!");
                proxiedPlayer.sendMessage(prefix + "Do you want to buy joinme tokens? §ashop.teamholy.de");
            } else {

                if (proxiedPlayer.hasPermission("teamholy.joinme")) {
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(proxiedPlayer, "joinme do it");
                } else {
                    proxiedPlayer.sendMessage(prefix + "You currently have §e" + tokens + " §7joinme " + (tokens == 1 ? "token" : "tokens"));
                    TextComponent message = new TextComponent(prefix + "§aClick to create a Joinme!");
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/joinme do it"));

                    proxiedPlayer.sendMessage(message);
                }


            }

        } else if (args.length == 1) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target == null) {
                proxiedPlayer.sendMessage(prefix + "This player does not exist!");
                return;
            }

            if (!joinMEHashMap.containsKey(target.getUniqueId())) {
                proxiedPlayer.sendMessage(prefix + "This player dont have an active joinme!");
                return;
            }

            if (!joinMEHashMap.get(target.getUniqueId()).getServer().equalsIgnoreCase(target.getServer().getInfo().getName())) {
                proxiedPlayer.sendMessage(prefix + "The player is not on the joinme server anymore!");
                return;
            }

            if ((joinMEHashMap.get(target.getUniqueId()).getCooldown() < System.currentTimeMillis())) {
                proxiedPlayer.sendMessage(prefix + "The Joinme has expired!");
                joinMEHashMap.remove(target.getUniqueId());
                return;
            }

            if (joinMEHashMap.get(target.getUniqueId()).getServer().equalsIgnoreCase(proxiedPlayer.getServer().getInfo().getName())) {
                proxiedPlayer.sendMessage(prefix + "You are already on the server!");
                return;
            }

            proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(joinMEHashMap.get(target.getUniqueId()).getServer()));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("do") && args[1].equalsIgnoreCase("it")) {

                if (joinMEHashMap.containsKey(proxiedPlayer.getUniqueId()) && (joinMEHashMap.get(proxiedPlayer.getUniqueId()).getCooldown() > System.currentTimeMillis()) && !proxiedPlayer.hasPermission("teamholy.joinme.bypass")) {
                    proxiedPlayer.sendMessage(prefix + "You already have a Joinme!");
                    return;
                }

                if (proxiedPlayer.getServer().getInfo().getName().toLowerCase().contains("lobby")) {
                    proxiedPlayer.sendMessage(prefix + "You cannot make a Joinme in the lobby!");
                    return;
                }


                PlayerProfile playerProfile = coreAPI.getPlayerService().getEntity(proxiedPlayer.getUniqueId(),
                        () -> coreAPI.getPlayerService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));

                int tokens = (int) playerProfile.getJoinMeTokens();

                if (tokens == 0 && !proxiedPlayer.hasPermission("teamholy.joinme")) {
                    proxiedPlayer.sendMessage(prefix + "You dont have any joinme tokens!");
                } else {
                    if (!proxiedPlayer.hasPermission("teamholy.joinme")) {
                        playerProfile.setJoinMeTokens(playerProfile.getJoinMeTokens() - 1);
                        coreAPI.getPlayerService().saveEntity(playerProfile, true, true);
                    }


                    publicBroadcastManager.sendPublicBroadcast(null, PublicBroadcastManager.BroadcastType.JOINME, proxiedPlayer);
                    joinMEHashMap.put(proxiedPlayer.getUniqueId(), new JoinME(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3), proxiedPlayer.getServer().getInfo().getName()));
                }


            }
        }
    }

}
