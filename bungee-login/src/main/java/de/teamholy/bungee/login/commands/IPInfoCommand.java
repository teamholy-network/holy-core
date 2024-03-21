package de.teamholy.bungee.login.commands;

import de.teamholy.bungee.login.api.TaskAPI;
import de.teamholy.bungee.login.filter.IPChecker;
import de.teamholy.bungee.login.filter.IPCheckerResult;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.regex.Pattern;

public class IPInfoCommand extends Command {

    public IPInfoCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (!player.hasPermission("holylogin.ipinfo")) {
                player.sendMessage(TextComponent.fromLegacyText("§cKeine Rechte"));
                return;
            }
            if (args.length < 1) {
                player.sendMessage(TextComponent.fromLegacyText("§c/ipinfo <ip>"));
                return;
            }

            String ip = args[0];

            if (!isValidInet4Address(ip)) {
                player.sendMessage(TextComponent.fromLegacyText("§cIP Invalid!"));
                return;
            }

            TaskAPI.runAsync(() -> {
                IPCheckerResult result = IPChecker.getInstance().getIPInfo(ip);

                sender.sendMessage(TextComponent.fromLegacyText("§f§lTeam§6§lHoly" + " §7- §8[§fIPINFO§8]"));
                if (result == null || !IPChecker.getInstance().isServiceonline()) {
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7IP: §e" + ip));
                    return;
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7IP: §e" + ip));
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Country: §e" + result.getCountry()));
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7CountryCode: §e" + result.getCountryCode()));
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7City: §e" + result.getCity()));
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7ASN: §e" + result.getASN()));
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Company: §e" + result.getCompany()));
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Hosting: §e" + result.isHosting()));
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7VPN: §e" + result.isVPN()));
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Proxy: §e" + result.isProxy()));
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7TOR: §e" + result.isTOR()));
                    sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Residental: §e" + result.isResidental()));
                }
            });
        }
        sender.sendMessage(TextComponent.fromLegacyText("§f§lTeam§6§lHoly" + " §7- §8[§fIPINFO§8]"));
    }


    private static final String IPV4_REGEX = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$";

    private static final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);

    public static boolean isValidInet4Address(String ip) {
        if (ip == null) {
            return false;
        }

        if (!IPv4_PATTERN.matcher(ip).matches()) {
            return false;
        }

        String[] parts = ip.split("\\.");

        // verify that each of the four subgroups of IPv4 addresses is legal
        try {
            for (String segment : parts) {
                // x.0.x.x is accepted but x.01.x.x is not
                if (Integer.parseInt(segment) > 255 || (segment.length() > 1 && segment.startsWith("0"))) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
