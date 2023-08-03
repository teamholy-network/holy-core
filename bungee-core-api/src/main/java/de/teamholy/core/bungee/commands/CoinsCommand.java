package de.teamholy.core.bungee.commands;

import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/* copyright by Yassino */
public class CoinsCommand extends SenderCommand {
    public CoinsCommand() {
        super(new String[]{"coins", "vaupelbucks"}, null);
    }

    private String prefix = "§eCoins §8× §7";

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

            if (!proxiedPlayer.hasPermission("teamholy.coins")) {
                sendCoins(proxiedPlayer, proxiedPlayer.getUniqueId());
                return;
            }

            if (args.length == 0) {
                proxiedPlayer.sendMessage(prefix + "/coins add (name) (amount)");
                proxiedPlayer.sendMessage(prefix + "/coins set (name) (amount)");
                proxiedPlayer.sendMessage(prefix + "/coins remove (name) (amount)");
                proxiedPlayer.sendMessage(prefix + "/coins (name)");
                sendCoins(proxiedPlayer, proxiedPlayer.getUniqueId());
            } else if (args.length == 1) {
                UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(args[0]);
                if (uuid == null) {
                    proxiedPlayer.sendMessage(prefix + "Player not found");
                    return;
                }

                sendCoins(proxiedPlayer, uuid);
            } else if (args.length == 3) {

                UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);
                if (uuid == null) {
                    proxiedPlayer.sendMessage(prefix + "Player not found");
                    return;
                }

                int coinsArg;
                try {
                    coinsArg = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    proxiedPlayer.sendMessage(prefix + "Amount is not a number!");
                    return;
                }

                ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(uuid);
                boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

                PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(uuid, () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(uuid));
                if (args[0].equalsIgnoreCase("add")) {
                    playerProfile.setCoins(playerProfile.getCoins() + coinsArg);
                    proxiedPlayer.sendMessage(prefix +
                        "You added " + BungeeCore.getAPI().getCloudManager().getColor(uuid) + args[1] + " §6" + BungeeCore.getAPI().getCoinManager().formatInteger(coinsArg) + " §ecoins");

                } else if (args[0].equalsIgnoreCase("set")) {
                    playerProfile.setCoins(coinsArg);

                    proxiedPlayer.sendMessage(prefix +
                        "You set " + BungeeCore.getAPI().getCloudManager().getColor(uuid) + args[1] + " §6" + BungeeCore.getAPI().getCoinManager().formatInteger(coinsArg) + " §ecoins");

                } else if (args[0].equalsIgnoreCase("remove")) {
                    playerProfile.setCoins(playerProfile.getCoins() - coinsArg);

                    proxiedPlayer.sendMessage(prefix +
                        "You removed " + BungeeCore.getAPI().getCloudManager().getColor(uuid) + args[1] + " §6" + BungeeCore.getAPI().getCoinManager().formatInteger(coinsArg) + " §ecoins");
                }

                BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile, isOnline, true);


            }


        }
    }


    private void sendCoins(ProxiedPlayer player, UUID uuid) {
        BungeeCore.getAPI().getPlayerService().getEntityAsync(uuid, () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(uuid), playerProfile -> {
            player.sendMessage(prefix + BungeeCore.getAPI().getCloudManager().getColor(uuid) + playerProfile.getPlayerName() + " §7has §e" + playerProfile.getCoins() + " §6coins");
        });
    }
}
