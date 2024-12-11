package de.teamholy.core.bukkit.commands;

import de.skydb.translateapi.bindings.BukkitTranslateAPI;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BugLogCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;
        if (!player.hasPermission("teamholy.team")) return false;

        File logFolder = new File(BukkitCore.getInstance().getServer().getWorldContainer().getParent(), "logs");

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File targetFolder = new File("/home/Buglogs", BukkitCore.getInstance().getGroup() + "_logs_" + timeStamp);

        if (!logFolder.exists()) {
            player.sendMessage(BukkitTranslateAPI.translate(player,"Der Log-Ordner existiert nicht!"));
            player.sendMessage(logFolder.getPath());
            return true;
        }

        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        try {
            copyFolder(logFolder.toPath(), targetFolder.toPath());
            player.sendMessage(BukkitTranslateAPI.translate(player,"Log erfolgreich gespeichert!"));
        } catch (IOException e) {
            player.sendMessage(BukkitTranslateAPI.translate(player,"Fehler beim Kopieren des Log-Ordners."));
            e.printStackTrace();
        }
        return false;
    }

    private void copyFolder(Path source, Path target) throws IOException {
        Files.walk(source).forEach(path -> {
            try {
                Path targetPath = target.resolve(source.relativize(path));
                Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
