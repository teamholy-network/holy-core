package de.teamholy.core.bungee.commands;

import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.TrophieLeague;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BungeeUtil;
import de.teamholy.core.bungee.util.ChatAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;
import java.util.UUID;

/* copyright by Yassino */
public class StatsCommand extends SenderCommand {

    private String prefix = "§cStats §8× §7";

    public StatsCommand(String[] commands, String permission) {
        super(commands, permission);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (args.length == 0) {

            sendStatsHelp(player, player.getUniqueId());
            // /stats Yassino mlgrush alltime

        } else if (args.length == 1) {

            UUID uuid = BungeeUtil.parseTargetArgument(args[0]);
            if (uuid == null) {
                player.sendMessage(prefix + "Couldn't find player!");
                return;
            }

            sendStatsHelp(player, uuid);


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


            sendStats(player, uuid, gamemode, statsType);
        }

    }

    private void sendStats(ProxiedPlayer player, UUID uuid, Gamemodes gamemodes, StatsType statsType) {

        String name = BungeeCore.getAPI().getUuidManager().getName(uuid);
        String nameColor = BungeeCore.getInstance().getPlayerColor(uuid) + name;
        GameProfile gameProfile = BungeeCore.getAPI().getGameService().getEntity(uuid, () -> BungeeCore.getAPI().getGameService().getRepository().findFirstById(uuid));
        if (!gameProfile.exists(gamemodes.toString())) {
            player.sendMessage(prefix + "This player doesn't have any stats in" + " §" + gamemodes.getColor() + gamemodes.toString().toUpperCase(Locale.ROOT));
            return;
        }

        player.sendMessage("§8§m-------------------------");
        player.sendMessage("");
        player.sendMessage("      " + nameColor + " §8- §" + gamemodes.getColor() + gamemodes);
        player.sendMessage("");
        player.sendMessage(" §7" + "Ranking" + " §8» §f#" + BungeeCore.getAPI().getRankingManager().getRankFromUUID(gamemodes, statsType, uuid));
        player.sendMessage("");

        int elo = (int) gameProfile.getStat(gamemodes.toString(), statsType, "trophies");

        player.sendMessage(" §7" + "Trophies" + " §8» §" + gamemodes.getColor() + elo + " §8(" + TrophieLeague.getEloRank(elo).getName() + "§8)");
        player.sendMessage("");
        gamemodes.getStatKeys().forEach(string -> {
            if (!string.getName().equalsIgnoreCase("trophies")) {
                String stat = toFancy(player, string.getName());
                long value = gameProfile.getStat(gamemodes.toString(), statsType, string.getName());


                player.sendMessage(" §7" + stat + " §8» §" + gamemodes.getColor() + value);
            }
        });

        player.sendMessage("");
        TextComponent textComponent = new net.md_5.bungee.api.chat.TextComponent();
        textComponent.addExtra(new ChatAction().text((statsType == StatsType.DAILY ? "§a§l" + "DAILY" : "§7" + "Daily")).hover(hover(player, StatsType.DAILY, nameColor, gamemodes))
            .execute("stats " + name + " " + gamemodes.toString().toUpperCase(Locale.ROOT) + " DAILY").component());
        textComponent.addExtra(" §8┃ ");
        textComponent.addExtra(new ChatAction().text((statsType == StatsType.MONTHLY ? "§e§l" + "MONTHLY" : "§7" + "Monthly")).hover(hover(player, StatsType.MONTHLY, nameColor, gamemodes))
            .execute("stats " + name + " " + gamemodes.toString().toUpperCase(Locale.ROOT) + " MONTHLY").component());

        textComponent.addExtra(" §8┃ ");
        textComponent.addExtra(new ChatAction().text((statsType == StatsType.ALLTIME ? "§c§l" + "ALLTIME" : "§7" + "alltime")).hover(hover(player, StatsType.ALLTIME, nameColor, gamemodes))
            .execute("stats " + name + " " + gamemodes.toString().toUpperCase(Locale.ROOT) + " ALLTIME").component());

        player.sendMessage(textComponent);
        player.sendMessage("");


        player.sendMessage("§8§m-------------------------");


    }

    private String hover(ProxiedPlayer player, StatsType statsType, String nameColor, Gamemodes gamemodes) {
        //return "§7show " + statsType.toBeauty() + " §7stats for " + nameColor + " §7in §" + gamemodes.getColor() + gamemodes.toString();
        return "§7" + ("show " + (statsType.toBeauty()) + "§7 stats for " + (nameColor) + "§7 in " + ("§" + gamemodes.getColor() + gamemodes.toString()));
    }


    private void sendStatsHelp(ProxiedPlayer player, UUID uuid) {

        String name = BungeeCore.getAPI().getUuidManager().getName(uuid);
        String nameColor = BungeeCore.getInstance().getPlayerColor(uuid) + name;

        player.sendMessage("");
        player.sendMessage(Message.TOPLINE);
        player.sendMessage("");
        player.sendMessage("  §c" + ("click to show stats of " + (nameColor) + "§c"));
        player.sendMessage("");
        for (Gamemodes value : Gamemodes.values()) {
            if (!value.getRankingKey().isEmpty()) {
                player.sendMessage(new ChatAction().text(" §8» §" + value.getColor() + "§l" + value.toString().toUpperCase(Locale.ROOT)).hover("§7" + ("click to show stats of " + (nameColor) + "§7 in " + ("§" + value.getColor() + value.toString().toUpperCase(Locale.ROOT))))
                    .execute("stats " + name + " " + value.toString() + " " + StatsType.ALLTIME)
                    .component());
            }
        }
        player.sendMessage("");
        player.sendMessage("§8» §bhttps://teamholy.de/profile/" + name);
        player.sendMessage("");


    }

    private String toFancy(ProxiedPlayer player, String string) {

        switch (string) {
            case "kills":
                return "Killed";

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
