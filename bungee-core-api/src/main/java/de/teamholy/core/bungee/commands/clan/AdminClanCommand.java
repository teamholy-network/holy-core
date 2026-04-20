package de.teamholy.core.bungee.commands.clan;

import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.utility.ClanRank;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.SenderCommand;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class AdminClanCommand extends SenderCommand {


    public AdminClanCommand() {
        super(new String[]{"adminclan", "aclan"}, "teamholy.adminclan");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!BungeeUtil.hasPermission(sender, "teamholy.adminclan")) {
            BungeeUtil.sendNoPermission(sender);
            return;
        }
        UUID author = BungeeUtil.parseAuthorUUID(sender);
        BungeeCore.getAPI().getExecutor().execute(() -> {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("delete")) {
                    sender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan delete (tag)");
                } else if (args[0].equalsIgnoreCase("kick")) {
                    sender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan kick ("+ "player"+")");
                } else if (args[0].equalsIgnoreCase("changecolor")) {
                    sender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan changecolor (tag) ("+"color"+")");
                } else printUsage(sender);
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("delete")) {
                    String tag = args[1];
                    Clan clan = BungeeCore.getAPI().getClanManager().getClanByTag(tag);
                    if (clan == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§c"+("The clan " + (tag) + " doesn't exists!"));
                        return;
                    }
                    for (UUID member : clan.getMembers()) {
                        ClanPlayerProfile clanPlayerProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(member,
                            () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(member));
                        if (clanPlayerProfile != null && clan.getClanId().equals(clanPlayerProfile.getClanId())) {
                            BungeeCore.getAPI().getClanPlayerService().deleteEntity(clanPlayerProfile);
                            ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), () -> BungeeCore.getAPI().getCloudManager().announceClanUpdate(member), 2, TimeUnit.SECONDS);
                        }
                    }
                    BungeeCore.getAPI().getClanManager().deleteClan(clan);
                    sender.sendMessage(Message.CLAN_PREFIX + "§a"+("You deleted the clan " + (clan.getTag()) + "!"));
                } else if (args[0].equalsIgnoreCase("kick")) {
                    String target = args[1];
                    UUID uuid = BungeeUtil.parseTargetArgument(target);
                    if (uuid == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§c"+"Error while fetching UUID from"+" §e" + target + "§c!");
                        return;
                    }
                    ClanPlayerProfile clanPlayerProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(uuid,
                        () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(uuid));
                    if (clanPlayerProfile == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§c"+("The player " + (target) + " doesn't has a clan!"));
                        return;
                    }
                    Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanPlayerProfile.getClanId());
                    if (clan.getMembers().contains(uuid)) {
                        clan.getMembers().remove(uuid);
                        BungeeCore.getAPI().getClanManager().updateClan(clan);
                    }
                    BungeeCore.getAPI().getClanPlayerService().deleteEntity(clanPlayerProfile);
                    ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), () -> BungeeCore.getAPI().getCloudManager().announceClanUpdate(uuid), 2, TimeUnit.SECONDS);
                    sender.sendMessage(Message.CLAN_PREFIX + "§a"+("The player §c" + (target) + "§a got kicked from §e" + (clan.getTag()) + "§a!"));
                } else if (args[0].equalsIgnoreCase("promote")) {
                    String target = args[1];
                    UUID uuid = BungeeUtil.parseTargetArgument(target);
                    if (uuid == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§c"+"Error while fetching UUID from"+" §e" + target + "§c!");
                        return;
                    }

                    ClanPlayerProfile promoteProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(uuid,
                        () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(uuid));

                    if (promoteProfile == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§c"+"The player dont have a clan!");
                        return;
                    }
                    if (promoteProfile.getClanRank() == ClanRank.LEADER) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§c"+"The player is already a"+" " + promoteProfile.getClanRank().getFancy() + "§c!");
                        return;
                    }

                    ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(uuid);
                    boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

                    ClanRank promote = promoteProfile.getClanRank() == ClanRank.MOD ? ClanRank.LEADER : ClanRank.MOD;
                    promoteProfile.setClanRank(promote);
                    BungeeCore.getAPI().getClanPlayerService().saveEntity(promoteProfile, isOnline, true);

                    Clan clan = BungeeCore.getAPI().getClanManager().getClanById(promoteProfile.getClanId());
                    sender.sendMessage(Message.CLAN_PREFIX + "The player was promoted to" + promote.getFancy());

                    BungeeCore.getInstance().getBungeePlayerManager().sendClanMessage(clan, Message.CLAN_PREFIX + "§7"+"The player"+" " +
                        BungeeCore.getInstance().getPlayerColor(uuid) + BungeeCore.getAPI().getUuidManager().getName(promoteProfile.getPlayerId()) +
                        "§7 "+("was promoted to " + (promote.getFancy())) + "§7!");
                } else {
                    printUsage(sender);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("changecolor")) {
                    String tag = args[1];
                    Clan clan = BungeeCore.getAPI().getClanManager().getClanByTag(tag);
                    String color = args[2];
                    if (clan == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§c"+("The clan " + (tag) + " doesn't exists!"));
                        return;
                    }

                    clan.setColor(color.replaceAll("&", "§"));
                    BungeeCore.getAPI().getClanManager().updateClan(clan);
                    BungeeCore.getInstance().getBungeePlayerManager().sendClanMessage(clan, Message.CLAN_PREFIX + "§7"+"Your clan got the color"+"§8: " + clan.getColor() + clan.getName());
                    for (UUID member : clan.getMembers()) {
                        ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), () -> {
                            BungeeCore.getAPI().getCloudManager().announceClanUpdate(member);
                        }, 2, TimeUnit.SECONDS);
                    }
                } else if (args[0].equalsIgnoreCase("send")) {


                    String target = args[2];
                    UUID uuid = BungeeUtil.parseTargetArgument(target);
                    if (uuid == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§c"+"Error while fetching UUID from"+" §e" + target + "§c!");
                        return;
                    }

                    UUID finalUuid1 = uuid;
                    ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(uuid,
                        () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(finalUuid1));
                    if (clanProfile != null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§c"+"The player already has a clan!");
                        return;
                    }

                    Clan clan = BungeeCore.getAPI().getClanManager().getClanByTag(args[1]);

                    if (clan == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§c"+"This clan does not exists!");
                        return;
                    }

                    if (clan.getMembers().size() >= 50) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§c"+"The clan reached the limit of 50 members!");
                        return;
                    }

                    sender.sendMessage(Message.CLAN_PREFIX + "§a"+"The player is now in the" + " " + clan.getColor() + clan.getName() + " §aclan!");
                    clanProfile = new ClanPlayerProfile();
                    clanProfile.setPlayerId(uuid);
                    clanProfile.setClanId(clan.getClanId());
                    clanProfile.setClanRank(ClanRank.MEMBER);
                    BungeeCore.getAPI().getClanPlayerService().saveEntity(clanProfile, true, true);
                    clan.getMembers().add(uuid);
                    BungeeCore.getAPI().getClanManager().updateClan(clan);
                    BungeeCore.getInstance().getBungeePlayerManager().sendClanMessage(clan, Message.CLAN_PREFIX + BungeeCore.getInstance().getPlayerColor(uuid) + BungeeCore.getAPI().getUuidManager().getName(uuid) + " §7 "+"joined the clan.");
                    ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), () -> BungeeCore.getAPI().getCloudManager().announceClanUpdate(finalUuid1), 2, TimeUnit.SECONDS);
                } else printUsage(sender);
            } else printUsage(sender);
        });
    }

    public void printUsage(CommandSender commandSender) {
        UUID author = BungeeUtil.parseAuthorUUID(commandSender);
        commandSender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan delete (tag)");
        commandSender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan kick ("+"name"+")");
        commandSender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan promote ("+"name"+")");
        commandSender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan changecolor (tag) ("+"color"+")");
        commandSender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan send (tag) ("+"player"+")");
    }

}
