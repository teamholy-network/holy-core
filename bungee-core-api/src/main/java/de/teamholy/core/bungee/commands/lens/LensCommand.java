package de.teamholy.core.bungee.commands.lens;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LensCommand extends Command {
    public LensCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {


      /*  if (!(commandSender instanceof ProxiedPlayer player)) {
            return;
        } */

        if (!commandSender.hasPermission("*")) {
            return;
        }

        String target = strings[0];

        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

        if (targetPlayer == null) {
            return;
        }

        String reason = strings[1];
        String multiply = strings[2];
        String max = strings[3];

        switch (reason) {
            case "faultscore":
                targetPlayer.sendMessage("§cGuardian §8× §7" + ("Your messages have been " + ("§c" + "flagged" + "§7") + " by our system. Please avoid toxic behaviour!") +
                    " §8(§7" + multiply + "/" + max + "§8)");
                targetPlayer.sendMessage("§cGuardian §8× §7" + ("Learn more about this on " + ("§6teamholy.de/guardian")));
                break;
            case "punishmultiplier":
                targetPlayer.sendMessage("§cGuardian §8× §7" + ("You have been " + ("§c" + "punished" + "§7") + " for your behavior ") +
                    "§8(§7x" + multiply + "§8)");
                break;
        }


    }
}
