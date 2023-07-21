package de.teamholy.core.bukkit.report;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.manager.ReportManager;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

/* copyright by Yassino */
public class ReportBukkitManager implements CommandExecutor {

    private final ReportManager reportManager = BukkitCore.getAPI().getReportManager();


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        if (player.hasPermission("teamholy.team")) return false;

        Inventory inventory = new Inventory("§8» §6Reports",9*4);

        for (int i = inventory.getInventory().getSize() - 9; i < inventory.getInventory().getSize(); i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName("§8//").build(), i);
        }


        reportManager.getAllReports().forEach((uuid, report) -> BukkitCore.getAPI().getPlayerService().getEntityAsync(player.getUniqueId(),
                () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()),playerProfile -> {

            //14 o 10 g 1 r

            ItemBuilder itemBuilder;

            if (!playerProfile.isOnline()) {
                itemBuilder = new ItemBuilder(Material.INK_SACK,1).setName("§8» " + PlayerRank.valueOf(playerProfile.getRank()).getColorCode() + playerProfile.getPlayerName());
            }





        }));

        inventory.setItem(new ItemBuilder(Material.ENDER_PEARL).setName("§8» §6Auto §creport")
                .setLore(""," §7Views the report of a random"," §7player like §6/reports auto","").build(),29,event -> {
            sendBungeeCommand(player,"reports auto");
        });

        inventory.setItem(new ItemBuilder(Material.ENDER_PEARL).setName("§8» §6Clear §creports")
                .setLore(""," §7clears all reports"," §7like §6/reports clear","").build(),32,event -> {
            sendBungeeCommand(player,"reports clear");
        });

        player.openInventory(inventory.getInventory());
        return false;
    }

    private void sendBungeeCommand(Player player, String command) {
        BukkitCore.getAPI().getCloudManager().sendCloudMessage("command", "command", JsonDocument.newDocument("uuid", player.getUniqueId()).append("command", command));
    }

    public static String convertTime(long timestampInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("d. MMM yyyy HH:mm:ss");
        Date date = new Date(timestampInMillis);
        return sdf.format(date);
    }
}
