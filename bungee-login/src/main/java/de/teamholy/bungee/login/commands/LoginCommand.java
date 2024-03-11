package de.teamholy.bungee.login.commands;

import java.util.Locale;

import de.teamholy.bungee.login.BungeeLogin;
import de.teamholy.bungee.login.api.TaskAPI;
import de.teamholy.bungee.login.manager.BotManager;
import de.teamholy.bungee.login.manager.CaptchaManager;
import de.teamholy.bungee.login.repositories.PlayerConnectRepository;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LoginCommand extends Command {


    public LoginCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) return;

        if (BungeeLogin.loggedin.contains(player)) return;

        if (CaptchaManager.getInstance().getCapcha(player).isPresent()) {
            player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + "§cYou have to Solve the Captcha first to Register your Account"));
            CaptchaManager.getInstance().getCapcha(player).ifPresent(captcha -> player
                .sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + captcha.link)));
            return;
        }

        if (args.length < 1) {
            player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + "§cWrong Password"));
            return;
        }

        TaskAPI.runAsync(() -> {
            PlayerConnectRepository repo = BungeeLogin.repo;

            String hashedpassword = repo.findFirstById(player.getName().toLowerCase(Locale.ROOT)).getPasswordhash();

            if (!hashedpassword.equals(BungeeLogin.hash(args[0]))) {
                player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + "§cWrong Password"));
                return;
            }

            BotManager.loggin();

            BungeeLogin.login(player);
        });
    }
}
