package de.teamholy.core.bungee.commands;

import de.teamholy.core.translation.BungeeTranslateAPI;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bungee.BungeeCore;
import java.util.Locale;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class TokensCommand extends Command {


    CoreAPI coreAPI = BungeeCore.getAPI();

    public TokensCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        if (args.length == 0 || !proxiedPlayer.hasPermission("teamholy.tokens")) {

            PlayerProfile playerProfile = coreAPI.getPlayerService()
                .getEntity(proxiedPlayer.getUniqueId(),
                    () -> coreAPI.getPlayerService().getRepository()
                        .findFirstById(proxiedPlayer.getUniqueId()));

            proxiedPlayer.sendMessage("§8§m-------------§f§lTOKENS§8§m---------------");
            proxiedPlayer.sendMessage("§dJoinme Tokens §8» §e" +
                (proxiedPlayer.hasPermission("teamholy.joinme") ? "§a§l"
                    + BungeeTranslateAPI.translate(proxiedPlayer, "UNLIMITED") + " §8(§e"
                    + playerProfile.getJoinMeTokens() + "§8)"
                    : playerProfile.getJoinMeTokens()));
            proxiedPlayer.sendMessage(
                "§cStatsreset Tokens §8» §e" + playerProfile.getStatsResetTokens());
            proxiedPlayer.sendMessage("§8§m----------------------------------");
            return;
        }

        if (args.length != 4) {
            commandSender.sendMessage(
                Message.PREFIX + "/tokens joinme (" + BungeeTranslateAPI.translate(proxiedPlayer,
                    "player") + ") add (" + BungeeTranslateAPI.translate(proxiedPlayer, "amount")
                    + ")");
            commandSender.sendMessage(
                Message.PREFIX + "/tokens statsreset (" + BungeeTranslateAPI.translate(
                    proxiedPlayer, "player") + ") add (" + BungeeTranslateAPI.translate(
                    proxiedPlayer, "amount") + ")");
        } else {

            String type;

            if (args[0].toLowerCase(Locale.ROOT).equals("joinme")) {
                type = "joinmeTokens";
            } else if (args[0].toLowerCase(Locale.ROOT).equals("statsreset")) {
                type = "statsresetTokens";
            } else {
                commandSender.sendMessage(
                    Message.PREFIX + BungeeTranslateAPI.translate(proxiedPlayer,
                        "This is not a valid argument") + " (joinme/statsreset)!");
                return;
            }

            UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);
            if (uuid == null) {
                commandSender.sendMessage(
                    Message.PREFIX + BungeeTranslateAPI.translate(proxiedPlayer,
                        "This is not a valid player!"));
                return;
            }

            if (args[2].toLowerCase(Locale.ROOT).equals("add")) {
                try {
                    int number = Integer.parseInt(args[3]);

                    PlayerProfile playerProfile = coreAPI.getPlayerService().getEntity(uuid,
                        () -> coreAPI.getPlayerService().getRepository().findFirstById(uuid));

                    if (type.equalsIgnoreCase("joinmeTokens")) {
                        playerProfile.setJoinMeTokens(playerProfile.getJoinMeTokens() + number);
                    } else {
                        playerProfile.setStatsResetTokens(
                            playerProfile.getStatsResetTokens() + number);
                    }
                    BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile,
                        ProxyServer.getInstance().getPlayer(uuid) != null, true);
                    commandSender.sendMessage(
                        Message.PREFIX + BungeeTranslateAPI.translatePlaceholder(proxiedPlayer,
                            "Added player {} {} {}", args[1], String.valueOf(number),
                            type.replace("Tokens", "") + " tokens") + "!");

                    ProxiedPlayer proxiedPlayer1 = ProxyServer.getInstance().getPlayer(uuid);
                    if (proxiedPlayer1 != null) {
                        if (type.equalsIgnoreCase("statsresetTokens")) {
                            proxiedPlayer1.sendMessage(
                                "§f§lWhoooosh! §7" + BungeeTranslateAPI.translate(proxiedPlayer1,
                                    "You received") + " §a" + number + " §7statsreset " + (
                                    number == 1 ? "token" : "tokens"));
                        } else {
                            proxiedPlayer1.sendMessage(
                                "§f§lWhoooosh! §7" + BungeeTranslateAPI.translate(proxiedPlayer1,
                                    "You received") + " §a" + number + " §7joinme " + (number == 1
                                    ? "token" : "tokens"));
                        }
                    }

                } catch (NumberFormatException e) {
                    commandSender.sendMessage(
                        Message.PREFIX + BungeeTranslateAPI.translate(proxiedPlayer,
                            "This is not a valid number!"));
                }
            }
        }


    }

}
