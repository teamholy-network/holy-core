package de.teamholy.core.bukkit.report;

import com.google.common.collect.Maps;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.manager.ReportManager;
import de.teamholy.core.api.utility.Pagifier;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.api.utility.Report;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportBukkitManager implements CommandExecutor {

    private final ReportManager reportManager = BukkitCore.getAPI().getReportManager();

    private final String prefix = "§cReport §8× §7";

    private final Map<UUID, Integer> playerPage = Maps.newHashMap();
    private final HashMap<UUID, Integer> filterId = Maps.newHashMap();
    private final Map<UUID, Pagifier<Report>> playerPagifier = Maps.newHashMap();


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (!player.hasPermission("teamholy.team")) return false;
        if (reportManager.getAllReports().isEmpty()) {
            player.sendMessage(prefix + "§cThere are no open reports!");
            return true;
        }

        if (!playerPage.containsKey(player.getUniqueId())) {
            playerPage.put(player.getUniqueId(), 1);
        }

        if (!filterId.containsKey(player.getUniqueId()))
            filterId.put(player.getUniqueId(), 0);

        openReportsInventory(player, playerPage.get(player.getUniqueId()));
        return false;
    }

    private void openReportsInventory(Player player, int currentPage) {
        Inventory inventory = new Inventory("§8» §cReports", 9 * 4);

        for (int i = inventory.getInventory().getSize() - 9; i < inventory.getInventory().getSize(); i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName("§8//").build(), i);
        }

        if (!playerPagifier.containsKey(player.getUniqueId()))
            playerPagifier.put(player.getUniqueId(), new Pagifier<>(27));

        Pagifier<Report> playerReports = playerPagifier.remove(player.getUniqueId());

        reportManager.getAllReports().forEach((uuid, report) -> {
            if (!playerReports.containsItem(report)) {
                playerReports.addItem(report);
            }
        });

        if (playerReports.getPage(currentPage) == null) {
            player.sendMessage(prefix + "§cThere are no more reports!");
            playerPage.remove(player.getUniqueId());
            return;
        }

        List<Report> copiedList = new ArrayList<>(playerReports.getPage(currentPage));
        copiedList.forEach(value -> {
            if (!reportManager.getAllReports().containsValue(value)) {
                playerReports.getPage(currentPage).remove(value);
            }
        });
        copiedList = new ArrayList<>(playerReports.getPage(currentPage));

        int filterId = this.filterId.getOrDefault(player.getUniqueId(), 0);
        copiedList.sort(getFilterById(filterId));

        // copiedList.forEach(report -> Bukkit.broadcastMessage(String.valueOf(report.isTargetOnline())));

        playerPagifier.put(player.getUniqueId(), playerReports);

        for (var item : copiedList) {
            addReportToInventory(player, inventory, item, false);
        }

        inventory.setItem(new
            ItemBuilder(Material.FISHING_ROD)
            .setName("§8» §6Auto §creport")
            .setLore("", " §7Views the report of a random", " §7player like §6/reports auto", "")
            .build(), 29, event ->
            sendBungeeCommand(player, "reports auto"));

        inventory.setItem(new
            ItemBuilder(Material.LAVA_BUCKET)
            .setName("§8» §6Clear §creports")
            .setLore("", " §7Clears all reports", " §7like §6/reports clear", "")
            .build(), 30, event ->
            sendBungeeCommand(player, "reports clear"));

        inventory.setItem(new
            ItemBuilder(Material.HOPPER)
            .setName("§8» §6Filter §creports")
            .setLore("",
                " §7Filter the §creports",
                "",
                " §7Current§8: §a" + getFilterByIdName(filterId))
            .build(), 32, event -> {
            int currentFilter = this.filterId.remove(player.getUniqueId());
            if (currentFilter == 4) {
                this.filterId.put(player.getUniqueId(), 0);
            } else {
                this.filterId.put(player.getUniqueId(), currentFilter + 1);
            }
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(BukkitCore.getInstance(), () -> {
                player.performCommand("reportsgui");
            }, 3L);
        });

        inventory.setItem(new
            ItemBuilder(Material.PAPER)
            .setName("§8» §6List §creports")
            .setLore("", " §7Lists your current §creports", "")
            .build(), 33, event ->
            openCurrentReports(player));


        if (currentPage > 1) {
            inventory.setItem(new ItemBuilder(Material.ARROW).setName("§8» §cBack").build(), inventory.getInventory().getSize() - 9, event -> {
                playerPage.put(player.getUniqueId(), playerPage.remove(player.getUniqueId()) - 1);
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(BukkitCore.getInstance(), () -> {
                    player.performCommand("reportsgui");
                }, 3L);
            });
        }

        boolean forwardPage = playerReports.getPage(currentPage + 1) != null;
        if (forwardPage) {
            inventory.setItem(new ItemBuilder(Material.ARROW).setName("§8» §bForward").build(),
                inventory.getInventory().getSize() - 1, event -> {

                    int playerCurrent = playerPage.remove(player.getUniqueId());
                    playerPage.put(player.getUniqueId(), playerCurrent + 1);
                    player.closeInventory();
                    Bukkit.getScheduler().runTaskLater(BukkitCore.getInstance(), () -> {
                        player.performCommand("reportsgui");
                    }, 3L);
                });
        }

        player.openInventory(inventory.getInventory());
    }

    private String getFilterByIdName(int id) {
        return (id == 0 ? "§aOnline" : id == 1 ? "§cOffline" : id == 2 ? "§eTime" : "§6All");
    }

    private Comparator<Report> getFilterById(int id) {
        return (id == 0 ? Comparator.comparing(Report::isTargetOnline)
            : id == 1 ? Comparator.comparing(Report::isTargetOnline, Comparator.reverseOrder())
            : id == 2 ? Comparator.comparingLong(Report::getTime)
            : Comparator.comparing(Report::getTime, Comparator.naturalOrder()));
    }

    private void openPlayerReport(Player player, Report report, boolean ownReport) {
        PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService()
            .getEntity(report.getTarget(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(report.getTarget()));
        if (playerProfile == null) return;

        Inventory inventory = new Inventory("§8» " + prefix + PlayerRank.valueOf(playerProfile.getRank()).getColorCode() + playerProfile.getPlayerName(), 9 * 3);
        for (int i = 0; i < inventory.getInventory().getSize(); i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName("§8//").build(), i);
        }

        ItemBuilder teleport = new ItemBuilder(playerProfile.isOnline() ? Material.ENDER_PEARL : Material.BARRIER)
            .setName((playerProfile.isOnline() ? "§8» §6Teleport" : "§cOffline"));

        ItemBuilder close = new ItemBuilder(Material.INK_SACK, 1, (byte) 1).setName("§8» §cClose Report");

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

        inventory.setItem(close.build(), 15, event -> {
            if (!playerProfile.isOnline()) {
                sendBungeeCommand(player, "reports remove " + playerProfile.getPlayerName());
            } else {
                sendBungeeCommand(player, "reports finish");
            }
            player.closeInventory();
            Bukkit.getScheduler().runTaskLater(BukkitCore.getInstance(), () -> {
                if (ownReport) {
                    if (!openCurrentReports(player)) {
                        player.performCommand("reportsgui");
                    }
                } else player.performCommand("reportsgui");
            }, 3L);
        });
        inventory.setItem(new ItemBuilder(Material.ARROW).setName("§8» §cBack").build(), 18, event -> {
            player.closeInventory();
            if (ownReport) {
                if (!openCurrentReports(player)) {
                    player.performCommand("reportsgui");
                }
            } else player.performCommand("reportsgui");
        });
        player.openInventory(inventory.getInventory());
    }


    private boolean openCurrentReports(Player player) {
        if (reportManager.getAllReports().isEmpty()) {
            player.sendMessage(prefix + "§cThere are no open reports!");
            return false;
        }

        final Pagifier<Report> ownReports = new Pagifier<>(27);

        reportManager.getAllReports().forEach((uuid, report) -> {
            if (report.getViewer() != null && report.getViewer().equals(player.getUniqueId()))
                if (ownReports.containsItem(report)) ownReports.addItem(report);
        });

        Inventory inventory = new Inventory("§8» " + prefix + "§6Your Reports", 9 * 4);
        for (int i = inventory.getInventory().getSize() - 9; i < inventory.getInventory().getSize(); i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName("§8//").build(), i);
        }
        inventory.setItem(new ItemBuilder(Material.ARROW).setName("§8» §cBack").build(), inventory.getInventory().getSize() - 9, event -> {
            player.closeInventory();
            player.performCommand("reportsgui");
        });

        if (ownReports.getPage(0) == null) return false;

        for (var item : ownReports.getPage(0)) {
            addReportToInventory(player, inventory, item, true);
        }

        player.openInventory(inventory.getInventory());
        return true;
    }

    private void addReportToInventory(Player player, Inventory inventory, Report report, boolean ownReport) {
        PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService()
            .getEntity(report.getTarget(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(report.getTarget()));
        if (playerProfile == null) return;

        ItemBuilder itemBuilder;

        if (!playerProfile.isOnline()) {
            itemBuilder = new ItemBuilder(Material.INK_SACK, 1, 1)
                .setName("§8» " + PlayerRank.valueOf(playerProfile.getRank()).getColorCode() + playerProfile.getPlayerName())
                .setLore("§cOffline");
        } else {
            String nick = BukkitCore.getAPI().getNickManager().getNickFromUUID(report.getTarget());

            itemBuilder = new ItemBuilder(Material.INK_SACK, 1, report.getViewer() == null ? 10 : 14)
                .setName("§8» " + PlayerRank.valueOf(playerProfile.getRank()).getColorCode() + playerProfile.getPlayerName() + " " + (nick != null ? "§8(§5§lNICKED §7- §e" + nick + "§8)" : ""))
                .setLore(" ",
                    "§8┃ §7State §8» §a" + (report.getViewer() == null ? "§aOpen" : "§6In Progress"),
                    "§8┃ §7Date §8» §e" + convertTime(report.getTime()),
                    "§8┃ §7Reason §8» §e" + report.getReason());
        }
        if (inventory.getInventory().contains(itemBuilder.build())) {
            inventory.getInventory().remove(itemBuilder.build());
        }
        inventory.setItem(itemBuilder.build(), inventory.getInventory().firstEmpty(), event -> openPlayerReport(player, report, ownReport));
    }

    private void sendBungeeCommand(Player player, String command) {
        BukkitCore.getAPI().getCloudManager().sendCloudMessage("command", "command", JsonDocument.newDocument("uuid", player.getUniqueId()).append("command", command));
    }

    public static String convertTime(long timestampInMillis) {
        //Date date = new Date(timestampInMillis);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        ZonedDateTime zonedDateTime = ZonedDateTime
            .ofInstant(Instant.ofEpochMilli(timestampInMillis), ZoneId.systemDefault());
        return zonedDateTime.format(dateTimeFormatter);
    }
}
