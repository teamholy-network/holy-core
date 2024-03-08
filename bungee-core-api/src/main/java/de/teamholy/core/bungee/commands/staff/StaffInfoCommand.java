package de.teamholy.core.bungee.commands.staff;

import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class StaffInfoCommand extends Command {

    private final String usage = "Usage: '/staffinfo ([player] [days])'";

    public StaffInfoCommand() {
        super("staffinfo");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("teamholy.team")) {
            BungeeUtil.sendNoPermission(sender);
            return;
        }

        if (args.length == 0) {
            executeSelf(sender, null);
            return;
        }

        if (args.length == 1) {
            if (args[0].equals("help")) {
                sender.sendMessage(Message.STAFF_INFO_PREFIX + usage);
                return;
            }

            try {
                int days = Integer.parseInt(args[0]);
                executeSelf(sender, days);
            } catch (NumberFormatException e) {
                if (!sender.hasPermission("teamholy.staffinfo.others")) {
                    executeSelf(sender, null);
                    return;
                }

                executeOther(sender, args[0], null);
            }

            return;
        }

        if (!sender.hasPermission("teamholy.staffinfo.others")) {
            executeSelf(sender, null);
            return;
        }

        try {
            int days = Integer.parseInt(args[1]);
            executeOther(sender, args[0], days);
        } catch (NumberFormatException e) {
            executeOther(sender, args[0], null);
        }
    }

    private void executeSelf(CommandSender sender, Integer days) {
        if (!(sender instanceof ProxiedPlayer player)) {
            sender.sendMessage(String.format("%s§7You must be a §cplayer §7to execute this command on yourself. %s",
                Message.STAFF_INFO_PREFIX, usage));
            return;
        }

        StaffProfile staffProfile = getStaffProfile(player.getUniqueId());

        if (staffProfile == null) {
            sender.sendMessage(Message.STAFF_INFO_PREFIX + "Your staff profile §cisn't existing§7.");
            return;
        }

        if (days == null) {
            player.sendMessage(formatStaffProfile(staffProfile, player.getName()));
            return;
        }

        player.sendMessage(formatTimeStaffProfile(staffProfile, player.getName(), days));
    }

    private void executeOther(CommandSender sender, String target, Integer days) {
        UUID uuid = BungeeUtil.parseTargetArgument(target);

        if (uuid == null) {
            sender.sendMessage(String.format("%sCan't find any player with the name §c%s§7.",
                Message.STAFF_INFO_PREFIX, target));
            return;
        }

        StaffProfile staffProfile = getStaffProfile(uuid);

        if (staffProfile == null) {
            sender.sendMessage(String.format("%sCan't find staff profile for the player §c%s§7.",
                Message.STAFF_INFO_PREFIX, target));
            return;
        }

        if (days == null) {
            sender.sendMessage(formatStaffProfile(staffProfile, target));
            return;
        }

        sender.sendMessage(formatTimeStaffProfile(staffProfile, target, days));
    }

    private StaffProfile getStaffProfile(UUID uuid) {
        return BungeeCore.getAPI().getStaffService().getEntity(
            uuid, () -> BungeeCore.getAPI()
                .getStaffService()
                .getRepository()
                .findFirstById(uuid)
        );
    }

    private String formatStaffProfile(StaffProfile profile, String username) {
        return String.format("""
                        %sHere are the staff stats of §2%s§7:
                        §7Banned players §8» §2%d
                        §7Muted players §8» §2%d
                        §7Finished reports §8» §2%d""",

            Message.STAFF_INFO_PREFIX,
            username,
            profile.getBanProfileList().size(),
            profile.getMuteProfileList().size(),
            profile.getReportList().size());
    }

    private String formatTimeStaffProfile(StaffProfile profile, String username, int days) {

        long timemillis = (long) days*24*60*60*1000;

        return String.format("""
                        %sHere are the staff stats of §2%s §7§o(last %dd)§r§8:
                        §7Banned players §8» §2%d
                        §7Muted players §8» §2%d
                        §7Finished reports §8» §2%d""",
            Message.STAFF_INFO_PREFIX, username, days,
            profile.getBanProfileList()
                .stream()
                .filter(banProfile -> banProfile.getCreateDate() <= (System.currentTimeMillis() - timemillis))
                .count(),
            profile.getMuteProfileList()
                .stream()
                .filter(muteProfile -> muteProfile.getCreateDate() <= (System.currentTimeMillis() - timemillis))
                .count(),
            profile.getReportList()
                .stream()
                .filter(report -> report.getTime() <= (System.currentTimeMillis() - timemillis))
                .count()
        );
    }
}
