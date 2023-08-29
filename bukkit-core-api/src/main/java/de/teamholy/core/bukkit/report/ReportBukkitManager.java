package de.teamholy.core.bukkit.report;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.ext.bridge.player.CloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.manager.ReportManager;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.api.utility.Report;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.stream.IntStream;

/* copyright by Yassino */
public class ReportBukkitManager implements CommandExecutor {

    private final ReportManager reportManager = BukkitCore.getAPI().getReportManager();


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        if (!player.hasPermission("teamholy.team")) return false;

        Inventory inventory = new Inventory("§8» §cReports", 9 * 4);

        for (int i = inventory.getInventory().getSize() - 9; i < inventory.getInventory().getSize(); i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName("§8//").build(), i);
        }

        reportManager.getAllReports().forEach((uuid, report) -> {
            PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService()
                .getEntity(report.getTarget(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(report.getTarget()));
            if (playerProfile == null) return;

            ItemBuilder itemBuilder;

            if (!playerProfile.isOnline()) {
                itemBuilder = new ItemBuilder(Material.INK_SACK, 1)
                    .setName("§8» " + PlayerRank.valueOf(playerProfile.getRank()).getColorCode() + playerProfile.getPlayerName())
                    .setLore("§cOffline");
            } else {
                String nick = BukkitCore.getAPI().getNickManager().getNickFromUUID(report.getTarget());

                itemBuilder = new ItemBuilder(Material.INK_SACK, 1, report.getViewer() == null ? 10 : 14)
                    .setName("§8» " + PlayerRank.valueOf(playerProfile.getRank()).getColorCode() + playerProfile.getPlayerName() + " " + (nick != null ? "§8(§5§lNICKED §7- §e" + nick + "§8)" : ""))
                    .setLore(" ", "§8┃ §7State §8» §a" + (report.getViewer() == null ? "§aOpen" : "§6In Progress"),
                        "§8┃ §7Date §8» §e" + convertTime(report.getTime()),
                        "§8┃ §7Reason §8» §e" + report.getReason());
            }
            if (inventory.getInventory().contains(itemBuilder.build())) return;
            inventory.setItem(itemBuilder.build(), inventory.getInventory().firstEmpty(), event -> openPlayerReport(player, report));

        });

        inventory.setItem(new
            ItemBuilder(Material.FISHING_ROD)
            .setName("§8» §6Auto §creport")
            .setLore("", " §7Views the report of a random", " §7player like §6/reports auto", "")
            .build(), 30, event ->
        {
            sendBungeeCommand(player, "reports auto");
        });

        inventory.setItem(new
            ItemBuilder(Material.BARRIER)
            .setName("§8» §6Clear §creports")
            .setLore("", " §7Clears all reports", " §7like §6/reports clear", "")
            .build(), 32, event ->
        {
            sendBungeeCommand(player, "reports clear");
        });

        player.openInventory(inventory.getInventory());
        return false;
    }

    private void openPlayerReport(Player player, Report report) {
        PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService()
            .getEntity(report.getTarget(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(report.getTarget()));
        if (playerProfile == null) return;
        Inventory inventory = new Inventory("§8» §cReport §8× §7" + PlayerRank.valueOf(playerProfile.getRank()).getColorCode() + playerProfile.getPlayerName(), 9 * 3);

        ItemBuilder teleport = new ItemBuilder(playerProfile.isOnline() ? Material.ENDER_PEARL : Material.BARRIER)
            .setName((playerProfile.isOnline() ? "§8» §6Teleport" : "§cOffline"));

        ItemBuilder close = new ItemBuilder(Material.INK_SACK, 1, (byte) 1).setName("§8» §cClose Report");

        for (int i = 0; i < inventory.getInventory().getSize(); i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName("§8//").build(), i);
        }
        inventory.setItem(teleport.build(), 11, event -> {
            if (playerProfile.isOnline()) {
                sendBungeeCommand(player, "reports accept " + playerProfile.getPlayerName());
            }
        });

        PlayerProfile reportSender = BukkitCore.getAPI().getPlayerService()
            .getEntity(report.getSender(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(report.getSender()));

        inventory.setItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
            .setSkullOwner(playerProfile.getPlayerName())
            .setName(PlayerRank.valueOf(playerProfile.getRank()).getColorCode() + playerProfile.getPlayerName())
            .setLore(" ",
                "§8┃ §7State §8» §a" + (report.getViewer() == null ? "§aOpen" : "§6In Progress"),
                "§8┃ §7Date §8» §e" + convertTime(report.getTime()),
                " ",
                "§8┃ §7Sender §8» §a" + (reportSender == null ? "§cUnknown" : PlayerRank.valueOf(reportSender.getRank()).getColorCode() + reportSender.getPlayerName()),
                "§8┃ §7Target §8» §c" + PlayerRank.valueOf(playerProfile.getRank()).getColorCode() + playerProfile.getPlayerName(),
                "§8┃ §7Server §8» §f" + playerProfile.getServerName(),
                "§8┃ §7Reason §8» §c" + report.getReason())
            .build(), 13);

        inventory.setItem(close.build(), 15, event -> sendBungeeCommand(player, "reports finish"));
        inventory.setItem(new ItemBuilder(Material.ARROW).setName("§8» §cBack").build(), 18, event -> {
            player.closeInventory();
            player.chat("/reportsgui");
        });
        player.openInventory(inventory.getInventory());
    }

    private void sendBungeeCommand(Player player, String command) {
        BukkitCore.getAPI().getCloudManager().sendCloudMessage("command", "command", JsonDocument.newDocument("uuid", player.getUniqueId()).append("command", command));
    }

    public String getCurrentServer(UUID player) {
        ICloudPlayer cloudPlayer = BukkitCore.getAPI().getCloudManager().getPlayerManager().getOnlinePlayer(player);
        if (cloudPlayer == null) return "§cUnknown";
        return cloudPlayer.getConnectedService().getServerName();
    }

    public static String convertTime(long timestampInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("d. MMM yyyy HH:mm:ss");
        Date date = new Date(timestampInMillis);
        return sdf.format(date);
    }
}
