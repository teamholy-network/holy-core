package de.teamholy.core.bungee.commands.report;

import de.teamholy.core.api.manager.ReportManager;
import de.teamholy.core.api.utility.Punish;
import de.teamholy.core.api.utility.Report;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.model.ChatLog;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;

/* copyright by Yassino */
public class ReportCommand extends Command {

    private String prefix = "§cReport §8× §7";
    private ReportManager reportHandler = BungeeCore.getAPI().getReportManager();
    private String[] reportReasons = new String[]{"Hacking", "Autoclicker", "Bugusing", "Trolling", "Skin", "Boosting", "Name", "Spam", "Provocation", "Insult", "Advertising"};

    public ReportCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (args.length == 2) {

            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            boolean isNicked = false;

            if (target == null) {

                UUID nickUUID = BungeeCore.getAPI().getNickManager().getUUIDFromNick(args[0]);
                if (nickUUID == null) {
                    player.sendMessage(prefix + "This player is not online");
                    return;
                }
                isNicked = true;
                target = ProxyServer.getInstance().getPlayer(nickUUID);

            }

            String reason = args[1];
            boolean isValidReason = false;
            for (String reportReason : reportReasons) {
                if (reason.equalsIgnoreCase(reportReason)) {
                    isValidReason = true;
                    reason = reportReason;
                }
            }

            if (!isValidReason) {
                player.sendMessage(prefix + "The reason §e" + args[1] + "§7 is not a valid reason!");
                return;
            }

            if (target.getName().equals(player.getName())) {
                player.sendMessage(prefix + "You can't report yourself!");
                return;
            }


            if (reportHandler.isReported(target.getUniqueId())) {
                player.sendMessage(prefix + "The player is already reported!");
                return;
            }

            Report report = new Report();
            report.setReason(reason);
            report.setSender(player.getUniqueId());
            report.setTarget(target.getUniqueId());
            report.setTime(System.currentTimeMillis());
            player.sendMessage(prefix + "You reported the player for §e" + report.getReason());
            String[] chatlogReasons = new String[]{"Spam", "Provocation", "Insult", "Advertising"};


            ChatLog chatLog = null;
            for (String chatlogReason : chatlogReasons) {
                if (chatlogReason.equalsIgnoreCase(reason)) {
                    chatLog = BungeeCore.getInstance().getChatLogManager().createChatlog(Punish.getConsoleUuid(),target);
                }
            }
            report.setChatlogID(chatLog == null ? null : chatLog.getChatLogId());
            reportHandler.addReport(report);



            boolean finalIsNicked = isNicked;

            String reported = BungeeCore.getAPI().getCloudManager().getColor(target.getUniqueId()) + target.getName();
            String reporter = BungeeCore.getAPI().getCloudManager().getColor(player.getUniqueId()) + player.getName();

            ProxiedPlayer finalTarget = target;
            ChatLog finalChatLog = chatLog;
            ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> {

                if (!proxiedPlayer.hasPermission("teamholy.team")) return;
                if (!BungeeCore.getAPI().getStaffManager().canNotify(proxiedPlayer.getUniqueId())) return;

                proxiedPlayer.sendMessage(prefix + "The player " + reporter + " §7reported " + reported + " §7for §e" + report.getReason() + " §7reportet §8(§e" + finalTarget.getServer().getInfo().getName() + "§8) " + (finalIsNicked ? "§8(§5§lNICKED§8)" : ""));
                if (finalChatLog != null) proxiedPlayer.sendMessage(prefix + "Chatlog -> https://teamholy.de/chatlog/" + finalChatLog.getChatLogId());
                TextComponent message = new TextComponent(prefix + "§a§lAccept report");
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports accept " + finalTarget.getName()));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Accept the report of " + reported + " §7an")));
                proxiedPlayer.sendMessage(message);
            });

        } else {
            sendHelp(player);
        }
    }

    private void sendHelp(ProxiedPlayer proxiedPlayer) {


        StringBuilder stringBuilder = new StringBuilder();
        for (String reportReason : reportReasons) {
            stringBuilder.append(reportReason + "§7, §e");
        }

        proxiedPlayer.sendMessage(prefix + "Reasons §8» §e" + stringBuilder.toString());
        proxiedPlayer.sendMessage(prefix + "/report (name) (reason)");
    }

}
