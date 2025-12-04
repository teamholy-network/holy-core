package de.teamholy.core.bukkit.commands;

import de.skydb.translateapi.bindings.BukkitTranslateAPI;
import de.teamholy.core.bukkit.BukkitCore;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

/* copyright by Yassino */
public class StopCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s,
        String[] strings) {
        if (!commandSender.hasPermission("teamholy.stop")) {
            return false;
        }

        BukkitCore.RESTART = true;

        final AtomicInteger countDown = new AtomicInteger(5);
        new BukkitRunnable() {
            @Override
            public void run() {
                final int currentCountDown = countDown.getAndDecrement();
                if (currentCountDown == 0) {
                    Bukkit.getOnlinePlayers().forEach(player1 -> player1.kickPlayer(
                        "§c" + BukkitTranslateAPI.translate(player1, "Server restart")));
                    Bukkit.getScheduler()
                        .runTaskLater(BukkitCore.getInstance(), Bukkit::shutdown, 20L);
                    cancel();
                }

                Bukkit.getOnlinePlayers().forEach(player1 -> {
                    player1.sendMessage("");
                    player1.sendMessage("§c" + BukkitTranslateAPI.translate(player1, "Server " + "restarts in")
                            + " §l" + currentCountDown + " §c" + BukkitTranslateAPI.translate(
                            player1, "seconds"));
                    player1.sendMessage("");
                    player1.playSound(player1.getLocation(), Sound.NOTE_BASS, 20, 20);
                });
            }
        }.runTaskTimer(BukkitCore.getInstance(), 0, 20);

        return false;
    }
}
