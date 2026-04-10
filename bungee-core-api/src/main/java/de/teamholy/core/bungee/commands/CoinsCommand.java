package de.teamholy.core.bungee.commands;

import de.teamholy.core.translation.BungeeTranslateAPI;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/* copyright by Yassino */
public class CoinsCommand extends SenderCommand {

    CoreAPI coreAPI;

    public CoinsCommand(CoreAPI coreAPI) {
        super(new String[]{"coins", "vaupelbucks"}, null);
        this.coreAPI = coreAPI;
    }

    private String prefix = "§eCoins §8× §7";

    @Override
    public void execute(CommandSender sender, String[] args) {

        UUID author = BungeeUtil.parseAuthorUUID(sender);

        if (sender instanceof ProxiedPlayer player && !player.hasPermission("teamholy.coins")) {
            sendCoins(sender, "");
            return;
        }


        if (args.length == 0) {
            sender.sendMessage(prefix + "/coins add ("+ BungeeTranslateAPI.translate(author,"name")+") ("+BungeeTranslateAPI.translate(author,"amount")+")");
            sender.sendMessage(prefix + "/coins set ("+BungeeTranslateAPI.translate(author,"name")+") ("+BungeeTranslateAPI.translate(author,"amount")+")");
            sender.sendMessage(prefix + "/coins remove ("+BungeeTranslateAPI.translate(author,"name")+") ("+BungeeTranslateAPI.translate(author,"amount")+")");
            sender.sendMessage(prefix + "/coins ("+BungeeTranslateAPI.translate(author,"name")+")");
            sendCoins(sender, "");
        } else if (args.length == 1) {
            sendCoins(sender, args[0]);
        } else if (args.length == 3) {

            UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(args[1]);
            if (uuid == null) {
                sender.sendMessage(prefix + BungeeTranslateAPI.translate(author,"Player not found"));
                return;
            }

            int coinsArg;
            try {
                coinsArg = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(prefix + BungeeTranslateAPI.translate(author,"Amount is not a number!"));
                return;
            }

            PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(uuid, () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(uuid));
            ProxiedPlayer receiver = ProxyServer.getInstance().getPlayer(uuid);

            switch (args[0].toLowerCase()) {
                case "add" -> {
                    addCoins(playerProfile, coinsArg);

                    if (receiver != null) {
                        receiver.sendMessage(" ");
                        receiver.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(receiver,"You received §6{} Coins§7!", String.valueOf(coinsArg)));
                        receiver.sendMessage(" ");
                    }

                    sender.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(author,"You added §6{} Coins§7 to {}", String.valueOf(coinsArg), BungeeCore.getInstance().getPlayerColor(uuid) + args[1]));
                }
                case "set" -> {
                    setCoins(playerProfile, coinsArg);

                    if (receiver != null) {
                        receiver.sendMessage(" ");
                        receiver.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(receiver,"Your Coins have been set to §6{} Coins§7!", String.valueOf(coinsArg)));
                        receiver.sendMessage(" ");
                    }

                    sender.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(author,"You set {} Coins to §6{}", BungeeCore.getInstance().getPlayerColor(uuid) + args[1], String.valueOf(coinsArg)));
                }
                case "remove" -> {
                    removeCoins(playerProfile, coinsArg);

                    if (receiver != null) {
                        receiver.sendMessage(" ");
                        receiver.sendMessage(prefix + "§6 " + coinsArg + BungeeTranslateAPI.translate(receiver,"Coins §7have been removed from your account!"));
                        receiver.sendMessage(" ");
                    }

                    sender.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(author,"You removed §6{} Coins§7 from {}§7's account!", String.valueOf(coinsArg), BungeeCore.getInstance().getPlayerColor(uuid) + args[1]));
                }
            }

        }

    }


    private void sendCoins(CommandSender sender, String name) {

        UUID uuid = null;

        if (name != null && !name.isEmpty()) {
            uuid = BungeeCore.getAPI().getUuidManager().getUUID(name);
        } else if (sender instanceof ProxiedPlayer player) {
            uuid = player.getUniqueId();
        }

        if (uuid == null) {
            sender.sendMessage(prefix + "Player not found");
            return;
        }

        final UUID finalUuid = uuid;

        PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(finalUuid, () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(finalUuid));

        if (playerProfile == null) {
            sender.sendMessage(prefix + "Player not found");
            return;
        }

        sender.sendMessage(prefix + BungeeCore.getInstance().getPlayerColor(playerProfile.getPlayerId()) + playerProfile.getPlayerName() + " §7has §e" + playerProfile.getCoins() + " §6coins");
    }

    private void addCoins(PlayerProfile player, int coins) {
        player.setCoins(player.getCoins() + coins);
        BungeeCore.getAPI().getPlayerService().saveEntity(player, true, true);
        coreAPI.getCoinManager().sendMessage(player.getPlayerId(), player.getCoins());

    }

    private void setCoins(PlayerProfile player, int coins) {
        player.setCoins(coins);
        BungeeCore.getAPI().getPlayerService().saveEntity(player, true, true);
        coreAPI.getCoinManager().sendMessage(player.getPlayerId(), player.getCoins());
    }

    private void removeCoins(PlayerProfile player, int coins) {
        player.setCoins(player.getCoins() - coins);
        BungeeCore.getAPI().getPlayerService().saveEntity(player, true, true);
        coreAPI.getCoinManager().sendMessage(player.getPlayerId(), player.getCoins());
    }
}

// Was hat dir die console getan digga
