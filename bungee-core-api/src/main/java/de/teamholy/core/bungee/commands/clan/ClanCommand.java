package de.teamholy.core.bungee.commands.clan;

import de.teamholy.core.translation.BungeeTranslateAPI;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.utility.ClanRank;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.SenderCommand;
import de.teamholy.core.bungee.util.BungeeUtil;
import de.teamholy.core.bungee.util.ChatAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/* copyright by Yassino */
public class ClanCommand extends SenderCommand {

    private static final int MAX_CLAN_MEMBERS = 50;

    private static final Pattern pattern = Pattern.compile("[a-zA-z0-9]*");

    public ClanCommand() {
        super(new String[] { "clan", "guild" }, null);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender
                    .sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(
                            ChatColor.RED).create());
            return;
        }
        BungeeCore.getAPI().getExecutor().execute(() -> {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("delete")) {
                    onDelete(player);
                } else if (args[0].equalsIgnoreCase("leave")) {
                    onLeave(player);
                } else if (args[0].equalsIgnoreCase("info")) {
                    onInfo(player);
                } else if (args[0].equalsIgnoreCase("togglejoin")) {
                    onToggleJoin(player);
                } else if (args[0].equalsIgnoreCase("promote")) {
                    player.sendMessage(Message.CLAN_PREFIX + "§7/clan promote ("
                            + BungeeTranslateAPI.translate(player, "name") + ")");
                } else if (args[0].equalsIgnoreCase("demote")) {
                    player.sendMessage(Message.CLAN_PREFIX + "§7/clan demote ("
                            + BungeeTranslateAPI.translate(player, "name") + ")");
                } else if (args[0].equalsIgnoreCase("invite")) {
                    player.sendMessage(Message.CLAN_PREFIX + "§7/clan invite ("
                            + BungeeTranslateAPI.translate(player, "name") + ")");
                } else if (args[0].equalsIgnoreCase("accept")) {
                    player.sendMessage(Message.CLAN_PREFIX + "§7/clan accept (tag)");
                } else if (args[0].equalsIgnoreCase("kick")) {
                    player.sendMessage(Message.CLAN_PREFIX + "§7/clan kick ("
                            + BungeeTranslateAPI.translate(player, "name") + ")");
                } else if (args[0].equalsIgnoreCase("create")) {
                    player.sendMessage(Message.CLAN_PREFIX + "§7/clan create ("
                            + BungeeTranslateAPI.translate(player, "name") + ") (tag)");
                } else if (args[0].equalsIgnoreCase("rename")) {
                    player.sendMessage(Message.CLAN_PREFIX + "§7/clan rename ("
                            + BungeeTranslateAPI.translate(player, "name") + ") (tag)");
                } else if (args[0].equalsIgnoreCase("chat")) {
                    player.sendMessage(Message.CLAN_PREFIX + "§7/clan chat ("
                            + BungeeTranslateAPI.translate(player, "message") + ")");
                } else if (args[0].equalsIgnoreCase("join")) {
                    player.sendMessage(Message.CLAN_PREFIX + "§7/clan join (tag)");
                } else {
                    try {
                        int page = Integer.parseInt(args[0]);
                        onHelp(player, page);
                    } catch (NumberFormatException e) {
                        onHelp(player, 1);
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("promote")) {
                    String target = args[1];
                    UUID uuid = BungeeUtil.parseTargetArgument(target);
                    if (uuid == null) {
                        commandSender.sendMessage(Message.CLAN_PREFIX + "§c"
                                + BungeeTranslateAPI.translate(player, "Error while fetching UUID from") + " §e"
                                + target + "§c!");
                        return;
                    }
                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(uuid);
                    if (targetPlayer == null || !targetPlayer.isConnected()) {
                        commandSender.sendMessage(Message.CLAN_PREFIX + BungeeCore.getInstance().getPlayerColor(uuid)
                                + target
                                + "§c " + BungeeTranslateAPI.translate(player, "has to be online to get promoted!"));
                        return;
                    }
                    onPromote(player, uuid);
                } else if (args[0].equalsIgnoreCase("userinfo") || args[0].equalsIgnoreCase("uinfo")) {
                    String target = args[1];
                    UUID uuid = BungeeUtil.parseTargetArgument(target);
                    if (uuid == null) {
                        commandSender.sendMessage(Message.CLAN_PREFIX + "§c"
                                + BungeeTranslateAPI.translate(player, "Error while fetching UUID from") + " §e"
                                + target + "§c!");
                        return;
                    }

                    ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(uuid,
                            () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(uuid));
                    if (clanProfile != null) {
                        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());
                        player.sendMessage(Message.CLAN_PREFIX + BungeeTranslateAPI.translate(player, "Clan") + "§8: §6"
                                + clan.getName());
                        player.sendMessage(Message.CLAN_PREFIX + "Tag§8: §6" + clan.getColor() + clan.getTag());
                        player.sendMessage(Message.CLAN_PREFIX + BungeeTranslateAPI.translate(player, "Clan Rank")
                                + "§8: §6" + clanProfile.getClanRank());
                    } else {
                        player.sendMessage(Message.CLAN_PREFIX
                                + BungeeTranslateAPI.translate(player, "The player is not in a clan!"));
                    }
                } else if (args[0].equalsIgnoreCase("demote")) {
                    String target = args[1];
                    UUID uuid = BungeeUtil.parseTargetArgument(target);
                    if (uuid == null) {
                        commandSender.sendMessage(Message.CLAN_PREFIX + "§c"
                                + BungeeTranslateAPI.translate(player, "Error while fetching UUID from") + " §e"
                                + target + "§c!");
                        return;
                    }

                    onDemote(player, uuid);
                } else if (args[0].equalsIgnoreCase("invite")) {
                    String target = args[1];
                    UUID uuid = BungeeUtil.parseTargetArgument(target);
                    if (uuid == null) {
                        commandSender.sendMessage(Message.CLAN_PREFIX + "§c"
                                + BungeeTranslateAPI.translate(player, "Error while fetching UUID from") + " §e"
                                + target + "§c!");
                        return;
                    }

                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(uuid);
                    if (targetPlayer == null || !targetPlayer.isConnected()) {
                        commandSender.sendMessage(Message.CLAN_PREFIX + BungeeCore.getInstance().getPlayerColor(uuid)
                                + target
                                + "§c " + BungeeTranslateAPI.translate(player, "has to be online to get invited!"));
                        return;
                    }
                    onInvite(player, uuid);
                } else if (args[0].equalsIgnoreCase("accept")) {
                    String tag = args[1];
                    onAccept(player, tag);
                } else if (args[0].equalsIgnoreCase("kick")) {
                    String target = args[1];
                    UUID uuid = BungeeUtil.parseTargetArgument(target);
                    if (uuid == null) {
                        commandSender.sendMessage(Message.CLAN_PREFIX + "§c"
                                + BungeeTranslateAPI.translate(player, "Error while fetching UUID from") + " §e"
                                + target + "§c!");
                        return;
                    }

                    onKick(player, uuid);
                } else if (args[0].equalsIgnoreCase("info")) {
                    String tag = args[1];
                    onInfo(player, tag);
                } else if (args[0].equalsIgnoreCase("create")) {
                    player.sendMessage(Message.CLAN_PREFIX + "§7/clan create ("
                            + BungeeTranslateAPI.translate(player, "name") + ") (tag)");
                } else if (args[0].equalsIgnoreCase("rename")) {
                    player.sendMessage(Message.CLAN_PREFIX + "§7/clan rename ("
                            + BungeeTranslateAPI.translate(player, "name") + ") (tag)");
                } else if (args[0].equalsIgnoreCase("chat")) {
                    onClanChat(player, args);
                } else if (args[0].equalsIgnoreCase("color")) {
                    onColor(player, args[1]);
                } else if (args[0].equalsIgnoreCase("help")) {
                    try {
                        int page = Integer.parseInt(args[1]);
                        onHelp(player, page);
                    } catch (NumberFormatException e) {
                        onHelp(player, 1);
                    }
                } else if (args[0].equalsIgnoreCase("delete") && args[1].equalsIgnoreCase("confirm")) {
                    onDeleteConfirm(player);
                } else if (args[0].equalsIgnoreCase("join")) {
                    String tag = args[1];
                    onJoin(player, tag);
                } else {
                    onHelp(player, 1);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (BungeeUtil.hasPermission(commandSender, "teamholy.perk.premium")) {
                        String tag = args[2];
                        String name = args[1];
                        onCreate(player, tag, name);
                    } else {
                        player.sendMessage(Message.CLAN_PREFIX + "§7"
                                + BungeeTranslateAPI.translate(player, "You need §6Premium §7to create a clan!")
                                + " (§chttps://shop.teamholy.de§7)");
                    }
                } else if (args[0].equalsIgnoreCase("rename")) {
                    String tag = args[2];
                    String name = args[1];
                    onRename(player, tag, name);
                } else if (args[0].equalsIgnoreCase("chat")) {
                    onClanChat(player, args);
                } else {
                    onHelp(player, 1);
                }
            } else if (args.length > 1 && args[0].equalsIgnoreCase("chat")) {
                onClanChat(player, args);
            } else {
                onHelp(player, 1);
            }
        });
    }

    private void onJoin(ProxiedPlayer player, String tag) {
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile != null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You already have a clan!"));
            return;
        }
        if (!BungeeCore.getAPI().getClanManager().existsClanTag(tag)) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "This clan does not exists!"));
            return;
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanByTag(tag);
        if (!clan.isOpenClan()) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "This clan is not open."));
            return;
        }

        if (clan.getMembers().size() >= MAX_CLAN_MEMBERS) {
            player.sendMessage(Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player,
                    "The clan reached the limit of " + MAX_CLAN_MEMBERS + " members!"));
            return;
        }
        clanProfile = new ClanPlayerProfile();
        clanProfile.setPlayerId(player.getUniqueId());
        clanProfile.setClanId(clan.getClanId());
        clanProfile.setClanRank(ClanRank.MEMBER);
        BungeeCore.getAPI().getClanPlayerService().saveEntity(clanProfile, true, true);
        clan.getMembers().add(player.getUniqueId());
        BungeeCore.getAPI().getClanManager().updateClan(clan);

        BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
            clanmember.sendMessage(Message.CLAN_PREFIX + BungeeCore.getInstance().getPlayerColor(player.getUniqueId())
                    + player.getName() + "§7 " + BungeeTranslateAPI.translate(clanmember, "joined the clan."));
        });
        ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(),
                () -> BungeeCore.getAPI().getCloudManager().announceClanUpdate(player.getUniqueId()), 2,
                TimeUnit.SECONDS);
    }

    private void onToggleJoin(ProxiedPlayer player) {
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile == null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You don't have a clan!"));
            return;
        }
        if (clanProfile.getClanRank() != ClanRank.LEADER) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "You don't have permissions to togglejoin the clan!"));
            return;
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());
        clan.setOpenClan(!clan.isOpenClan());
        BungeeCore.getAPI().getClanManager().updateClan(clan);
        if (clan.isOpenClan()) {
            BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
                clanmember.sendMessage(Message.CLAN_PREFIX + "§7"
                        + BungeeTranslateAPI.translate(clanmember, "Your clan is now open for everyone!"));
            });
        } else {
            BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
                clanmember.sendMessage(Message.CLAN_PREFIX + "§7"
                        + BungeeTranslateAPI.translate(clanmember, "Your clan is now closed!"));
            });
        }
    }

    public void onColor(ProxiedPlayer player, String color) {
        if (!player.hasPermission("teamholy.clan.color")) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "You don't have permission to colorize your clan."));
            return;
        }
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));

        if (color.length() > 4) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "You can only use 2 color codes with '&' symbol!"));
            return;
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());
        clan.setColor(color.replaceAll("&", "§"));
        BungeeCore.getAPI().getClanManager().updateClan(clan);
        BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
            clanmember.sendMessage(
                    Message.CLAN_PREFIX + "§7" + BungeeTranslateAPI.translate(clanmember, "Your clan got the color")
                            + ": " + clan.getColor() + clan.getName());
        });

        ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(),
                () -> BungeeCore.getAPI().getCloudManager().announceClanUpdate(player.getUniqueId()), 2,
                TimeUnit.SECONDS);
    }

    public void onDelete(ProxiedPlayer player) {
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile == null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You don't have a clan!"));
            return;
        }
        if (clanProfile.getClanRank() != ClanRank.LEADER) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "You must be a leader to delete the clan."));
            return;
        }
        player.sendMessage(Message.CLAN_PREFIX + "§6" + BungeeTranslateAPI.translate(player, "Clans"));
        player.sendMessage(
                Message.CLAN_PREFIX + "§7" + BungeeTranslateAPI.translate(player, "Are you sure to delete the clan?"));
        TextComponent main = new TextComponent(
                Message.CLAN_PREFIX + "§7" + BungeeTranslateAPI.translate(player, "Click here") + ": ");
        main.addExtra(new ChatAction().text("§4§l" + BungeeTranslateAPI.translate(player, "Delete"))
                .execute("clan delete confirm")
                .hover("§7" + BungeeTranslateAPI.translate(player, "Delete the clans and kick all members"))
                .component());
        player.sendMessage(main);
    }

    public void onDeleteConfirm(ProxiedPlayer player) {
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile == null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You don't have a clan!"));
            return;
        }
        if (clanProfile.getClanRank() != ClanRank.LEADER) {
            player.sendMessage(Message.CLAN_PREFIX + "§c+"
                    + BungeeTranslateAPI.translate(player, "You must be a leader to delete the clan."));
            return;
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());
        for (UUID memberUuid : clan.getMembers()) {
            if (!memberUuid.equals(player.getUniqueId())) {
                ClanPlayerProfile memberProfile = BungeeCore.getAPI().getClanPlayerService()
                        .getEntity(player.getUniqueId(), () -> BungeeCore.getAPI().getClanPlayerService()
                                .getRepository().findFirstById(player.getUniqueId()));
                if (memberProfile != null) {
                    if (memberProfile.getClanId().equals(clan.getClanId()))
                        BungeeCore.getAPI().getClanPlayerService().deleteEntity(memberProfile);
                }
            }
            ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(),
                    () -> BungeeCore.getAPI().getCloudManager().announceClanUpdate(player.getUniqueId()), 2,
                    TimeUnit.SECONDS);
        }
        BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
            clanmember.sendMessage(Message.CLAN_PREFIX + "§7"
                    + BungeeTranslateAPI.translate(clanmember, "Your clan has been deleted!"));
        });

        BungeeCore.getAPI().getClanPlayerService().deleteEntity(clanProfile);
        BungeeCore.getAPI().getClanManager().deleteClan(clan);
    }

    public void onLeave(ProxiedPlayer player) {
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile == null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You don't have a clan!"));
            return;
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());
        if (clanProfile.getClanRank() == ClanRank.LEADER) {
            Long leaderCount = clan.getMembers().stream().map(memberUuid -> {
                ClanPlayerProfile memberProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(memberUuid,
                        () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(memberUuid));
                if (memberProfile == null)
                    return null;
                return memberProfile.getClanRank() == ClanRank.LEADER ? memberUuid : null;
            }).filter(Objects::nonNull).count();
            if (leaderCount == 1) {
                player.sendMessage(Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player,
                        "Since you are the only leader, you must delete the clan or appoint someone as a leader to leave it."));
                return;
            }
        }
        clan.getMembers().remove(player.getUniqueId());
        BungeeCore.getAPI().getClanManager().updateClan(clan);
        BungeeCore.getAPI().getClanPlayerService().deleteEntity(clanProfile);
        player.sendMessage(Message.CLAN_PREFIX + "§7"
                + BungeeTranslateAPI.translate(player, "You have successfully left the clan!"));

        ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(),
                () -> BungeeCore.getAPI().getCloudManager().announceClanUpdate(player.getUniqueId()), 2,
                TimeUnit.SECONDS);
        BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
            clanmember.sendMessage(Message.CLAN_PREFIX + "§7" + BungeeTranslateAPI.translate(clanmember, "The player")
                    + " " + BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName() + "§7 "
                    + BungeeTranslateAPI.translate(clanmember, "has left the clan!"));
        });
    }

    public void onInfo(ProxiedPlayer player) {
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile == null) {
            player.sendMessage(Message.CLAN_PREFIX + BungeeTranslateAPI.translate(player, "§cYou don't have a clan!"));
            return;
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());
        printInfo(player, clan);
    }

    public void onHelp(ProxiedPlayer player, int number) {
        TextComponent main = new TextComponent("");
        Text hoverText = new Text("§7» §a" + BungeeTranslateAPI.translate(player, "Click Here") + " §7«");

        switch (number) {
            case 2:
                player.sendMessage(Message.TOPLINE);
                player.sendMessage("   " + Message.HELP_TITLE_CLAN + " §8- §7"
                        + BungeeTranslateAPI.translate(player, "Page") + " §62/2");
                player.sendMessage("");
                player.sendMessage(Message.HELP_BULLET + "/clan info");
                player.sendMessage(Message.HELP_BULLET + "/clan info (tag)");
                player.sendMessage(
                        Message.HELP_BULLET + "/clan userinfo (" + BungeeTranslateAPI.translate(player, "name") + ")");
                if (player.hasPermission("teamholy.clan.color"))
                    player.sendMessage(Message.HELP_BULLET + "/clan color ("
                            + BungeeTranslateAPI.translate(player, "color") + ")");
                player.sendMessage(Message.HELP_BULLET + "/clan delete");
                player.sendMessage(Message.HELP_BULLET + "/clan accept (tag)");
                player.sendMessage(Message.HELP_BULLET + "/clan join (tag)");
                player.sendMessage(Message.HELP_BULLET + "/clan rename (" + BungeeTranslateAPI.translate(player, "name")
                        + ") (tag)");
                player.sendMessage(
                        Message.HELP_BULLET + "/clan chat (" + BungeeTranslateAPI.translate(player, "message") + ")");
                player.sendMessage("");

                // Add clickable previous page button
                TextComponent prevPage = new TextComponent(
                        "  " + BungeeTranslateAPI.translate(player, "§6§lPrevious §f§fPage"));
                prevPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan help 1"));
                prevPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
                player.sendMessage(prevPage);

                player.sendMessage(Message.LINE_DOWN);
                break;

            default:
                player.sendMessage(Message.TOPLINE);
                player.sendMessage("   " + Message.HELP_TITLE_CLAN + " §8- §7"
                        + BungeeTranslateAPI.translate(player, "Page") + " §61/2");
                player.sendMessage("");
                player.sendMessage(Message.HELP_BULLET + "/clan help 1");
                player.sendMessage(Message.HELP_BULLET + "/clan help 2");
                player.sendMessage(
                        Message.HELP_BULLET + "/clan kick (" + BungeeTranslateAPI.translate(player, "name") + ")");
                player.sendMessage(
                        Message.HELP_BULLET + "/clan invite (" + BungeeTranslateAPI.translate(player, "name") + ")");
                player.sendMessage(Message.HELP_BULLET + "/clan leave");
                player.sendMessage(Message.HELP_BULLET + "/clan create (" + BungeeTranslateAPI.translate(player, "name")
                        + ") (tag)");
                player.sendMessage(
                        Message.HELP_BULLET + "/clan promote (" + BungeeTranslateAPI.translate(player, "name") + ")");
                player.sendMessage(
                        Message.HELP_BULLET + "/clan demote (" + BungeeTranslateAPI.translate(player, "name") + ")");
                player.sendMessage(Message.HELP_BULLET + "/clan togglejoin");
                player.sendMessage("");

                // Add clickable next page button
                TextComponent nextPage = new TextComponent(
                        "  " + BungeeTranslateAPI.translate(player, "§6§lNext §f§fPage"));
                nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan help 2"));
                nextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
                player.sendMessage(nextPage);

                player.sendMessage(Message.LINE_DOWN);
                break;
        }
    }

    public void onPromote(ProxiedPlayer player, UUID toPromote) {
        // LEADER can promote all
        // -> MOD -> LEADER
        // -> MEMBER -> MOD
        // MOD and MEMBER can't promote
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile == null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You don't have a clan!"));
            return;
        }
        if (player.getUniqueId().equals(toPromote)) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You can't promote yourself!"));
            return;
        }
        if (clanProfile.getClanRank() == ClanRank.MEMBER) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "You are not allowed to promote others!"));
            return;
        }
        ClanPlayerProfile promoteProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(toPromote,
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(toPromote));

        if (promoteProfile == null) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The player is not in your clan!"));
            return;
        }
        if (!promoteProfile.getClanId().equals(clanProfile.getClanId())) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The player is not in your clan!"));
            return;
        }
        if (promoteProfile.getClanRank() == ClanRank.LEADER) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "The player is already a") + " "
                            + promoteProfile.getClanRank().getFancy() + "§c!");
            return;
        }
        if (clanProfile.getClanRank() == ClanRank.LEADER) {

            ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(toPromote);
            boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

            ClanRank promote = promoteProfile.getClanRank() == ClanRank.MOD ? ClanRank.LEADER : ClanRank.MOD;
            promoteProfile.setClanRank(promote);
            BungeeCore.getAPI().getClanPlayerService().saveEntity(promoteProfile, isOnline, true);

            Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());

            BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
                clanmember.sendMessage(Message.CLAN_PREFIX + "§7"
                        + BungeeTranslateAPI.translate(clanmember, "The player") + " "
                        + BungeeCore.getInstance().getPlayerColor(toPromote)
                        + BungeeCore.getAPI().getUuidManager().getName(promoteProfile.getPlayerId()) + "§7 "
                        + BungeeTranslateAPI.translatePlaceholder(clanmember, "was promoted to {}", promote.getFancy())
                        + "§7!");
            });

        } else {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "You are not allowed to promote this player!"));
        }
    }

    public void onDemote(ProxiedPlayer player, UUID toDemote) {
        // LEADER can demote all, expect himself
        // -> MOD -> MEMBER
        // MOD and MEMBER can't demote
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile == null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You don't have a clan!"));
            return;
        }
        if (player.getUniqueId().equals(toDemote)) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You can't demote yourself!"));
            return;
        }
        ClanPlayerProfile demoteProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(toDemote,
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(toDemote));
        if (demoteProfile == null) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The player is not in your clan!"));
            return;
        }
        if (!demoteProfile.getClanId().equals(clanProfile.getClanId())) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The player is not in your clan!"));
            return;
        }
        if (demoteProfile.getClanRank() == ClanRank.MEMBER) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "You can only kick this member."));
            return;
        }
        if (clanProfile.getClanRank() == ClanRank.LEADER) {

            ProxiedPlayer promotePlayer = ProxyServer.getInstance().getPlayer(toDemote);
            boolean isOnline = promotePlayer != null && promotePlayer.isConnected();

            ClanRank demote = demoteProfile.getClanRank() == ClanRank.MOD ? ClanRank.MEMBER : ClanRank.MOD;

            demoteProfile.setClanRank(demote);
            BungeeCore.getAPI().getClanPlayerService().saveEntity(demoteProfile, isOnline, true);

            Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());

            BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
                clanmember.sendMessage(Message.CLAN_PREFIX + "§7"
                        + BungeeTranslateAPI.translate(clanmember, "The player") + " "
                        + BungeeCore.getInstance().getPlayerColor(toDemote)
                        + BungeeCore.getAPI().getUuidManager().getName(demoteProfile.getPlayerId()) + "§7 "
                        + BungeeTranslateAPI.translatePlaceholder(clanmember, "was demoted to {}", demote.getFancy())
                        + "§7!");
            });
        } else {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "You are not allowed to demote others!"));
        }
    }

    public void onInvite(ProxiedPlayer player, UUID toInvite) {
        // LEADER and MOD can invite
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile == null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You don't have a clan!"));
            return;
        }
        if (player.getUniqueId().equals(toInvite)) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You can't invite yourself!"));
            return;
        }
        if (clanProfile.getClanRank() == ClanRank.MEMBER) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "You are not allowed to invite others!"));
            return;
        }
        ClanPlayerProfile inviteProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(toInvite,
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(toInvite));
        if (inviteProfile != null) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The player already has a clan!"));
            return;
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());
        if (clan == null) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "A weird error occured.. Please contact an administrator!"));
            return;
        }
        if (clan.getMembers().size() >= MAX_CLAN_MEMBERS) {
            player.sendMessage(Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translatePlaceholder(player,
                    "The clan reached the limit of {} members!", String.valueOf(MAX_CLAN_MEMBERS)));
            return;
        }
        if (clan.getRequestsTo().contains(toInvite)) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The player is already invited!"));
            return;
        }
        final UUID clanId = clan.getClanId();
        clan.getRequestsTo().add(toInvite);
        BungeeCore.getAPI().getClanManager().updateClan(clan);
        ProxiedPlayer invitePlayer = ProxyServer.getInstance().getPlayer(toInvite);
        invitePlayer.sendMessage(Message.CLAN_PREFIX + "§6" + BungeeTranslateAPI.translate(player, "Clan"));
        invitePlayer.sendMessage(
                Message.CLAN_PREFIX + "§7" + BungeeTranslateAPI.translate(player, "You have been invited to the")
                        + " §6" + clan.getTag() + "§7 " + BungeeTranslateAPI.translate(player, "Clan") + ".");
        TextComponent main = new TextComponent(Message.CLAN_PREFIX + "§7Click here§8: ");
        main.addExtra(new ChatAction().text("§a§l" + BungeeTranslateAPI.translate(player, "Accept"))
                .execute("clan accept " + clan.getTag())
                .hover("§7" + BungeeTranslateAPI.translate(player, "Accept clan-invite")).component());
        invitePlayer.sendMessage(main);
        ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), () -> {
            Clan cleanClan = BungeeCore.getAPI().getClanManager().getClanById(clanId);
            if (cleanClan.getRequestsTo().contains(toInvite)) {
                cleanClan.getRequestsTo().remove(toInvite);
                BungeeCore.getAPI().getClanManager().updateClan(cleanClan);
            }
        }, 2, TimeUnit.MINUTES);
        BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
            clanmember.sendMessage(Message.CLAN_PREFIX + "§7" + BungeeTranslateAPI.translate(clanmember, "The player")
                    + " " + BungeeCore.getInstance().getPlayerColor(toInvite) + invitePlayer.getName() + "§7 "
                    + BungeeTranslateAPI.translate(clanmember, "has been invited to your clan!"));
        });

    }

    public void onAccept(ProxiedPlayer player, String tag) {
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile != null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You already have a clan!"));
            return;
        }
        if (!BungeeCore.getAPI().getClanManager().existsClanTag(tag)) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "This clan does not exists!"));
            return;
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanByTag(tag);
        if (!clan.getRequestsTo().contains(player.getUniqueId())) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "This clan did not invite you."));
            return;
        }
        player.sendMessage(Message.CLAN_PREFIX + "§7"
                + BungeeTranslateAPI.translate(player, "You have accepted the invite from §6" + clan.getTag()));

        BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
            clanmember.sendMessage(Message.CLAN_PREFIX + "§7" + BungeeTranslateAPI.translate(clanmember, "The player")
                    + " " + BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName() + "§7 "
                    + BungeeTranslateAPI.translate(clanmember, "has accepted the clan-invite!"));
        });

        clan.getRequestsTo().remove(player.getUniqueId());
        clan.getMembers().add(player.getUniqueId());

        clanProfile = new ClanPlayerProfile();
        clanProfile.setPlayerId(player.getUniqueId());
        clanProfile.setClanRank(ClanRank.MEMBER);
        clanProfile.setClanId(clan.getClanId());

        BungeeCore.getAPI().getClanPlayerService().saveEntity(clanProfile, true, true);

        BungeeCore.getAPI().getClanManager().updateClan(clan);

        ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(),
                () -> BungeeCore.getAPI().getCloudManager().announceClanUpdate(player.getUniqueId()), 2,
                TimeUnit.SECONDS);
    }

    public void onKick(ProxiedPlayer player, UUID toKick) {
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile == null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You don't have a clan!"));
            return;
        }
        if (player.getUniqueId().equals(toKick)) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You can't kick yourself!"));
            return;
        }
        if (clanProfile.getClanRank() == ClanRank.MEMBER) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "You are not allowed to kick others!"));
            return;
        }
        ClanPlayerProfile kickProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(toKick,
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(toKick));
        if (kickProfile == null || !kickProfile.getClanId().equals(clanProfile.getClanId())) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The player is not in your clan!"));
            return;
        }
        if (!clanProfile.canKick(kickProfile.getClanRank())) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You can't kick this member."));
            return;
        }

        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());
        clan.getMembers().remove(toKick);
        BungeeCore.getAPI().getClanManager().updateClan(clan);
        BungeeCore.getAPI().getClanPlayerService().deleteEntity(kickProfile);

        BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
            clanmember.sendMessage(Message.CLAN_PREFIX + "§7" + BungeeTranslateAPI.translate(clanmember, "The player")
                    + " " + BungeeCore.getInstance().getPlayerColor(toKick)
                    + BungeeCore.getAPI().getUuidManager().getName(kickProfile.getPlayerId()) + "§7 "
                    + BungeeTranslateAPI.translatePlaceholder(clanmember, "has been kicked by §6{}", player.getName())
                    + "§7!");
        });

        ProxiedPlayer kickPlayer = ProxyServer.getInstance().getPlayer(toKick);
        if (kickPlayer != null && kickPlayer.isConnected()) {
            kickPlayer.sendMessage(Message.CLAN_PREFIX + "§7"
                    + BungeeTranslateAPI.translate(kickPlayer, "You have been kicked from the clan."));
            ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(),
                    () -> BungeeCore.getAPI().getCloudManager().announceClanUpdate(player.getUniqueId()), 2,
                    TimeUnit.SECONDS);
        }
    }

    public void onInfo(ProxiedPlayer player, String tag) {
        if (!BungeeCore.getAPI().getClanManager().existsClanTag(tag)) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "This clan does not exist."));
            return;
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanByTag(tag);
        printInfo(player, clan);
    }

    public void onCreate(ProxiedPlayer player, String tag, String name) {
        // Tag conditions
        if (!pattern.matcher(tag).matches()) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The tag may only contain the characters a-z A-Z and 0-9."));
            return;
        }
        if (tag.length() > 5) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The tag can be only 5 characters long."));
            return;
        }
        if (tag.length() < 3) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The tag must be at least 3 characters long."));
            return;
        }
        if (BungeeCore.getAPI().getClanManager().existsClanTag(tag)) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "The tag already exists."));
            return;
        }
        // Name conditions
        if (!pattern.matcher(name).matches()) {
            player.sendMessage(Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player,
                    "The name may only contain the characters a-z A-Z and 0-9."));
            return;
        }
        if (name.length() > 16) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The name can be only 16 characters long."));
            return;
        }
        if (name.length() < 3) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The name must be at least 3 characters long."));
            return;
        }
        if (BungeeCore.getAPI().getClanManager().existsClanName(name)) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "The name already exists."));
            return;
        }

        // MongoDB calls
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));

        if (clanProfile != null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You are already in a clan!"));
            return;
        }

        Clan clan = BungeeCore.getAPI().getClanManager().createClan(name, tag, player.getUniqueId());
        clanProfile = new ClanPlayerProfile();
        clanProfile.setPlayerId(player.getUniqueId());
        clanProfile.setClanId(clan.getClanId());
        clanProfile.setClanRank(ClanRank.LEADER);
        BungeeCore.getAPI().getClanPlayerService().saveEntity(clanProfile, true, true);

        player.sendMessage(Message.CLAN_PREFIX + "§7"
                + BungeeTranslateAPI.translatePlaceholder(player, "You have created the §6{}§7 clan!", name));

        ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(),
                () -> BungeeCore.getAPI().getCloudManager().announceClanUpdate(player.getUniqueId()), 2,
                TimeUnit.SECONDS);
    }

    public void onRename(ProxiedPlayer player, String tag, String name) {
        // Tag conditions
        if (!pattern.matcher(tag).matches()) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The tag may only contain the characters a-z A-Z and 0-9."));
            return;
        }
        if (tag.length() > 5) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The tag can be only 5 characters long."));
            return;
        }
        if (tag.length() < 3) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The tag must be at least 3 characters long."));
            return;
        }
        // Name conditions
        if (!pattern.matcher(name).matches()) {
            player.sendMessage(Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player,
                    "The name may only contain the characters a-z A-Z and 0-9."));
            return;
        }
        if (name.length() > 16) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The name can be only 16 characters long."));
            return;
        }
        if (name.length() < 3) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "The name must be at least 3 characters long."));
            return;
        }

        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));

        if (clanProfile == null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "You are not in a clan!"));
            return;
        }
        if (clanProfile.getClanRank() != ClanRank.LEADER) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "You are not allowed to rename the clan."));
            return;
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());
        if (clan.getTag().equalsIgnoreCase(tag) && clan.getName().equalsIgnoreCase(name)) {
            player.sendMessage(Message.CLAN_PREFIX + "§c"
                    + BungeeTranslateAPI.translate(player, "Please choose other name or tag!"));
            return;
        }

        if (!clan.getName().equalsIgnoreCase(name) && BungeeCore.getAPI().getClanManager().existsClanName(name)) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "The name already exists."));
            return;
        }
        if (!clan.getTag().equalsIgnoreCase(tag) && BungeeCore.getAPI().getClanManager().existsClanTag(tag)) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§c" + BungeeTranslateAPI.translate(player, "The tag already exists."));
            return;
        }

        clan.setTag(tag);
        clan.setName(name);
        BungeeCore.getAPI().getClanManager().updateClan(clan);
        // BungeeCore.getInstance().getBungeePlayerManager().sendClanMessage(clan,
        // Message.CLAN_PREFIX + "§7Your clan has been renamed!");
        BungeeCore.getInstance().getBungeePlayerManager().getClanMessagePlayers(clan).forEach(clanmember -> {
            clanmember.sendMessage(Message.CLAN_PREFIX + "§7"
                    + BungeeTranslateAPI.translate(clanmember, "Your clan has been renamed!"));
        });

        ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), () -> {
            for (UUID member : clan.getMembers()) {
                BungeeCore.getAPI().getCloudManager().announceClanUpdate(member);
            }
        }, 2, TimeUnit.SECONDS);

    }

    public void printInfo(ProxiedPlayer proxiedPlayer, Clan clan) {
        List<ClanPlayerProfile> clanMemberList = new ArrayList<>();
        for (UUID memberUuid : clan.getMembers()) {
            ClanPlayerProfile memberProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(memberUuid,
                    () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(memberUuid));
            if (memberProfile != null) {
                if (memberProfile.getClanId().equals(clan.getClanId()))
                    clanMemberList.add(memberProfile);
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        proxiedPlayer.sendMessage(Message.TOPLINE);
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage("§7" + BungeeTranslateAPI.translate(proxiedPlayer, "Creation date") + "§8: §a"
                + simpleDateFormat.format(new Date(clan.getCreationDate())));
        proxiedPlayer.sendMessage("§7" + BungeeTranslateAPI.translate(proxiedPlayer, "Member") + "§8: §6"
                + clan.getMembers().size() + "§7/" + MAX_CLAN_MEMBERS);
        proxiedPlayer.sendMessage(
                "§7" + BungeeTranslateAPI.translate(proxiedPlayer, "Name") + "§8: " + clan.getColor() + clan.getName());
        proxiedPlayer.sendMessage("§7Tag§8: " + clan.getColor() + clan.getTag());
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage("§4" + BungeeTranslateAPI.translate(proxiedPlayer, "Leader") + " §8» ");
        StringBuilder leaders = new StringBuilder();
        for (ClanPlayerProfile profile : clanMemberList) {
            if (profile.getClanRank() == ClanRank.LEADER) {
                leaders.append(BungeeCore.getInstance().getPlayerColor(profile.getPlayerId()))
                        .append(BungeeCore.getAPI().getUuidManager().getName(profile.getPlayerId())).append("§7, ");
            }
        }
        proxiedPlayer.sendMessage(leaders.toString());
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage("§cMods §8» ");
        StringBuilder mods = new StringBuilder();
        for (ClanPlayerProfile profile : clanMemberList) {
            if (profile.getClanRank() == ClanRank.MOD) {
                mods.append(BungeeCore.getInstance().getPlayerColor(profile.getPlayerId()))
                        .append(BungeeCore.getAPI().getUuidManager().getName(profile.getPlayerId())).append("§7, ");
            }
        }
        proxiedPlayer.sendMessage(mods.toString());
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage("§a" + BungeeTranslateAPI.translate(proxiedPlayer, "Member") + " §8» ");
        StringBuilder members = new StringBuilder();
        for (ClanPlayerProfile profile : clanMemberList) {
            if (profile.getClanRank() == ClanRank.MEMBER) {
                members.append(BungeeCore.getInstance().getPlayerColor(profile.getPlayerId()))
                        .append(BungeeCore.getAPI().getUuidManager().getName(profile.getPlayerId()))
                        .append("§7, ");
            }
        }
        proxiedPlayer.sendMessage(members.toString());
        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage(Message.LINE_DOWN);
    }

    public void onClanChat(ProxiedPlayer player, String[] args) {
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile == null) {
            player.sendMessage(
                    Message.CLAN_PREFIX + "§7" + BungeeTranslateAPI.translate(player, "You don't have a clan!"));
            return;
        }
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());
        BungeeCore.getInstance().getBungeePlayerManager().sendClanMessage(clan,
                Message.CLAN_PREFIX + ClanRank.parsePrefix(clanProfile.getClanRank()) + "§l"
                        + clanProfile.getClanRank().getFancy() + " "
                        + BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName() + "§8 » §7"
                        + message);
    }

}
