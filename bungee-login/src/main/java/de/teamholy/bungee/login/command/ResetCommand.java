package de.teamholy.bungee.login.command;

import java.util.Locale;

import de.teamholy.bungee.login.BungeeLogin;
import de.teamholy.bungee.login.api.TaskAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ResetCommand extends Command {

	public ResetCommand(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if (!player.hasPermission("holylogin.reset")) {
				player.sendMessage(TextComponent.fromLegacyText("§cKeine Rechte"));
				return;
			}
			if (args.length < 1) {
				player.sendMessage(TextComponent.fromLegacyText("§c/reset <player>"));
				return;
			}
			
			TaskAPI.runAsync(() -> {
				if (BungeeLogin.repo.deleteById(args[0].toLowerCase(Locale.ROOT))) {
					player.sendMessage(TextComponent.fromLegacyText("§cDie Daten von " + args[0] + " wurden gelöscht"));
				} else {
					player.sendMessage(TextComponent.fromLegacyText("§cDer User " + args[0] + " wurde nicht gefunden"));
				}
			});
		}
	}

}
