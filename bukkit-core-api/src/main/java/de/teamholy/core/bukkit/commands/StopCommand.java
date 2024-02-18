package de.teamholy.core.bukkit.commands;

import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/* copyright by Yassino */
public class StopCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) return false;

        if (!player.hasPermission("teamholy.stop")) return false;

        BukkitCore.RESTART = true;


        final int[] i = {5};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (i[0] == 0) {
                    Bukkit.getOnlinePlayers().forEach(player1 -> player1.kickPlayer("§cServer restart"));
                    Bukkit.getScheduler().runTaskLater(BukkitCore.getInstance(), Bukkit::shutdown, 20L);
                }

                Bukkit.getOnlinePlayers().forEach(player1 -> {
                    player1.sendMessage("");
                    player1.sendMessage("§cServer restarts in §l" + i[0] + " §cseconds");
                    player1.sendMessage("");
                    player1.playSound(player1.getLocation(), Sound.NOTE_BASS, 20, 20);
                });

                i[0]--;
            }
        }.runTaskTimer(BukkitCore.getInstance(), 0, 20);



        return false;
    }
}
