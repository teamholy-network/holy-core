package de.teamholy.core.bukkit;

import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BannerMeta;

/* copyright by Yassino */
public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (player.getItemInHand() != null) {
            if (player.getItemInHand().getType() == Material.BANNER) {
                BannerMeta bannerMeta = (BannerMeta) player.getItemInHand().getItemMeta();
                System.out.println("----------");
                System.out.println("pattern ->");
                for (Pattern pattern : bannerMeta.getPatterns()) {
                    System.out.println(pattern.getPattern());
                    System.out.println(pattern.getColor());
                    System.out.println("-");
                }
                System.out.println("-CYE---");
                System.out.println(bannerMeta.getBaseColor());
                System.out.println("-----------");
            } else {
                BukkitCore.getInstance().getPerkManager().openMainPerkInventory(player);
            }
        }
        return false;
    }
}
