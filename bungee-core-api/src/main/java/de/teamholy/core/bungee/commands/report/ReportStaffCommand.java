package de.teamholy.core.bungee.commands.report;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.api.manager.ReportManager;
import de.teamholy.core.api.utility.Report;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;
import java.util.UUID;

/* copyright by Yassino */
public class ReportStaffCommand extends Command {


    private String prefix = "§cReports §8× §7";
    private ReportManager reportHandler = BungeeCore.getAPI().getReportManager();

    public ReportStaffCommand(String name) {
        super(name, "teamholy.reports", "reports");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission("teamholy.reports"))
            return;

        if (args.length == 0) {
            sendHelp(player);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("finish")) {

                Report report = null;


                for (Report reportTemp : reportHandler.getAllReports().values()) {
                    if (reportTemp.getViewer() != null && reportTemp.getViewer().equals(player.getUniqueId()))
                        report = reportTemp;
                }


                if (report == null) {
                    player.sendMessage(prefix + "You dont edit any report!");
                    return;
                }


                player.sendMessage(prefix + "You finished the report of " + BungeeCore.getAPI().getCloudManager().getColor(report.getTarget()) + BungeeCore.getAPI().getUuidManager().getName(report.getTarget()) + "§7!");
                reportHandler.removeReport(report.getTarget());
                reportHandler.addReport(report);

            } else if (args[0].equalsIgnoreCase("auto")) {

                for (Report report : reportHandler.getAllReports().values()) {
                    if (report.getViewer() != null && report.getViewer().equals(player.getUniqueId())) {
                        player.sendMessage(prefix + "You already took over the report of " + BungeeCore.getAPI().getCloudManager().getColor(report.getTarget()) + BungeeCore.getAPI().getUuidManager().getName(report.getTarget()));
                        return;
                    } else if (report.getViewer() == null) {
                        player.chat("/reports chat " + BungeeCore.getAPI().getUuidManager().getName(report.getTarget()));
                    }
                }


                player.sendMessage(prefix + "§aThere is currently no report open");


            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("accept")) {
                String name = args[1];
                UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(name);
                if (uuid == null) {
                    player.sendMessage(prefix + "This player doesn't exist");
                    return;
                }

                if (!reportHandler.isReported(uuid)) {
                    player.sendMessage(prefix + "This player wasn't reported");
                    return;
                }

                if (ProxyServer.getInstance().getPlayer(uuid) == null) {
                    player.sendMessage(prefix + "This player isn't §aonline §7anymore");
                    return;
                }


                for (Report report : reportHandler.getAllReports().values()) {
                    if (report.getViewer() != null && report.getViewer().equals(player.getUniqueId())) {
                        player.sendMessage(prefix + "You already took over the report of " + BungeeCore.getAPI().getCloudManager().getColor(report.getTarget()) + BungeeCore.getAPI().getUuidManager().getName(report.getTarget()));
                        return;
                    }
                }

                Report report = reportHandler.getReport(uuid);

                if (report.getViewer() != null) {
                    player.sendMessage(prefix + "The report was already took over from " + BungeeCore.getAPI().getCloudManager().getColor(report.getViewer()) + BungeeCore.getAPI().getUuidManager().getName(report.getViewer()));
                    return;
                }

                report.setViewer(player.getUniqueId());
                player.sendMessage(prefix + "You took over the report of " + BungeeCore.getAPI().getCloudManager().getColor(uuid) + name);
                BungeeCore.getAPI().getCloudManager().sendCloudMessage("command", "command", JsonDocument.newDocument("uuid", player.getUniqueId()).append("command", "jump " + name));
                reportHandler.addReport(report);

            } else if (args[0].equalsIgnoreCase("remove")) {
                String name = args[1];
                if (!player.hasPermission("reports.remove")) {
                    player.sendMessage(prefix + "you dont have any permissions to remove a report");
                    return;
                }
                UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(name);
                if (uuid == null) {
                    player.sendMessage(prefix + "This player doesn't exist");
                    return;
                }


                if (!reportHandler.isReported(uuid)) {
                    player.sendMessage(prefix + "This player wasn't reported");
                    return;
                }

                reportHandler.removeReport(uuid);
                player.sendMessage(prefix + "Deleted report of " + BungeeCore.getAPI().getCloudManager().getColor(uuid) + name);
            }
        }

    }

    public void sendHelp(ProxiedPlayer player) {
        Map<UUID, Report> reportMap = reportHandler.getAllReports();
        player.sendMessage(prefix + "Active reports §8(§e" + reportMap.size() + "§8)");
        for (Report report : reportMap.values()) {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(report.getTarget());
            String name = BungeeCore.getAPI().getCloudManager().getColor(report.getTarget()) + BungeeCore.getAPI().getUuidManager().getName(report.getTarget());
            if (proxiedPlayer == null) {
                player.sendMessage(prefix + name + " §8- §e" + report.getReason() + " §8(§cOffline§8)");
            } else {
                String nick = BungeeCore.getAPI().getNickManager().getNickFromUUID(report.getTarget());
                TextComponent message = new TextComponent(prefix + name + " §8- §e" + report.getReason() + " §8(§e" + proxiedPlayer.getServer().getInfo().getName() + "§8) " + (nick != null ? "§8(§5§lNICKED §7- §e" + nick + "§8)" : ""));
                if (report.getViewer() == null) {
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports accept " + BungeeCore.getAPI().getUuidManager().getName(report.getTarget())));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Accept the report of " + name)));
                } else {
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(BungeeCore.getAPI().getCloudManager().getColor(report.getViewer()) + BungeeCore.getAPI().getUuidManager().getName(report.getViewer()) + " §7already took over the report")));
                }

                player.sendMessage(message);
            }
        }
        player.sendMessage(prefix + "");
        player.sendMessage(prefix + "/reportstaff accept (name)");
        player.sendMessage(prefix + "/reportstaff remove (name)");
        player.sendMessage(prefix + "/reportstaff finish");
        player.sendMessage(prefix + "/reportstaff auto");

    }

}
