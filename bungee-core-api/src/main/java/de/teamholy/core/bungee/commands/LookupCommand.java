package de.teamholy.core.bungee.commands;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.driver.permission.PermissionUserGroupInfo;
import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.entities.mute.MuteProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.punishhistory.PunishHistoryProfile;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.api.utility.TimeUtil;
import de.teamholy.core.api.utility.UUIDUtility;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BungeeUtil;
import de.teamholy.core.bungee.util.ChatAction;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static de.teamholy.core.bungee.BungeeCore.RESTBASE;

/* copyright by Yassino */
public class LookupCommand extends SenderCommand {

    public LookupCommand() {
        super(new String[]{"lookup", "check", "info"}, "teamholy.check");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("Lookup doesnt allowed for you, sry..");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!BungeeUtil.hasPermission(player, "teamholy.check")) {
            BungeeUtil.sendNoPermission(player);
            return;
        }
        BungeeCore.getAPI().getExecutor().execute(() -> {
            if (args.length == 1) {
                String target = args[0];
                UUID uuid = BungeeUtil.parseTargetArgument(target);
                if (uuid == null) {
                    player.sendMessage(Message.LOOKUP_PREFIX + "§c"+ BungeeTranslateAPI.translate(player,"Error while fetching Data about")+" §e" + target + "§c!");
                    return;
                }

                PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(uuid,
                    () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(uuid));
                ClanPlayerProfile clanPlayerProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(uuid,
                    () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(uuid));
                BanProfile banProfile = BungeeCore.getAPI().getBanService().getEntity(uuid,
                    () -> BungeeCore.getAPI().getBanService().getRepository().findFirstById(uuid));
                MuteProfile muteProfile = BungeeCore.getAPI().getMuteService().getEntity(uuid,
                    () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(uuid));
                PunishHistoryProfile punishHistoryProfile = BungeeCore.getAPI().getPunishHistoryService().getEntity(uuid,
                    () -> BungeeCore.getAPI().getPunishHistoryService().getRepository().findFirstById(uuid));


                Scanner scanner = null;
                String country = "§c"+BungeeTranslateAPI.translate(player,"No country found");

                try {
                    scanner = new Scanner(new URL( RESTBASE + "holy/vpn/check/" + playerProfile.getIp() + "/adasaisuoa2j2j2j2jnvalkooiwuhlkabvd").openStream());
                    String response = scanner.useDelimiter("\\A").next();
                    JSONObject jsonResponse = new JSONObject(response);


                    if (!jsonResponse.isEmpty() || !jsonResponse.isNull("country")) {
                        country = jsonResponse.getString("countryname");
                    }

                } catch (IOException ignored) {

                } finally {
                    if (scanner != null) {
                        scanner.close();
                    }
                }



                player.sendMessage(Message.LINE_DOWN);
                player.sendMessage("");
                TextComponent nameComp = new TextComponent("§7"+BungeeTranslateAPI.translate(player,"Name")+" §8» ");
                nameComp.addExtra(new ChatAction().text(BungeeCore.getInstance().getPlayerColor(playerProfile.getPlayerId()) + playerProfile.getPlayerName()).suggest(uuid.toString()).hover("§7"+BungeeTranslateAPI.translate(player,"Click to copy uuid")).component());
                player.sendMessage(nameComp);

                TextComponent cracked = new TextComponent("§7"+BungeeTranslateAPI.translate(player,"Premium Account")+" §8» " + (UUIDUtility.isCracked(playerProfile.getPlayerId(), playerProfile.getPlayerName()) ? "§c"+BungeeTranslateAPI.translate(player,"no") : "§a"+BungeeTranslateAPI.translate(player,"yes")));
                player.sendMessage(cracked);

                TextComponent onlineComp = new TextComponent("§7"+BungeeTranslateAPI.translate(player,"Status")+" §8» ");
                if (playerProfile.isOnline()) {
                    onlineComp.addExtra(new ChatAction().text("§a"+BungeeTranslateAPI.translate(player,"Online")+" §8(§7" + playerProfile.getServerName() + "§8)").execute("server " + playerProfile.getServerName()).hover("§7"+BungeeTranslateAPI.translatePlaceholder(player,"Click to jump on {}", playerProfile.getServerName())).component());
                } else {
                    onlineComp.addExtra(new TextComponent("§cOffline"));
                }
                player.sendMessage(onlineComp);

                TextComponent onlinetimeComp = new TextComponent("§7"+BungeeTranslateAPI.translate(player,"Onlinetime")+" §8» §6" + TimeUtil.beautifyTime(playerProfile.getOnlineTime(), TimeUnit.MILLISECONDS));
                player.sendMessage(onlinetimeComp);

                TextComponent coins = new TextComponent("§7"+BungeeTranslateAPI.translate(player,"Coins")+" §8» §6" + BungeeCore.getAPI().getCoinManager().formatInteger(playerProfile.getCoins()));
                player.sendMessage(coins);

                TextComponent tokens = new TextComponent("§7Tokens §8» §6");
                tokens.addExtra(new ChatAction().text("§a" + playerProfile.getJoinMeTokens() + " JT").hover("§a" + playerProfile.getJoinMeTokens() + " Joinme Tokens").component());
                tokens.addExtra(" §8┃ ");
                tokens.addExtra(new ChatAction().text("§c" + playerProfile.getStatsResetTokens() + " ST").hover("§c" + playerProfile.getStatsResetTokens() + " Statsreset Tokens").component());
                player.sendMessage(tokens);

                if (player.hasPermission("teamholy.check.admin")) {
                    TextComponent ipComp = new TextComponent("§7IP §8» ");
                    ipComp.addExtra(new ChatAction().text("§6" + playerProfile.getIp()).suggest(playerProfile.getIp()).hover("§7"+BungeeTranslateAPI.translate(player,"Click to copy IP")).component());
                    player.sendMessage(ipComp);
                }

                TextComponent countryComp = new TextComponent("§7"+BungeeTranslateAPI.translate(player,"Country")+" §8» §6" + country);
                player.sendMessage(countryComp);


                TextComponent accComp = new TextComponent("§7Accounts §8» ");
                List<PlayerProfile> profileList = BungeeCore.getAPI().getPlayerService().getRepository().findManyByIp(playerProfile.getIp());
                int size = profileList.isEmpty() ? 0 : profileList.size() - 1;
                accComp.addExtra(new ChatAction().text("§6" + size + " alt(s)").hover("§7"+BungeeTranslateAPI.translate(player,"Click to show account list")).execute("lookup 3 " + playerProfile.getPlayerId()).component());
                player.sendMessage(accComp);
                player.sendMessage("");

                player.sendMessage("§7"+BungeeTranslateAPI.translate(player,"First Join")+" §8» §e" + BungeeUtil.parseDate(playerProfile.getFirstJoin()));
                player.sendMessage("§7"+BungeeTranslateAPI.translate(player,"Last Join")+" §8» §e" + BungeeUtil.parseDate(playerProfile.getLastJoin()));
                player.sendMessage("§7"+BungeeTranslateAPI.translate(player,"Registered since")+" §8» §6" + TimeUtil.beautifyTime(playerProfile.getLastJoin() - playerProfile.getFirstJoin(), TimeUnit.MILLISECONDS, true));

                player.sendMessage("");


                IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(playerProfile.getPlayerId());
                IPermissionGroup permissionGroup = CloudNetDriver.getInstance().getPermissionManagement().getGroup(PlayerRank.valueOf(playerProfile.getRank()).getName());
                long rankTime = 0;

                for (PermissionUserGroupInfo group : permissionUser.getGroups()) {
                    if (group.getGroup().equalsIgnoreCase(permissionGroup.getName()))
                        rankTime = group.getTimeOutMillis();
                }

                TextComponent rankComp = new TextComponent("§7Highest Rank §8» ");
                if (rankTime == 0 || rankTime == -1) {
                    rankComp.addExtra(new ChatAction().text(permissionGroup.getDisplay() + permissionGroup.getName() + " §8┃ §aLifetime").hover("§7Click to show all ranks").execute("lookup 4 " + uuid)
                        .component());
                } else {
                    rankComp.addExtra(new ChatAction().text(permissionGroup.getDisplay() + permissionGroup.getName() + " §8┃ §e" + BungeeUtil.parseDate(rankTime)).hover("§7Click to show all ranks").execute("lookup 4 " + uuid)
                        .component());
                }
                player.sendMessage(rankComp);

                TextComponent clanComp = new TextComponent("§7Clan §8» ");
                if (clanPlayerProfile != null) {
                    Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanPlayerProfile.getClanId());
                    clanComp.addExtra(new ChatAction().text("§6" + clan.getName() + " §8┃ " + clanPlayerProfile.getClanRank().getFancy()).execute("clan info " + clan.getTag()).hover("§7Click to show clan info").component());
                } else {
                    clanComp.addExtra(new ChatAction().text("§7No clan").component());
                }
                player.sendMessage(clanComp);

                player.sendMessage("");
                TextComponent punishComp = new TextComponent("§7Punish §8» ");
                if (banProfile != null) {
                    punishComp.addExtra(new ChatAction().text("§4Banned").execute("lookup 1 " + uuid + " ban").hover("§7Click to show ban").component());
                } else if (muteProfile != null) {
                    punishComp.addExtra(new ChatAction().text("§cMuted").execute("lookup 1 " + uuid + " mute").hover("§7Click to show mute").component());
                } else {
                    punishComp.addExtra(new ChatAction().text("§7No Punish").component());
                }
                player.sendMessage(punishComp);

                TextComponent banHistoryComp = new TextComponent("§7BanHistory §8» ");
                if (punishHistoryProfile.getBanProfileMap().size() > 0) {
                    banHistoryComp.addExtra(new ChatAction().text("§4" + punishHistoryProfile.getBanProfileMap().size() + " Ban(s)").execute("lookup 2 " + uuid + " ban").hover("§7Click to show BanHistory").component());
                } else {
                    banHistoryComp.addExtra(new TextComponent("§aNo BanHistory"));
                }
                player.sendMessage(banHistoryComp);

                TextComponent muteHistoryComp = new TextComponent("§7MuteHistory §8» ");
                if (punishHistoryProfile.getMuteProfileMap().size() > 0) {
                    muteHistoryComp.addExtra(new ChatAction().text("§c" + punishHistoryProfile.getMuteProfileMap().size() + " Mute(s)").execute("lookup 2 " + uuid + " mute").hover("§7Click to show MuteHistory").component());
                } else {
                    muteHistoryComp.addExtra(new TextComponent("§aNo MuteHistory"));
                }
                player.sendMessage(muteHistoryComp);
                player.sendMessage("");
                player.sendMessage(Message.LINE_DOWN);
            } else if (args.length >= 2) {
                try {
                    int num = Integer.parseInt(args[0]);

                    UUID uuid = BungeeUtil.parseTargetArgument(args[1]);
                    if (uuid == null) {
                        player.sendMessage(Message.LOOKUP_PREFIX + "§cError while fetching UUID from §e" + args[1] + "§c!");
                        return;
                    }
                    String targetName = BungeeCore.getAPI().getUuidManager().getName(uuid);

                    PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(uuid,
                        () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(uuid));

                    switch (num) {
                        case 1:
                            if (args[2].equalsIgnoreCase("ban")) {
                                BanProfile banProfile = BungeeCore.getAPI().getBanService().getEntity(uuid,
                                    () -> BungeeCore.getAPI().getBanService().getRepository().findFirstById(uuid));
                                if (banProfile != null) {
                                    String author = BungeeCore.getAPI().getUuidManager().getName(banProfile.getAuthorId());
                                    String until = BungeeUtil.parseDate(banProfile.getValidUntilDate()) + " §7(" + TimeUtil.beautifyTime(banProfile.getDuration(), TimeUnit.MILLISECONDS) + ")";
                                    printPunish(player, BungeeCore.getAPI().getUuidManager().getName(banProfile.getPlayerId()), "Ban", author, banProfile.getReason(), until, banProfile.getEvidence());
                                } else {
                                    player.sendMessage(Message.LOOKUP_PREFIX + "§cThe player §e" + targetName + "§c isn't banned!");
                                }
                            } else if (args[2].equalsIgnoreCase("mute")) {
                                MuteProfile muteProfile = BungeeCore.getAPI().getMuteService().getEntity(uuid,
                                    () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(uuid));
                                if (muteProfile != null) {
                                    String author = BungeeCore.getAPI().getUuidManager().getName(muteProfile.getAuthorId());
                                    String until = BungeeUtil.parseDate(muteProfile.getValidUntilDate()) + " §7(" + TimeUtil.beautifyTime(muteProfile.getDuration(), TimeUnit.MILLISECONDS) + ")";
                                    printPunish(player, BungeeCore.getAPI().getUuidManager().getName(muteProfile.getPlayerId()), "Ban", author, muteProfile.getReason(), until, muteProfile.getEvidence());
                                } else {
                                    player.sendMessage(Message.LOOKUP_PREFIX + "§cThe player §e" + targetName + "§c isn't muted!");
                                }
                            } else {
                                printUsage(sender);
                            }
                            break;
                        case 2:
                            if (args[2].equalsIgnoreCase("ban")) {
                                PunishHistoryProfile punishHistoryProfile = BungeeCore.getAPI().getPunishHistoryService().getEntity(uuid,
                                    () -> BungeeCore.getAPI().getPunishHistoryService().getRepository().findFirstById(uuid));
                                if (punishHistoryProfile.getBanProfileMap().size() > 0) {
                                    player.sendMessage(Message.LINE_DOWN);
                                    player.sendMessage("");
                                    player.sendMessage("§7BanHistory of §6" + targetName);
                                    for (BanProfile banProfile : punishHistoryProfile.getBanProfileMap().values()) {
                                        String date = BungeeUtil.parseDate(banProfile.getCreateDate());
                                        String reason = banProfile.getReason();
                                        String author = BungeeCore.getInstance().getPlayerColor(banProfile.getAuthorId()) + BungeeCore.getAPI().getUuidManager().getName(banProfile.getAuthorId());
                                        String evidence = banProfile.getEvidence();
                                        TextComponent punishComp = new TextComponent(" §8» §7" + date + " §8┃§7 " + reason + " §8┃§7 " + author + " §8┃§7 ");
                                        punishComp.addExtra(new ChatAction().text(evidence.equalsIgnoreCase("No evidence") ? evidence : "Show Evidence").hover("Copy evidence: " + evidence).suggest(evidence).component());
                                        player.sendMessage(punishComp);
                                    }
                                    player.sendMessage("");
                                    player.sendMessage(new ChatAction().text("  §6§lLOOKUP").hover("§7Click back to lookup").execute("lookup " + targetName).component());
                                    player.sendMessage("");
                                    player.sendMessage(Message.LINE_DOWN);
                                } else {
                                    player.sendMessage(Message.LOOKUP_PREFIX + "§cNo history found about §e" + targetName + "§c!");
                                }
                            } else if (args[2].equalsIgnoreCase("mute")) {
                                PunishHistoryProfile punishHistoryProfile = BungeeCore.getAPI().getPunishHistoryService().getEntity(uuid,
                                    () -> BungeeCore.getAPI().getPunishHistoryService().getRepository().findFirstById(uuid));
                                if (punishHistoryProfile.getMuteProfileMap().size() > 0) {
                                    player.sendMessage(Message.LINE_DOWN);
                                    player.sendMessage("");
                                    player.sendMessage("§7MuteHistory of §6" + targetName);
                                    for (MuteProfile muteProfile : punishHistoryProfile.getMuteProfileMap().values()) {
                                        String date = BungeeUtil.parseDate(muteProfile.getCreateDate());
                                        String reason = muteProfile.getReason();
                                        String author = BungeeCore.getInstance().getPlayerColor(muteProfile.getAuthorId()) + BungeeCore.getAPI().getUuidManager().getName(muteProfile.getAuthorId());
                                        String evidence = muteProfile.getEvidence();
                                        TextComponent punishComp = new TextComponent(" §8» §7" + date + " §8┃§7 " + reason + " §8┃§7 " + author + " §8┃§7 ");
                                        punishComp.addExtra(new ChatAction().text(evidence.equalsIgnoreCase("No evidence") ? evidence : "Show Evidence").hover("Copy evidence: " + evidence).suggest(evidence).component());
                                        player.sendMessage(punishComp);
                                    }
                                    player.sendMessage("");
                                    player.sendMessage(new ChatAction().text("  §6§lLOOKUP").hover("§7Click back to lookup").execute("lookup " + targetName).component());
                                    player.sendMessage("");
                                    player.sendMessage(Message.LINE_DOWN);
                                } else {
                                    player.sendMessage(Message.LOOKUP_PREFIX + "§cNo history found about §e" + targetName + "§c!");
                                }
                            } else {
                                printUsage(sender);
                            }
                            break;
                        case 3:
                            List<PlayerProfile> profileList = BungeeCore.getAPI().getPlayerService().getRepository().findManyByIp(playerProfile.getIp());
                            int size = profileList.isEmpty() ? 0 : profileList.size() - 1;
                            if (size != 0) {
                                player.sendMessage(Message.LINE_DOWN);
                                player.sendMessage("");
                                player.sendMessage("§7Accounts of §6" + targetName);
                                for (PlayerProfile profile : profileList) {
                                    if (!profile.getPlayerName().equalsIgnoreCase(targetName)) {
                                        String prefix = BungeeCore.getInstance().getPlayerColor(profile.getPlayerId());
                                        TextComponent comp = new ChatAction().text(" §8- " + prefix + profile.getPlayerName()).hover("§7Click to lookup").execute("lookup " + profile.getPlayerName()).component();
                                        player.sendMessage(comp);
                                    }
                                }
                                player.sendMessage("");
                                player.sendMessage(Message.LINE_DOWN);
                            } else {
                                player.sendMessage(Message.LOOKUP_PREFIX + "§cNo more accounts found of §e" + targetName + "§c!");
                            }
                            break;

                        case 4:
                            player.sendMessage(Message.LINE_DOWN);
                            player.sendMessage("");
                            player.sendMessage("§7Ranks of §6" + targetName);
                            player.sendMessage("");
                            IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(playerProfile.getPlayerId());
                            for (PermissionUserGroupInfo group : permissionUser.getGroups()) {
                                IPermissionGroup permissionGroup = CloudNetDriver.getInstance().getPermissionManagement().getGroup(group.getGroup());
                                if (group.getTimeOutMillis() == 0 || group.getTimeOutMillis() == -1) {
                                    player.sendMessage(" §8- " + permissionGroup.getDisplay() + permissionGroup.getName() + permissionGroup.getDisplay() + " §8┃ §aLifetime");
                                } else {
                                    player.sendMessage(" §8- " + permissionGroup.getDisplay() + permissionGroup.getName() + permissionGroup.getDisplay() + " §8┃ §e" + BungeeUtil.parseDate(group.getTimeOutMillis()));
                                }
                            }
                            player.sendMessage("");
                            player.sendMessage(Message.LINE_DOWN);
                            break;
                        default:
                            printUsage(sender);
                            break;
                    }
                } catch (NumberFormatException e) {
                    printUsage(sender);
                }
            } else {
                printUsage(sender);
            }
        });
    }

    public void printUsage(CommandSender commandSender) {
        commandSender.sendMessage(Message.LOOKUP_PREFIX + "§7/lookup (name)");
    }

    public void printPunish(ProxiedPlayer player, String name, String type, String author, String reason, String until, String evidence) {
        player.sendMessage(Message.LINE_DOWN);
        player.sendMessage("");
        player.sendMessage("§7" + type + " of §6" + name);
        player.sendMessage("§7Author: §6" + author);
        player.sendMessage("§7Reason: §6" + reason);
        player.sendMessage("§7Until: §6" + until);
        player.sendMessage("§7Evidence: §6" + evidence);
        player.sendMessage("");
        player.sendMessage(new ChatAction().text("  §6§lLOOKUP").hover("§7Click back to lookup").execute("lookup " + name).component());
        player.sendMessage("");
        player.sendMessage(Message.LINE_DOWN);
    }

}
