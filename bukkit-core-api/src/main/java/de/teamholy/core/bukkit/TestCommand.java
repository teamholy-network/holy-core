package de.teamholy.core.bukkit;

import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerService;
import de.teamholy.core.api.utility.Gamemodes;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.Random;

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
                BukkitCore.getAPI().getGameService().getEntityAsync(player.getUniqueId(),() -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(player.getUniqueId()) , gameProfile -> {
                    for (Gamemodes value : Gamemodes.values()) {
                        for (StatsType statsType : StatsType.values()) {
                            for (String statKey : value.getStatKeys()) {
                                if (!statKey.isEmpty()) gameProfile.setStat(value.toString(),statsType,statKey,new Random().nextInt(1000));
                            }
                        }
                    }
                    BukkitCore.getAPI().getGameService().saveEntity(gameProfile,true,true);
                });
            } else {
                BukkitCore.getInstance().getPerkManager().openMainPerkInventory(player);
            }
        }
        return false;
    }
}
