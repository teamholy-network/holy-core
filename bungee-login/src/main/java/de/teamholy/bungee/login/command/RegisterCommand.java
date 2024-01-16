package de.teamholy.bungee.login.command;

import java.util.Locale;

import de.teamholy.bungee.login.BungeeLogin;
import de.teamholy.bungee.login.api.TaskAPI;
import de.teamholy.bungee.login.manager.BotManager;
import de.teamholy.bungee.login.manager.CaptchaManager;
import de.teamholy.bungee.login.model.PlayerObject;
import de.teamholy.bungee.login.repositories.PlayerConnectRepository;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RegisterCommand extends Command {


    public RegisterCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (BungeeLogin.loggedin.contains(player)) {
                player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + "§cAlready Logged in"));
            	return;
            }

            if (player.getPing() > 500) {
                player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + "§cRetry"));
            	return;
            }
            
            if (CaptchaManager.getInstance().getCapcha(player).isPresent()) {
                player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + "§cYou have to Solve the Captcha first to Register your Account"));
                CaptchaManager.getInstance().getCapcha(player).ifPresent(captcha -> {
                    player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + captcha.link));
                });
            	return;
            }

			TaskAPI.runAsync(() -> {
	            PlayerConnectRepository repo = BungeeLogin.repo;
	            PlayerObject playerobj = repo.findFirstById(player.getName().toLowerCase(Locale.ROOT));
	
	            if (playerobj.getPasswordhash() != null) {
	                player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + "§cThis account is already registered"));
	                return;
	            }
	
	            if (args.length < 2) {
	                player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + "§cWrong password"));
	                return;
	            }
	
	            if (!args[0].equals(args[1])) {
	                player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + "§cPasswords don't match"));
	                return;
	            }
	
	            BotManager.register();
	            playerobj.setPasswordhash(BungeeLogin.hash(args[0]));
	            repo.save(playerobj);
	            player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + "§aYou are now Registered"));
	            TextComponent text = new TextComponent(BungeeLogin.PREFIX + "§6" + args[0] + " §8[§7Copy§8]");
	            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Copy")));
	            text.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, args[0]));
	            player.sendMessage(text);

                BungeeLogin.login(player);
			});
        }
    }

}
