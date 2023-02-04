package de.teamholy.core.bungee.commands.clan;

import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.SenderCommand;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

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
        BungeeCore.getAPI().getExecutor().execute(() -> {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("delete")) {
                    sender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan delete (tag)");
                } else if (args[0].equalsIgnoreCase("kick")) {
                    sender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan kick (player)");
                } else if (args[0].equalsIgnoreCase("changecolor")) {
                    sender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan changecolor (tag) (color)");
                } else printUsage(sender);
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("delete")) {
                    String tag = args[1];
                    Clan clan = BungeeCore.getAPI().getClanManager().getClanByTag(tag);
                    if (clan == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§cThe clan " + tag + " doesn't exists!");
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
                    sender.sendMessage(Message.CLAN_PREFIX + "§aYou deleted the clan " + clan.getTag() + "!");
                } else if (args[0].equalsIgnoreCase("kick")) {
                    String target = args[1];
                    UUID uuid = BungeeUtil.parseTargetArgument(target);
                    if (uuid == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§cError while fetching UUID from §e" + target + "§c!");
                        return;
                    }
                    ClanPlayerProfile clanPlayerProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(uuid,
                            () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(uuid));
                    if (clanPlayerProfile == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§cThe player " + target + " doesn't has a clan!");
                        return;
                    }
                    Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanPlayerProfile.getClanId());
                    if (clan.getMembers().contains(uuid)) {
                        clan.getMembers().remove(uuid);
                        BungeeCore.getAPI().getClanManager().updateClan(clan);
                    }
                    BungeeCore.getAPI().getClanPlayerService().deleteEntity(clanPlayerProfile);
                    ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), () -> BungeeCore.getAPI().getCloudManager().announceClanUpdate(uuid), 2, TimeUnit.SECONDS);
                    sender.sendMessage(Message.CLAN_PREFIX + "§aThe player §c" + target + " §agot kicked from the clan §e" + clan.getTag() + "§a!");

                } else {
                    printUsage(sender);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("changecolor")) {
                    String tag = args[1];
                    Clan clan = BungeeCore.getAPI().getClanManager().getClanByTag(tag);
                    String color = args[2];
                    if (clan == null) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§cThe clan " + tag + " doesn't exists!");
                        return;
                    }
                    if (color.length() > 4) {
                        sender.sendMessage(Message.CLAN_PREFIX + "§cYou can only use 2 color codes with '&' symbol!");
                        return;
                    }

                    clan.setColor(color.replaceAll("&", "§"));
                    BungeeCore.getAPI().getClanManager().updateClan(clan);
                    BungeeCore.getInstance().getBungeePlayerManager().sendClanMessage(clan, Message.CLAN_PREFIX + "§7Your clan got the color§8: " + clan.getColor() + clan.getName());
                    for (UUID member : clan.getMembers()) {
                        ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), () -> {
                            BungeeCore.getAPI().getCloudManager().announceClanUpdate(member);
                        }, 2, TimeUnit.SECONDS);
                    }
                }
            } else printUsage(sender);
        });
    }

    public void printUsage(CommandSender commandSender) {
        commandSender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan delete (tag)");
        commandSender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan kick (name)");
        commandSender.sendMessage(Message.CLAN_PREFIX + "§7/adminclan changecolor (tag) (color)");
    }

}
