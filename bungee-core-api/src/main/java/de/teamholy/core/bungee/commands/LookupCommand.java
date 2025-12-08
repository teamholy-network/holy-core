package de.teamholy.core.bungee.commands;

import static de.teamholy.core.bungee.BungeeCore.RESTBASE;

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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONObject;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LookupCommand extends SenderCommand {

    private static final String PERMISSION_LOOKUP = "teamholy.check";
    private static final String PERMISSION_LOOKUP_ADMIN = "teamholy.check.admin";
    private static final String DEFAULT_COUNTRY = "§cNo country found";

    public LookupCommand() {
        super(new String[]{"lookup", "check", "info"}, PERMISSION_LOOKUP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            sender.sendMessage(new TextComponent("Lookup doesn't allowed for you, sry.."));
            return;
        }

        if (!BungeeUtil.hasPermission(player, PERMISSION_LOOKUP)) {
            BungeeUtil.sendNoPermission(player);
            return;
        }

        BungeeCore.getAPI().getExecutor().execute(() -> {
            try {
                if (args.length == 1) {
                    handleBasicLookup(player, args[0]);
                } else if (args.length >= 2) {
                    handleAdvancedLookup(player, args);
                } else {
                    printUsage(sender);
                }
            } catch (Exception e) {
                log.error("Error executing lookup command for player {}", player.getName(), e);
                player.sendMessage(
                    new TextComponent(Message.LOOKUP_PREFIX + "§cEin Fehler ist aufgetreten."));
            }
        });
    }

    private void handleBasicLookup(ProxiedPlayer player, String target) {
        UUID uuid = BungeeUtil.parseTargetArgument(target);
        if (uuid == null) {
            player.sendMessage(new TextComponent(Message.LOOKUP_PREFIX + "§c" +
                BungeeTranslateAPI.translate(player, "Error while fetching Data about") + " §e"
                + target + "§c!"));
            return;
        }

        PlayerProfile playerProfile = loadPlayerProfile(uuid);
        if (playerProfile == null) {
            player.sendMessage(
                new TextComponent(Message.LOOKUP_PREFIX + "§cSpieler nicht gefunden!"));
            return;
        }

        ClanPlayerProfile clanPlayerProfile = loadClanPlayerProfile(uuid);
        BanProfile banProfile = loadBanProfile(uuid);
        MuteProfile muteProfile = loadMuteProfile(uuid);
        PunishHistoryProfile punishHistoryProfile = loadPunishHistoryProfile(uuid);

        String country = fetchCountryInfo(player, playerProfile.getIp());

        displayPlayerInfo(player, playerProfile, clanPlayerProfile, banProfile, muteProfile,
            punishHistoryProfile, country);
    }

    private void handleAdvancedLookup(ProxiedPlayer player, String[] args) {
        try {
            int subCommand = Integer.parseInt(args[0]);
            UUID uuid = BungeeUtil.parseTargetArgument(args[1]);

            if (uuid == null) {
                player.sendMessage(new TextComponent(
                    Message.LOOKUP_PREFIX + "§cError while fetching UUID from §e" +
                        args[1] + "§c!"));
                return;
            }

            String targetName = BungeeCore.getAPI().getUuidManager().getName(uuid);
            PlayerProfile playerProfile = loadPlayerProfile(uuid);

            if (playerProfile == null) {
                player.sendMessage(
                    new TextComponent(Message.LOOKUP_PREFIX + "§cSpieler nicht gefunden!"));
                return;
            }

            switch (subCommand) {
                case 1 -> handlePunishLookup(player, uuid, targetName, args);
                case 2 -> handleHistoryLookup(player, uuid, targetName, args);
                case 3 -> handleAlternateAccounts(player, playerProfile, targetName);
                case 4 -> handleRankHistory(player, playerProfile, targetName);
                default -> printUsage(player);
            }
        } catch (NumberFormatException e) {
            printUsage(player);
        }
    }

    private void handlePunishLookup(ProxiedPlayer player, UUID uuid, String targetName,
        String[] args) {
        if (args.length < 3) {
            printUsage(player);
            return;
        }

        String punishType = args[2].toLowerCase();

        if (punishType.equals("ban")) {
            Optional<BanProfile> banProfile = Optional.ofNullable(loadBanProfile(uuid));

            banProfile.ifPresentOrElse(
                profile -> displayPunishmentDetails(player, profile, targetName, "Ban"),
                () -> player.sendMessage(
                    new TextComponent(Message.LOOKUP_PREFIX + "§cThe player §e" +
                        targetName + "§c isn't banned!"))
            );
        } else if (punishType.equals("mute")) {
            Optional<MuteProfile> muteProfile = Optional.ofNullable(loadMuteProfile(uuid));

            muteProfile.ifPresentOrElse(
                profile -> displayPunishmentDetails(player, profile, targetName, "Mute"),
                () -> player.sendMessage(
                    new TextComponent(Message.LOOKUP_PREFIX + "§cThe player §e" +
                        targetName + "§c isn't muted!"))
            );
        } else {
            printUsage(player);
        }
    }

    private void handleHistoryLookup(ProxiedPlayer player, UUID uuid, String targetName,
        String[] args) {
        if (args.length < 3) {
            printUsage(player);
            return;
        }

        PunishHistoryProfile historyProfile = loadPunishHistoryProfile(uuid);
        String historyType = args[2].toLowerCase();
        Integer page = 1;
        if (args.length == 4) {
            try {
                page = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                printUsage(player);
                return;
            }
        }

        if (historyType.equals("ban")) {
            displayBanHistory(player, historyProfile, targetName, page);
        } else if (historyType.equals("mute")) {
            displayMuteHistory(player, historyProfile, targetName, page);
        } else {
            printUsage(player);
        }
    }

    private void handleAlternateAccounts(ProxiedPlayer player, PlayerProfile playerProfile,
        String targetName) {
        List<PlayerProfile> alternateAccounts = BungeeCore.getAPI().getPlayerService()
            .getRepository().findManyByIp(playerProfile.getIp());

        int altCount = Math.max(0, alternateAccounts.size() - 1);

        if (altCount == 0) {
            player.sendMessage(
                new TextComponent(Message.LOOKUP_PREFIX + "§cNo more accounts found of §e" +
                    targetName + "§c!"));
            return;
        }

        player.sendMessage(new TextComponent(Message.LINE_DOWN));
        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent("§7Accounts of §6" + targetName));

        alternateAccounts.stream()
            .filter(profile -> !profile.getPlayerName().equalsIgnoreCase(targetName))
            .forEach(profile -> {
                String prefix = BungeeCore.getInstance().getPlayerColor(profile.getPlayerId());
                TextComponent comp = new ChatAction()
                    .text(" §8- " + prefix + profile.getPlayerName())
                    .hover("§7Click to lookup")
                    .execute("lookup " + profile.getPlayerName())
                    .component();
                player.sendMessage(comp);
            });

        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent(Message.LINE_DOWN));
    }

    private void handleRankHistory(ProxiedPlayer player, PlayerProfile playerProfile,
        String targetName) {
        player.sendMessage(new TextComponent(Message.LINE_DOWN));
        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent("§7Ranks of §6" + targetName));
        player.sendMessage(new TextComponent(""));

        IPermissionUser permissionUser = CloudNetDriver.getInstance()
            .getPermissionManagement()
            .getUser(playerProfile.getPlayerId());

        if (permissionUser == null) {
            player.sendMessage(new TextComponent("§cKeine Rank-Informationen verfügbar."));
            player.sendMessage(new TextComponent(""));
            player.sendMessage(new TextComponent(Message.LINE_DOWN));
            return;
        }

        permissionUser.getGroups().forEach(group -> {
            IPermissionGroup permissionGroup = CloudNetDriver.getInstance()
                .getPermissionManagement()
                .getGroup(group.getGroup());

            if (permissionGroup != null) {
                String timeInfo = formatRankTime(group.getTimeOutMillis());
                player.sendMessage(new TextComponent(" §8- " + permissionGroup.getDisplay() +
                    permissionGroup.getName() + permissionGroup.getDisplay() + " §8┃ " + timeInfo));
            }
        });

        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent(Message.LINE_DOWN));
    }

    private void displayPlayerInfo(ProxiedPlayer player, PlayerProfile playerProfile,
        ClanPlayerProfile clanPlayerProfile, BanProfile banProfile,
        MuteProfile muteProfile, PunishHistoryProfile punishHistoryProfile,
        String country) {

        player.sendMessage(new TextComponent(Message.LINE_DOWN));
        player.sendMessage(new TextComponent(""));

        displayNameAndUUID(player, playerProfile);

        displayPremiumStatus(player, playerProfile);

        displayOnlineStatus(player, playerProfile);

        displayPlaytimeAndCurrency(player, playerProfile);

        if (player.hasPermission(PERMISSION_LOOKUP_ADMIN)) {
            displayIPInfo(player, playerProfile, country);
        } else {
            displayCountryInfo(player, country);
        }

        displayAlternateAccountsInfo(player, playerProfile);

        player.sendMessage(new TextComponent(""));

        displayJoinInfo(player, playerProfile);

        player.sendMessage(new TextComponent(""));

        displayRankInfo(player, playerProfile);

        displayClanInfo(player, clanPlayerProfile);

        player.sendMessage(new TextComponent(""));

        displayPunishmentInfo(player, playerProfile.getPlayerId(), banProfile, muteProfile,
            punishHistoryProfile);

        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent(Message.LINE_DOWN));
    }

    private void displayNameAndUUID(ProxiedPlayer player, PlayerProfile playerProfile) {
        TextComponent nameComp = new TextComponent(
            "§7" + BungeeTranslateAPI.translate(player, "Name") + " §8» ");
        nameComp.addExtra(new ChatAction()
            .text(BungeeCore.getInstance().getPlayerColor(playerProfile.getPlayerId()) +
                playerProfile.getPlayerName())
            .suggest(playerProfile.getPlayerId().toString())
            .hover("§7" + BungeeTranslateAPI.translate(player, "Click to copy uuid"))
            .component());
        player.sendMessage(nameComp);
    }

    private void displayPremiumStatus(ProxiedPlayer player, PlayerProfile playerProfile) {
        boolean isPremium = !UUIDUtility.isCracked(playerProfile.getPlayerId(),
            playerProfile.getPlayerName());
        String status = isPremium
            ? "§a" + BungeeTranslateAPI.translate(player, "yes")
            : "§c" + BungeeTranslateAPI.translate(player, "no");

        player.sendMessage(
            new TextComponent("§7" + BungeeTranslateAPI.translate(player, "Premium Account") +
                " §8» " + status));
    }

    private void displayOnlineStatus(ProxiedPlayer player, PlayerProfile playerProfile) {
        TextComponent onlineComp = new TextComponent("§7" +
            BungeeTranslateAPI.translate(player, "Status") + " §8» ");

        if (playerProfile.isOnline()) {
            onlineComp.addExtra(new ChatAction()
                .text("§a" + BungeeTranslateAPI.translate(player, "Online") + " §8(§7" +
                    playerProfile.getServerName() + "§8)")
                .execute("server " + playerProfile.getServerName())
                .hover("§7" + BungeeTranslateAPI.translatePlaceholder(player, "Click to jump on {}",
                    playerProfile.getServerName()))
                .component());
        } else {
            onlineComp.addExtra(new TextComponent("§cOffline"));
        }

        player.sendMessage(onlineComp);
    }

    private void displayPlaytimeAndCurrency(ProxiedPlayer player, PlayerProfile playerProfile) {
        player.sendMessage(
            new TextComponent("§7" + BungeeTranslateAPI.translate(player, "Onlinetime") +
                " §8» §6" + TimeUtil.beautifyTime(playerProfile.getOnlineTime(),
                TimeUnit.MILLISECONDS)));

        player.sendMessage(new TextComponent("§7" + BungeeTranslateAPI.translate(player, "Coins") +
            " §8» §6" + BungeeCore.getAPI().getCoinManager()
            .formatInteger(playerProfile.getCoins())));

        TextComponent tokens = new TextComponent("§7Tokens §8» §6");
        tokens.addExtra(new ChatAction()
            .text("§a" + playerProfile.getJoinMeTokens() + " JT")
            .hover("§a" + playerProfile.getJoinMeTokens() + " Joinme Tokens")
            .component());
        tokens.addExtra(" §8┃ ");
        tokens.addExtra(new ChatAction()
            .text("§c" + playerProfile.getStatsResetTokens() + " ST")
            .hover("§c" + playerProfile.getStatsResetTokens() + " Statsreset Tokens")
            .component());
        player.sendMessage(tokens);
    }

    private void displayIPInfo(ProxiedPlayer player, PlayerProfile playerProfile, String country) {
        TextComponent ipComp = new TextComponent("§7IP §8» ");
        ipComp.addExtra(new ChatAction()
            .text("§6" + playerProfile.getIp())
            .suggest(playerProfile.getIp())
            .hover("§7" + BungeeTranslateAPI.translate(player, "Click to copy IP"))
            .component());
        player.sendMessage(ipComp);
        displayCountryInfo(player, country);
    }

    private void displayCountryInfo(ProxiedPlayer player, String country) {
        player.sendMessage(
            new TextComponent("§7" + BungeeTranslateAPI.translate(player, "Country") +
                " §8» §6" + country));
    }

    private void displayAlternateAccountsInfo(ProxiedPlayer player, PlayerProfile playerProfile) {
        TextComponent accComp = new TextComponent("§7Accounts §8» ");
        List<PlayerProfile> profileList = BungeeCore.getAPI().getPlayerService()
            .getRepository().findManyByIp(playerProfile.getIp());
        int size = Math.max(0, profileList.size() - 1);

        accComp.addExtra(new ChatAction()
            .text("§6" + size + " alt(s)")
            .hover("§7" + BungeeTranslateAPI.translate(player, "Click to show account list"))
            .execute("lookup 3 " + playerProfile.getPlayerId())
            .component());
        player.sendMessage(accComp);
    }

    private void displayJoinInfo(ProxiedPlayer player, PlayerProfile playerProfile) {
        player.sendMessage(
            new TextComponent("§7" + BungeeTranslateAPI.translate(player, "First Join") +
                " §8» §e" + BungeeUtil.parseDate(playerProfile.getFirstJoin())));
        player.sendMessage(
            new TextComponent("§7" + BungeeTranslateAPI.translate(player, "Last Join") +
                " §8» §e" + BungeeUtil.parseDate(playerProfile.getLastJoin())));

        long registeredTime = playerProfile.getLastJoin() - playerProfile.getFirstJoin();
        player.sendMessage(
            new TextComponent("§7" + BungeeTranslateAPI.translate(player, "Registered since") +
                " §8» §6" + TimeUtil.beautifyTime(registeredTime, TimeUnit.MILLISECONDS, true)));
    }

    private void displayRankInfo(ProxiedPlayer player, PlayerProfile playerProfile) {
        IPermissionUser permissionUser = CloudNetDriver.getInstance()
            .getPermissionManagement()
            .getUser(playerProfile.getPlayerId());

        if (permissionUser == null) {
            player.sendMessage(new TextComponent("§7Highest Rank §8» §cUnbekannt"));
            return;
        }

        IPermissionGroup permissionGroup = CloudNetDriver.getInstance()
            .getPermissionManagement()
            .getGroup(PlayerRank.valueOf(playerProfile.getRank()).getName());

        long rankTime = getRankExpirationTime(permissionUser, permissionGroup);

        TextComponent rankComp = new TextComponent("§7Highest Rank §8» ");
        String timeInfo = formatRankTime(rankTime);

        rankComp.addExtra(new ChatAction()
            .text(permissionGroup.getDisplay() + permissionGroup.getName() + " §8┃ " + timeInfo)
            .hover("§7Click to show all ranks")
            .execute("lookup 4 " + playerProfile.getPlayerId())
            .component());

        player.sendMessage(rankComp);
    }

    private void displayClanInfo(ProxiedPlayer player, ClanPlayerProfile clanPlayerProfile) {
        TextComponent clanComp = new TextComponent("§7Clan §8» ");

        if (clanPlayerProfile != null) {
            Clan clan = BungeeCore.getAPI().getClanManager()
                .getClanById(clanPlayerProfile.getClanId());
            if (clan != null) {
                clanComp.addExtra(new ChatAction()
                    .text("§6" + clan.getName() + " §8┃ " + clanPlayerProfile.getClanRank()
                        .getFancy())
                    .execute("clan info " + clan.getTag())
                    .hover("§7Click to show clan info")
                    .component());
            } else {
                clanComp.addExtra(new ChatAction().text("§7No clan").component());
            }
        } else {
            clanComp.addExtra(new ChatAction().text("§7No clan").component());
        }

        player.sendMessage(clanComp);
    }

    private void displayPunishmentInfo(ProxiedPlayer player, UUID uuid, BanProfile banProfile,
        MuteProfile muteProfile, PunishHistoryProfile punishHistoryProfile) {
        TextComponent punishComp = new TextComponent("§7Punish §8» ");

        if (banProfile != null) {
            punishComp.addExtra(new ChatAction()
                .text("§4Banned")
                .execute("lookup 1 " + uuid + " ban")
                .hover("§7Click to show ban")
                .component());
        } else if (muteProfile != null) {
            punishComp.addExtra(new ChatAction()
                .text("§cMuted")
                .execute("lookup 1 " + uuid + " mute")
                .hover("§7Click to show mute")
                .component());
        } else {
            punishComp.addExtra(new ChatAction().text("§7No Punish").component());
        }
        player.sendMessage(punishComp);

        displayPunishmentHistory(player, uuid, punishHistoryProfile.getBanProfileMap().size(),
            "Ban", "ban");

        displayPunishmentHistory(player, uuid, punishHistoryProfile.getMuteProfileMap().size(),
            "Mute", "mute");
    }

    private void displayPunishmentHistory(ProxiedPlayer player, UUID uuid, int count,
        String typeName, String typeCommand) {
        TextComponent historyComp = new TextComponent("§7" + typeName + "History §8» ");

        if (count > 0) {
            String color = typeName.equals("Ban") ? "§4" : "§c";
            historyComp.addExtra(new ChatAction()
                .text(color + count + " " + typeName + "(s)")
                .execute("lookup 2 " + uuid + " " + typeCommand)
                .hover("§7Click to show " + typeName + "History")
                .component());
        } else {
            historyComp.addExtra(new TextComponent("§aNo " + typeName + "History"));
        }

        player.sendMessage(historyComp);
    }

    private void displayBanHistory(ProxiedPlayer player, PunishHistoryProfile historyProfile,
        String targetName, Integer page) {
        if (historyProfile.getBanProfileMap().isEmpty()) {
            player.sendMessage(
                new TextComponent(Message.LOOKUP_PREFIX + "§cNo history found about §e" +
                    targetName + "§c!"));
            return;
        }

        player.sendMessage(new TextComponent(Message.LINE_DOWN));
        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent("§7BanHistory of §6" + targetName));

        TextComponent historyComp = new TextComponent("");
        historyProfile.getBanProfileMap().values().stream()
            .sorted((o1, o2) -> Long.compare(o2.getCreateDate(), o1.getCreateDate()))
            .limit(10).skip((page-1)*10L).map(banProfile -> getHistoryEntry(player, banProfile)).forEach(historyEntry -> {
                historyComp.addExtra(historyEntry);
                historyComp.addExtra("\n");
            });
        player.sendMessage(historyComp);
        displayBackButton(player, targetName);
        player.sendMessage(new TextComponent(Message.LINE_DOWN));
    }

    private void displayMuteHistory(ProxiedPlayer player, PunishHistoryProfile historyProfile,
        String targetName, Integer page) {
        if (historyProfile.getMuteProfileMap().isEmpty()) {
            player.sendMessage(
                new TextComponent(Message.LOOKUP_PREFIX + "§cNo history found about §e" +
                    targetName + "§c!"));
            return;
        }

        player.sendMessage(new TextComponent(Message.LINE_DOWN));
        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent("§7MuteHistory of §6" + targetName));

        TextComponent historyComp = new TextComponent("");
        historyProfile.getMuteProfileMap().values().stream()
            .sorted((o1, o2) -> Long.compare(o2.getCreateDate(), o1.getCreateDate()))
            .limit(10).skip((page-1)* 10L).map(muteProfile -> getHistoryEntry(player,
                muteProfile)).forEach(historyEntry -> {
                historyComp.addExtra(historyEntry);
                historyComp.addExtra("\n");
            });
        player.sendMessage(historyComp);
        displayBackButton(player, targetName);
        player.sendMessage(new TextComponent(Message.LINE_DOWN));
    }

    private TextComponent getHistoryEntry(ProxiedPlayer player, Object profile) {
        String date, reason, evidence;
        UUID authorId;

        if (profile instanceof BanProfile banProfile) {
            date = BungeeUtil.parseDate(banProfile.getCreateDate());
            reason = banProfile.getReason();
            authorId = banProfile.getAuthorId();
            evidence = banProfile.getEvidence();
        } else if (profile instanceof MuteProfile muteProfile) {
            date = BungeeUtil.parseDate(muteProfile.getCreateDate());
            reason = muteProfile.getReason();
            authorId = muteProfile.getAuthorId();
            evidence = muteProfile.getEvidence();
        } else {
            return null;
        }

        String author = BungeeCore.getInstance().getPlayerColor(authorId) +
            BungeeCore.getAPI().getUuidManager().getName(authorId);

        TextComponent punishComp = new TextComponent(" §8» §7" + date + " §8┃§7 " + reason +
            " §8┃§7 " + author + " §8┃§7 ");

        String evidenceText = evidence.equalsIgnoreCase("No evidence") ? evidence : "Show Evidence";
        punishComp.addExtra(new ChatAction()
            .text(evidenceText)
            .hover("Copy evidence: " + evidence)
            .suggest(evidence)
            .component());

        return punishComp;
    }

    private void displayPunishmentDetails(ProxiedPlayer player, Object profile, String targetName,
        String type) {
        String author, reason, evidence, until;

        if (profile instanceof BanProfile banProfile) {
            author = BungeeCore.getAPI().getUuidManager().getName(banProfile.getAuthorId());
            reason = banProfile.getReason();
            evidence = banProfile.getEvidence();
            until = BungeeUtil.parseDate(banProfile.getValidUntilDate()) + " §7(" +
                TimeUtil.beautifyTime(banProfile.getDuration(), TimeUnit.MILLISECONDS) + ")";
        } else if (profile instanceof MuteProfile muteProfile) {
            author = BungeeCore.getAPI().getUuidManager().getName(muteProfile.getAuthorId());
            reason = muteProfile.getReason();
            evidence = muteProfile.getEvidence();
            until = BungeeUtil.parseDate(muteProfile.getValidUntilDate()) + " §7(" +
                TimeUtil.beautifyTime(muteProfile.getDuration(), TimeUnit.MILLISECONDS) + ")";
        } else {
            return;
        }

        printPunish(player, targetName, type, author, reason, until, evidence);
    }

    private void displayBackButton(ProxiedPlayer player, String targetName) {
        player.sendMessage(new TextComponent(""));
        player.sendMessage(new ChatAction()
            .text("  §6§lLOOKUP")
            .hover("§7Click back to lookup")
            .execute("lookup " + targetName)
            .component());
        player.sendMessage(new TextComponent(""));
    }

    private PlayerProfile loadPlayerProfile(UUID uuid) {
        return BungeeCore.getAPI().getPlayerService().getEntity(uuid,
            () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(uuid));
    }

    private ClanPlayerProfile loadClanPlayerProfile(UUID uuid) {
        return BungeeCore.getAPI().getClanPlayerService().getEntity(uuid,
            () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(uuid));
    }

    private BanProfile loadBanProfile(UUID uuid) {
        return BungeeCore.getAPI().getBanService().getEntity(uuid,
            () -> BungeeCore.getAPI().getBanService().getRepository().findFirstById(uuid));
    }

    private MuteProfile loadMuteProfile(UUID uuid) {
        return BungeeCore.getAPI().getMuteService().getEntity(uuid,
            () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(uuid));
    }

    private PunishHistoryProfile loadPunishHistoryProfile(UUID uuid) {
        return BungeeCore.getAPI().getPunishHistoryService().getEntity(uuid,
            () -> BungeeCore.getAPI().getPunishHistoryService().getRepository()
                .findFirstById(uuid));
    }

    private String fetchCountryInfo(ProxiedPlayer player, String ip) {
        try (Scanner scanner = new Scanner(new URL(RESTBASE + "holy/vpn/check/" + ip +
            "/adasaisuoa2j2j2j2jnvalkooiwuhlkabvd").openStream())) {

            String response = scanner.useDelimiter("\\A").next();
            JSONObject jsonResponse = new JSONObject(response);

            if (!jsonResponse.isEmpty() && !jsonResponse.isNull("countryname")) {
                return jsonResponse.getString("countryname");
            }
        } catch (IOException e) {
            log.warn("Failed to fetch country info for IP: {}", ip, e);
        }

        return "§c" + BungeeTranslateAPI.translate(player, DEFAULT_COUNTRY);
    }

    private long getRankExpirationTime(IPermissionUser permissionUser,
        IPermissionGroup permissionGroup) {
        return permissionUser.getGroups().stream()
            .filter(group -> group.getGroup().equalsIgnoreCase(permissionGroup.getName()))
            .findFirst()
            .map(PermissionUserGroupInfo::getTimeOutMillis)
            .orElse(0L);
    }

    private String formatRankTime(long timeMillis) {
        if (timeMillis == 0 || timeMillis == -1) {
            return "§aLifetime";
        }
        return "§e" + BungeeUtil.parseDate(timeMillis);
    }

    public void printUsage(CommandSender commandSender) {
        commandSender.sendMessage(new TextComponent(Message.LOOKUP_PREFIX + "§7/lookup (name)"));
    }

    public void printPunish(ProxiedPlayer player, String name, String type, String author, String reason, String until, String evidence) {
        TextComponent punishComp = new TextComponent(Message.LINE_DOWN);
        punishComp.addExtra("\n");
        punishComp.addExtra(new TextComponent("§7" + type + " of §6" + name));
        punishComp.addExtra(new TextComponent("§7Author: §6" + author));
        punishComp.addExtra(new TextComponent("§7Reason: §6" + reason));
        punishComp.addExtra(new TextComponent("§7Until: §6" + until));
        punishComp.addExtra(new ChatAction()
            .text("§7Evidence: §6" + evidence)
            .hover("§7Click to show evidence")
            .url(evidence)
            .component());
        punishComp.addExtra("\n");
        punishComp.addExtra(new ChatAction()
            .text("  §6§lLOOKUP")
            .hover("§7Click back to lookup")
            .execute("lookup " + name)
            .component());
        punishComp.addExtra("\n");
        punishComp.addExtra(new TextComponent(Message.LINE_DOWN));
        player.sendMessage(punishComp);
    }
}