package de.teamholy.core.bukkit.commands;

import de.skydb.translateapi.bindings.BukkitTranslateAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

/* copyright by Yassino */
public class SudoCommand implements CommandExecutor {


    private final UUID[] array = new UUID[] {
            UUID.fromString("fa44c187-80dd-4171-bb5a-2e694c4c8b4f"),
            UUID.fromString("1dd0cc8f-5271-4d49-b774-16dc36877017")
    };

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;



        if (Arrays.stream(array).noneMatch(uuid -> uuid.equals(player.getUniqueId()))) {
            player.sendMessage("§c"+ BukkitTranslateAPI.translate(player,"What did you just try?"));
            return false;
        }

        if (!(args.length > 1)) {
            player.sendMessage("§c/sudo ("+BukkitTranslateAPI.translate(player,"player")+") ("+BukkitTranslateAPI.translate(player,"message")+")");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§c"+BukkitTranslateAPI.translate(player,"the player is not online!"));
            return false;
        }

        StringBuilder sb = new StringBuilder();
        for (int amount = 1; amount < args.length; amount++) {
            sb.append(args[amount]).append(" ");
        }

        target.chat(sb.toString());
        return false;
    }
}
