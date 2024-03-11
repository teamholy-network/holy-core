package de.teamholy.bungee.login.commands;

import java.util.Locale;

import de.teamholy.bungee.login.BungeeLogin;
import de.teamholy.bungee.login.api.TaskAPI;
import de.teamholy.bungee.login.model.PlayerObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PremiumOnCommand extends Command {

    public PremiumOnCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) return;

        if (!player.hasPermission("holylogin.premiumon")) {
            player.sendMessage(TextComponent.fromLegacyText("§cKeine Rechte"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(TextComponent.fromLegacyText("§c/setpremium <player> <yes/no>"));
            return;
        }

        TaskAPI.runAsync(() -> {
            String target = args[0].toLowerCase(Locale.ROOT);
            if (BungeeLogin.repo.existsById(target)) {
                PlayerObject object = BungeeLogin.repo.findFirstById(target);
                object.setPremium(args[1].equalsIgnoreCase("yes"));
                BungeeLogin.repo.save(object);
                player.sendMessage(TextComponent.fromLegacyText("§cDer Account von " + args[0] + " wurden auf Premium " + object.isPremium() + " gesetzt"));

                return;
            }
            player.sendMessage(TextComponent.fromLegacyText("§cDer Account " + args[0] + " wurde noch nicht registriert"));
        });
    }

}
