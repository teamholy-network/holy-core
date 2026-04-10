package de.teamholy.core.bungee.commands.staff;

import de.teamholy.core.translation.BungeeTranslateAPI;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class StaffInfoCommand extends Command {

    public StaffInfoCommand() {
        super("staffinfo");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        UUID author = BungeeUtil.parseAuthorUUID(sender);
        String usage = BungeeTranslateAPI.translate(author, "Usage") + ": '/staffinfo ([" + BungeeTranslateAPI.translate(author, "player") + "] [" + BungeeTranslateAPI.translate(author, "days") + "])'";
        if (!sender.hasPermission("teamholy.team")) {
            BungeeUtil.sendNoPermission(sender);
            return;
        }

        if (args.length == 0) {
            executeSelf(sender, null, usage, author);
            return;
        }

        if (args.length == 1) {
            if (args[0].equals("help")) {
                sender.sendMessage(Message.STAFF_INFO_PREFIX + usage);
                return;
            }

            try {
                int days = Integer.parseInt(args[0]);
                executeSelf(sender, days, usage, author);
            } catch (NumberFormatException e) {
                if (!sender.hasPermission("teamholy.staffinfo.others")) {
                    executeSelf(sender, null, usage, author);
                    return;
                }

                executeOther(sender, args[0], null, author);
            }

            return;
        }

        if (!sender.hasPermission("teamholy.staffinfo.others")) {
            executeSelf(sender, null, usage, author);
            return;
        }

        try {
            int days = Integer.parseInt(args[1]);
            executeOther(sender, args[0], days, author);
        } catch (NumberFormatException e) {
            executeOther(sender, args[0], null, author);
        }
    }

    private void executeSelf(CommandSender sender, Integer days, String usage, UUID author) {
        if (!(sender instanceof ProxiedPlayer player)) {
            sender.sendMessage(String.format("%s§7" + BungeeTranslateAPI.translate(author, "You must be a §cplayer §7to execute this command on yourself.") + " %s",
                Message.STAFF_INFO_PREFIX, usage));
            return;
        }

        StaffProfile staffProfile = getStaffProfile(player.getUniqueId());

        if (staffProfile == null) {
            sender.sendMessage(Message.STAFF_INFO_PREFIX + BungeeTranslateAPI.translate(author, "Your staff profile §cisn't existing§7."));
            return;
        }

        if (days == null) {
            player.sendMessage(formatStaffProfile(staffProfile, player.getName(), author));
            return;
        }

        player.sendMessage(formatTimeStaffProfile(staffProfile, player.getName(), days, author));
    }

    private void executeOther(CommandSender sender, String target, Integer days, UUID author) {
        UUID uuid = BungeeUtil.parseTargetArgument(target);

        if (uuid == null) {
            sender.sendMessage(String.format("%s" + BungeeTranslateAPI.translate(author, "Can't find any player with the name") + " §c%s§7.",
                Message.STAFF_INFO_PREFIX, target));
            return;
        }

        StaffProfile staffProfile = getStaffProfile(uuid);

        if (staffProfile == null) {
            sender.sendMessage(String.format("%s" + BungeeTranslateAPI.translate(author, "Can't find staff profile for the player") + " §c%s§7.",
                Message.STAFF_INFO_PREFIX, target));
            return;
        }

        if (days == null) {
            sender.sendMessage(formatStaffProfile(staffProfile, target, author));
            return;
        }

        sender.sendMessage(formatTimeStaffProfile(staffProfile, target, days, author));
    }

    private StaffProfile getStaffProfile(UUID uuid) {
        return BungeeCore.getAPI().getStaffService().getEntity(
            uuid, () -> BungeeCore.getAPI()
                .getStaffService()
                .getRepository()
                .findFirstById(uuid)
        );
    }

    private String formatStaffProfile(StaffProfile profile, String username, UUID author) {
        StringBuilder builder = new StringBuilder();
        builder.append(BungeeTranslateAPI.translatePlaceholder(author, "{}Here are the staff stats of §2{}§7:", Message.STAFF_INFO_PREFIX, username));
        builder.append("\n" + BungeeTranslateAPI.translatePlaceholder(author, "§7Banned players §8» §2{}", String.valueOf(profile.getBanProfileList().size())));
        builder.append("\n" + BungeeTranslateAPI.translatePlaceholder(author, "§7Muted players §8» §2{}", String.valueOf(profile.getMuteProfileList().size())));
        builder.append("\n" + BungeeTranslateAPI.translatePlaceholder(author, "§7Finished reports §8» §2{}", String.valueOf(profile.getReportList().size())));

        return builder.toString();
    }

    private String formatTimeStaffProfile(StaffProfile profile, String username, int days, UUID author) {

        long timemillis = (long) days * 24 * 60 * 60 * 1000;

        StringBuilder builder = new StringBuilder();
        builder.append(BungeeTranslateAPI.translatePlaceholder(author, "{}Here are the staff stats of §2{} §7§o(last {}d)§r§8", Message.STAFF_INFO_PREFIX, username, String.valueOf(days)));
        builder.append("\n" + BungeeTranslateAPI.translatePlaceholder(author, "§7Banned players §8» §2{}", String.valueOf(profile.getBanProfileList()
            .stream()
            .filter(banProfile -> banProfile.getCreateDate() >= (System.currentTimeMillis() - timemillis))
            .count())));
        builder.append("\n" + BungeeTranslateAPI.translatePlaceholder(author, "§7Muted players §8» §2{}", String.valueOf(profile.getMuteProfileList()
            .stream()
            .filter(muteProfile -> muteProfile.getCreateDate() >= (System.currentTimeMillis() - timemillis))
            .count())));
        builder.append("\n" + BungeeTranslateAPI.translatePlaceholder(author, "§7Finished reports §8» §2{}", String.valueOf(profile.getReportList()
            .stream()
            .filter(report -> report.getTime() >= (System.currentTimeMillis() - timemillis))
            .count())));


        return builder.toString();
    }
}
