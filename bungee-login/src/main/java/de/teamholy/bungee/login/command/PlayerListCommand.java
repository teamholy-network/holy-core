package de.teamholy.bungee.login.command;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import de.teamholy.bungee.login.BungeeLogin;
import de.teamholy.bungee.login.api.TaskAPI;
import de.teamholy.bungee.login.model.PlayerObject;
import de.teamholy.bungee.login.util.UUIDUtility;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PlayerListCommand extends Command {

	public PlayerListCommand(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if (!player.hasPermission("holylogin.playerlist")) {
				player.sendMessage(TextComponent.fromLegacyText("§cKeine Rechte"));
				return;
			}
			TaskAPI.runAsync(() -> {
				player.sendMessage(TextComponent.fromLegacyText("§8§m-------§f§l Team§6§lHoly§8 §m-------"));
				player.sendMessage(TextComponent.fromLegacyText("§8 "));
				AtomicInteger cracked = new AtomicInteger();
				AtomicInteger premium = new AtomicInteger();
				AtomicInteger bedrock = new AtomicInteger();
				ProxyServer.getInstance().getPlayers().forEach(all -> {
					Version ver = getVersion(all);
					if (ver == Version.Premium) {
						premium.incrementAndGet();
					} else if (ver == Version.Cracked) {
						cracked.incrementAndGet();
					} else if (ver == Version.Bedrock) {
						bedrock.incrementAndGet();
					}
					if (all.getServer() != null) {
						PlayerObject playerobj = BungeeLogin.repo.findFirstById(all.getName().toLowerCase(Locale.ROOT));
						player.sendMessage(TextComponent.fromLegacyText("§f§l" + all.getName() + " §8» " + ver.format + "  §8» §a" + all.getServer().getInfo().getName() + "  §8» §e" + playerobj.getHostname()));
					}
				});
				player.sendMessage(TextComponent.fromLegacyText("§8 "));
				player.sendMessage(TextComponent.fromLegacyText("§6§lPremium §8» §e" + premium.intValue()
						+ " §7| §c§lCracked §8» §e" + cracked.intValue() + " §7| §d§lBedrock §8» §e" + bedrock.intValue()));
				player.sendMessage(TextComponent.fromLegacyText("§8 "));
				player.sendMessage(TextComponent.fromLegacyText("§8§m-------§f§l Team§6§lHoly§8 §m-------"));
			});
		}
	}

	public static Version getVersion(ProxiedPlayer player) {
		return getVersion(player.getUniqueId(), player.getName());
	}

	public static Version getVersion(UUID uniqueId, String name) {
		UUIDUtility.UUIDType uuid = UUIDUtility.getUUIDType(uniqueId, name);
		switch (uuid) {
		case PREMIUM:
			return Version.Premium;
		case CRACKED:
			return Version.Cracked;
		case BEDROCK:
			return Version.Bedrock;
		default:
			break;
		}
		return Version.Premium;
	}

	public static enum Version {
		Premium("§6Premium"), Cracked("§cCracked"), Bedrock("§dBedrock");

		String format;

		Version(String string) {
			this.format = string;
		}
	}
}
