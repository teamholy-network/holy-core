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

    public StaffInfoCommand() {
        super("staffinfo");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("teamholy.team")) {
            BungeeUtil.sendNoPermission(sender);
        }

        if (!sender.hasPermission("teamholy.staffinfo.others")) {
            executeSelf(sender);
            return;
        }

        if (args.length == 0) {
            executeSelf(sender);
            return;
        }

        executeOther(sender, args[0]);
    }

    private void executeSelf(CommandSender sender) {
        if (!(sender instanceof ProxiedPlayer player)) {
            String usage = "Try: '/staffinfo ([player])'";
            sender.sendMessage(String.format("%s§7You must be a §cplayer §7to execute this command on yourself. %s",
                Message.STAFF_INFO_PREFIX, usage));
            return;
        }

        StaffProfile staffProfile = getStaffProfile(player.getUniqueId());

        if (staffProfile == null) {
            sender.sendMessage(Message.STAFF_INFO_PREFIX + "Your staff profile §cisn't existing§7.");
            return;
        }

        player.sendMessage(formatStaffProfile(staffProfile, player.getName()));
    }

    private void executeOther(CommandSender sender, String target) {
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

        sender.sendMessage(formatStaffProfile(staffProfile, target));
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
                        §7Banned players: §2%d
                        §7Muted players: §2%d
                        §7Finished reports: §2%d""",

            Message.STAFF_INFO_PREFIX,
            username,
            profile.getBanProfileList().size(),
            profile.getMuteProfileList().size(),
            profile.getReportList().size());
    }
}
