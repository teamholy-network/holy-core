package de.teamholy.core.bungee.commands;

import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BungeeUtil;
import de.teamholy.core.bungee.util.ChatAction;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.util.Locale;
import java.util.UUID;

/* copyright by Yassino */
public class StatsCommand extends SenderCommand {

    private String prefix = "§cStats §8× §7";

    public StatsCommand(String[] commands, String permission) {
        super(commands, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {

            sendStatsHelp(player,player.getUniqueId());
            // /stats Yassino mlgrush alltime

        } else if (args.length == 1) {

            UUID uuid = BungeeUtil.parseTargetArgument(args[0]);
            if (uuid == null) {
                player.sendMessage(prefix + "Couldn't find player!");
                return;
            }

            sendStatsHelp(player,uuid);


        } else if (args.length == 3) {


            UUID uuid = BungeeUtil.parseTargetArgument(args[0]);
            if (uuid == null) {
                player.sendMessage(prefix + "Couldn't find player!");
                return;
            }

            Gamemodes gamemode = Gamemodes.valueOf(args[1]);
            if (gamemode == null || gamemode.getRankingKey().isEmpty()) {
                player.sendMessage(prefix + "Couldn't find gamemode");
                return;
            }

            StatsType statsType = StatsType.valueOf(args[2]);

            if (statsType == null) {
                player.sendMessage(prefix + "Couldn't find stats time");
                return;
            }


            sendStats(player,uuid,gamemode,statsType);
        }

    }

    private void sendStats(ProxiedPlayer player, UUID uuid, Gamemodes gamemodes, StatsType statsType) {

        String name = BungeeCore.getAPI().getUuidManager().getName(uuid);
        String nameColor = BungeeCore.getAPI().getCloudManager().getColor(uuid) + name;
        GameProfile gameProfile = BungeeCore.getAPI().getGameService().getEntity(uuid,() -> BungeeCore.getAPI().getGameService().getRepository().findFirstById(uuid));
        if (!gameProfile.exists(gamemodes.toString())) {
            player.sendMessage(prefix + "This player doesn't have any stats in §" + gamemodes.getColor() + gamemodes.toString().toUpperCase(Locale.ROOT));
            return;
        }

        player.sendMessage("§8§m-------------------------");
        player.sendMessage("");
        player.sendMessage("      " + nameColor + " §8- §" + gamemodes.getColor() + gamemodes);
        player.sendMessage("");
        player.sendMessage(" §7Ranking §8» §f#" + BungeeCore.getAPI().getRankingManager().getRankFromUUID(gamemodes,statsType,uuid));
        player.sendMessage("");
        gamemodes.getStatKeys().forEach(string -> {
            String stat = toFancy(string);
            long value = gameProfile.getStat(gamemodes.toString(),statsType,string);


            player.sendMessage(" §7" + stat + " §8» §" + gamemodes.getColor() + value);

        });

        player.sendMessage("");
        TextComponent textComponent = new net.md_5.bungee.api.chat.TextComponent();
        textComponent.addExtra(new ChatAction().text( ( statsType == StatsType.DAILY  ? "§a§lDAILY" : "§7Daily") ).hover(hover(StatsType.DAILY,nameColor,gamemodes))
                .execute("stats " + name + " " + gamemodes.toString().toUpperCase(Locale.ROOT) + " DAILY").component());
        textComponent.addExtra(" §8┃ ");
        textComponent.addExtra(new ChatAction().text( ( statsType == StatsType.MONTHLY  ? "§e§lMONTHLY" : "§7Monthly") ).hover(hover(StatsType.MONTHLY,nameColor,gamemodes))
                .execute("stats " + name + " " + gamemodes.toString().toUpperCase(Locale.ROOT) + " MONTHLY").component());

        textComponent.addExtra(" §8┃ ");
        textComponent.addExtra(new ChatAction().text( ( statsType == StatsType.ALLTIME  ? "§c§lALLTIME" : "§7alltime") ).hover(hover(StatsType.ALLTIME,nameColor,gamemodes))
                .execute("stats " + name + " " + gamemodes.toString().toUpperCase(Locale.ROOT) + " ALLTIME").component());

        player.sendMessage(textComponent);
        player.sendMessage("");


        player.sendMessage("§8§m-------------------------");


    }

    private String hover(StatsType statsType, String nameColor, Gamemodes gamemodes) {
        return "§7show " + statsType.toBeauty() + " §7stats for " + nameColor + " §7in §" + gamemodes.getColor() + gamemodes.toString();
    }


    private void sendStatsHelp(ProxiedPlayer player, UUID uuid) {

        String name = BungeeCore.getAPI().getUuidManager().getName(uuid);
        String nameColor = BungeeCore.getAPI().getCloudManager().getColor(uuid) + name;

        player.sendMessage("");
        player.sendMessage("          §f§lSTATS         ");
        player.sendMessage("");
        player.sendMessage("  §cclick to show stats of " + nameColor);
        player.sendMessage("");
        for (Gamemodes value : Gamemodes.values()) {
            if (!value.getRankingKey().isEmpty()) {
                player.sendMessage(new ChatAction().text(" §8» §" + value.getColor() + "§l" + value.toString().toUpperCase(Locale.ROOT)).hover("§7click to show stats of " + nameColor + " §7in §" + value.getColor() + value.toString().toUpperCase(Locale.ROOT))
                                .execute("stats " + name + " " + value.toString() + " " + StatsType.ALLTIME)
                        .component());
            }
        }
        player.sendMessage("");
        player.sendMessage("§8» §bhttps://teamholy.de/stats/?player=" + name);
        player.sendMessage("");


    }

    private String toFancy(String string) {

        switch (string) {
            case "kills":
                return "Kills";

            case "deaths":
                return "Deaths";

            case "played_games":
                return "Played games";

            case "won_games":
                return "Won games";

            case "destroyed_beds":
                return "Destroyed beds";
        }
        return "null";
    }
}
