package de.teamholy.core.bungee.listener;

import com.google.common.collect.Lists;
//import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.entities.mute.MuteProfile;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.api.utility.Punish;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.ChatFilterManager;
import de.teamholy.core.bungee.manager.ChatLogManager;
import de.teamholy.core.bungee.manager.LensRedisManager;
import de.teamholy.core.bungee.util.BanUtil;
import de.teamholy.core.bungee.util.DiffMatch;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.teamholy.core.bungee.util.BungeeUtil;

/**
 * The ChatFilterListener class is responsible for monitoring and managing chat activity on the server.
 * It enforces rules related to chat filtering, including but not limited to banned words, spam prevention,
 * domain blacklisting, and user command imitation. Actions like mute, ban, kick, or warning are handled
 * based on violations detected in chat messages.
 *
 * This class also integrates with third-party services (e.g., Discord) for notification purposes regarding chat violations.
 * Additionally, it leverages specific bypass rules and spam detection measures to enhance the chat moderation system.
 *
 * Fields:
 * - BYPASSED_DOMAINS: A set of domains exempted from being filtered or flagged.
 * - IMITATE_BLACKLIST: A list of commands or phrases restricted in the chat.
 * - MESSAGE_SIMILARITY_THRESHOLD: A threshold to determine if two messages are similar, aiding in spam detection.
 * - DISCORD_WEBHOOK_URL: The endpoint URL used for sending Discord webhook notifications.
 * - bungeeCore: The core server plugin instance.
 * - lensRedisManager: Manages Redis-based communication or storage for chat filtering.
 * - diffMatch: Facilitates text comparison for detecting message similarity.
 * - domains: A collection of domains loaded for filtering.
 * - domainPattern: The regex pattern used to identify domain-like strings in messages.
 * - lastMessages: Keeps track of recent messages sent by players for spam detection.
 *
 * Constructor:
 * - ChatFilterListener(BungeeCore bungeeCore): Initializes the ChatFilterListener with the main plugin instance.
 *
 * Methods:
 * - onChat(ChatEvent event): Responds to chat events, processes messages, and applies filtering or moderation based on configured rules.
 * - handleImitateCommand(ProxiedPlayer player, String message, ChatEvent event): Handles cases where players attempt to imitate commands in chat.
 * - handleBannedWords(ProxiedPlayer player, String message, ChatEvent event): Detects banned words in chat and applies appropriate actions.
 * - shouldBypassBannedWordCheck(ProxiedPlayer player, String message): Determines if a player should bypass the banned word check.
 * - createWordMatcher(String bannedWord, String message): Creates a matcher to identify occurrences of banned words in the chat message.
 * - isPlayerName(String name): Checks if the provided string matches a player name.
 * - processBannedWordViolation(ProxiedPlayer player, ChatEvent event, Matcher matcher, ChatFilterManager.FilterActionProfile actionProfile): Handles events where a player violates
 *  the banned words rule.
 * - handleMuteAction(ProxiedPlayer player, ChatEvent event, Matcher matcher, ChatFilterManager.FilterActionProfile actionProfile): Executes the mute action on players violating chat
 *  rules.
 * - logToChatHistory(ProxiedPlayer player, String message, String bannedWord): Logs chat messages violating the rules for audit purposes.
 * - handleBanAction(ProxiedPlayer player, ChatEvent event, Matcher matcher, ChatFilterManager.FilterActionProfile actionProfile): Executes the ban action on players violating chat
 *  rules.
 * - handleKickAction(ProxiedPlayer player, ChatEvent event, Matcher matcher, ChatFilterManager.FilterActionProfile actionProfile): Executes the kick action on players violating chat
 *  rules.
 * - handleWarnAction(ProxiedPlayer player, ChatEvent event, Matcher matcher, ChatFilterManager.FilterActionProfile actionProfile): Issues a warning to players violating chat rules
 * .
 * - handleDomainFilter(ProxiedPlayer player, String message, ChatEvent event): Handles filtering of blacklisted or unapproved domains from chat messages.
 * - removePlayerNamesFromMessage(String message): Strips player names from the chat message to avoid false positives during content checks.
 * - isDomainWhitelisted(String domain): Determines whether a domain is whitelisted and exempt from filtering.
 * - handleSpamPrevention(ProxiedPlayer player, String message, ChatEvent event): Detects and handles spam-like behavior in chat messages.
 * - areMessagesSimilar(String message1, String message2): Compares two messages to determine if they are similar based on predefined thresholds.
 * - sendDiscordWebhookChatfilter(ProxiedPlayer player, ChatEvent event, Matcher matcher, ChatFilterManager.FilterActionProfile actionProfile): Sends a Discord webhook notification
 *  for chat filter violations.
 * - sendDiscordWebhookDomainfilter(ProxiedPlayer player, ChatEvent event, Matcher matcher): Sends a Discord webhook notification for domain filter violations.
 * - loadDomains(): Loads the list of domains for filtering from the appropriate source.
 */
public class ChatFilterListener implements Listener {

    private static final String[] BYPASSED_DOMAINS = {"teamholy.de", "teamholy.net", "teamholy.top"};
    private static final String[] IMITATE_BLACKLIST = {"2sa", "beide"};
    private static final int MESSAGE_SIMILARITY_THRESHOLD = 30;
    private static final String DISCORD_WEBHOOK_URL = "https://discord.com/api/webhooks/1136093941612163241/OdV3rYMQtN9wtBU6xcu4IVnQrVPZb5hMveIAmXNFgHynd1JzDxg3QdiD5mzEs8JHyf8-";

    public static final Map<UUID, String> LASTMESSAGES = new ConcurrentHashMap<>();

    private final BungeeCore bungeeCore;
    private final LensRedisManager lensRedisManager;
    private final DiffMatch diffMatch;
    private final Set<String> domains;
    private final Pattern domainPattern;

    public ChatFilterListener(BungeeCore bungeeCore) {
        this.bungeeCore = bungeeCore;
        this.lensRedisManager = bungeeCore.getLensRedisManager();
        this.diffMatch = new DiffMatch();
        this.domains = ConcurrentHashMap.newKeySet();
        this.domainPattern = Pattern.compile("([a-zA-Z0-9-]+)[.,;⦁!@#$%^&*()_+=|<>?{}\\[\\]\\-]([a-zA-Z0-9-]+)");

        ProxyServer.getInstance().getPluginManager().registerListener(bungeeCore, this);
        loadDomains();
    }

    @EventHandler(priority = 64)
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();

        if (handleImitateCommand(player, message, event)) {
            return;
        }

        if (handleBannedWords(player, message, event)) {
            return;
        }

        if (handleDomainFilter(player, message, event)) {
            return;
        }

        handleSpamPrevention(player, message, event);
    }

    private boolean handleImitateCommand(ProxiedPlayer player, String message, ChatEvent event) {
        if (!message.toLowerCase().startsWith("/imitate")) {
            return false;
        }

        String[] args = message.split(" ");
        if (args.length < 2) {
            return false;
        }

        String targetName = args[1].toLowerCase(Locale.ROOT);

        if (Arrays.stream(IMITATE_BLACKLIST).anyMatch(blacklisted -> blacklisted.equalsIgnoreCase(targetName))) {
            String banMessage = "§c" + "You have been banned for imitating a staff member";
            player.disconnect(new TextComponent(banMessage));
            event.setCancelled(true);
            return true;
        }

        return false;
    }

    private boolean handleBannedWords(ProxiedPlayer player, String message, ChatEvent event) {
        if (shouldBypassBannedWordCheck(player, message)) {
            return false;
        }

        String lowerMessage = message.toLowerCase();

        for (Map.Entry<String, ChatFilterManager.FilterActionProfile> entry : ChatFilterManager.FILTEREDWORDS.entrySet()) {
            String bannedWord = entry.getKey();
            Matcher matcher = createWordMatcher(bannedWord, lowerMessage);

            if (matcher.find()) {
                if (isPlayerName(matcher.group())) {
                    continue;
                }

                ChatFilterManager.FilterActionProfile actionProfile = entry.getValue();
                processBannedWordViolation(player, event, matcher, actionProfile);
                return true;
            }
        }

        return false;
    }

    private boolean shouldBypassBannedWordCheck(ProxiedPlayer player, String message) {
        if (player.hasPermission("teamholy.team")) {
            return true;
        }

        return message.startsWith("/") && !BanUtil.isFilteredCommand(message);
    }

    private Matcher createWordMatcher(String bannedWord, String message) {
        String lowerBannedWord = bannedWord.toLowerCase();
        String regex = "\\b" + String.join("[^a-zA-Z]*", lowerBannedWord.split("")) + "\\b";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(message);
    }

    private boolean isPlayerName(String name) {
        return ProxyServer.getInstance().getPlayer(name) != null;
    }

    private void processBannedWordViolation(ProxiedPlayer player, ChatEvent event,
                                            Matcher matcher, ChatFilterManager.FilterActionProfile actionProfile) {
        event.setCancelled(true);

        switch (actionProfile.filterAction()) {
            case "mute" -> handleMuteAction(player, event, matcher, actionProfile);
            case "ban" -> handleBanAction(player, event, matcher, actionProfile);
            case "kick" -> handleKickAction(player, event, matcher, actionProfile);
            case "warn" -> handleWarnAction(player, event, matcher, actionProfile);
        }

        sendDiscordWebhookChatfilter(player, event, matcher, actionProfile);
    }

    private void handleMuteAction(ProxiedPlayer player, ChatEvent event, Matcher matcher,
                                  ChatFilterManager.FilterActionProfile actionProfile) {
        MuteProfile muteProfile = BungeeCore.getAPI().getMuteService().getEntity(
            player.getUniqueId(),
            () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(player.getUniqueId())
        );

        if (muteProfile != null) {
            return;
        }

        logToChatHistory(player, event.getMessage(), matcher.group());

        player.sendMessage(new TextComponent("§cChatFilter §8× §7" + "This word is not allowed!"
            + " §8(§c" + matcher.group() + "§8. §7" + "will be reviewed by our team" + ")"));
        player.sendMessage(new TextComponent("§cChatFilter §8× §7" + BungeeUtil.format("You have been Punished for {}", "§c" + Punish.parseMuteReasonById(actionProfile.filterActionId()))));

        ProxyServer.getInstance().getPluginManager().dispatchCommand(
            ProxyServer.getInstance().getConsole(),
            "mute " + player.getName() + " " + actionProfile.filterActionId()
        );
    }

    private void logToChatHistory(ProxiedPlayer player, String message, String bannedWord) {
        LinkedList<ChatLogManager.Message> chatlog = ChatLogManager.CHATLOGS.getOrDefault(
            player.getUniqueId(),
            Lists.newLinkedList()
        );

        if (chatlog.stream().noneMatch(msg -> msg.message().contains(bannedWord))) {
            chatlog.add(new ChatLogManager.Message(
                message,
                player.getServer().getInfo().getName(),
                System.currentTimeMillis()
            ));
            ChatLogManager.CHATLOGS.put(player.getUniqueId(), chatlog);
        }
    }

    private void handleBanAction(ProxiedPlayer player, ChatEvent event, Matcher matcher,
                                 ChatFilterManager.FilterActionProfile actionProfile) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(
            ProxyServer.getInstance().getConsole(),
            "ban " + player.getName() + actionProfile.filterActionId()
        );
    }

    private void handleKickAction(ProxiedPlayer player, ChatEvent event, Matcher matcher,
                                  ChatFilterManager.FilterActionProfile actionProfile) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(
            ProxyServer.getInstance().getConsole(),
            "kick " + player.getName()
        );
    }

    private void handleWarnAction(ProxiedPlayer player, ChatEvent event, Matcher matcher,
                                  ChatFilterManager.FilterActionProfile actionProfile) {
        player.sendMessage(new TextComponent("§cChatFilter §8× §7" + "This word is not allowed!"
            + " §8(§c" + matcher.group() + "§8. §7" + "will be reviewed by our team" + ")"));
    }

    private boolean handleDomainFilter(ProxiedPlayer player, String message, ChatEvent event) {
        String filteredMessage = removePlayerNamesFromMessage(message.toLowerCase());

        if (filteredMessage.isEmpty()) {
            return false;
        }

        Matcher domainMatcher = domainPattern.matcher(filteredMessage);

        if (!domainMatcher.find()) {
            return false;
        }

        if (isDomainWhitelisted(domainMatcher.group(0))) {
            return false;
        }

        String tld = domainMatcher.group(2);

        if (domains.contains(tld)) {
            MuteProfile muteProfile = BungeeCore.getAPI().getMuteService().getEntity(
                player.getUniqueId(),
                () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(player.getUniqueId())
            );

            if (muteProfile != null) {
                return false;
            }

            player.sendMessage(new TextComponent("§cChatFilter §8× §7" + "This word is not allowed!"
                + " §8(§c" + domainMatcher.group(0) + "§8. §7" + "will be reviewed by our team" + ")"));

            event.setCancelled(true);
            sendDiscordWebhookDomainfilter(player, event, domainMatcher);
            return true;
        }

        return false;
    }

    private String removePlayerNamesFromMessage(String message) {
        String result = message;

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            String playerName = player.getName().toLowerCase();
            if (result.contains(playerName)) {
                result = result.replace(playerName, "");
            }
        }

        return result.trim();
    }

    private boolean isDomainWhitelisted(String domain) {
        return Arrays.stream(BYPASSED_DOMAINS)
            .anyMatch(whitelisted -> domain.toLowerCase().endsWith(whitelisted.toLowerCase()));
    }

    private void handleSpamPrevention(ProxiedPlayer player, String message, ChatEvent event) {
        if (message.startsWith("/")) {
            return;
        }

        if (player.hasPermission("teamholy.team") || player.hasPermission("teamholy.perk.holy")) {
            return;
        }

        String lastMessage = LASTMESSAGES.get(player.getUniqueId());

        if (lastMessage == null) {
            LASTMESSAGES.put(player.getUniqueId(), message);
            lensRedisManager.addMessage(player.getUniqueId(), message, player.getDisplayName());
            return;
        }

        if (areMessagesSimilar(message, lastMessage)) {
            player.sendMessage(new TextComponent("§cChatFilter §8× §7" + "Your last message is 70% similar"));
            event.setCancelled(true);
            return;
        }

        LASTMESSAGES.put(player.getUniqueId(), message);
        lensRedisManager.addMessage(player.getUniqueId(), message, player.getDisplayName());
    }

    private boolean areMessagesSimilar(String message1, String message2) {
        int differences = diffMatch.diff_levenshtein(diffMatch.diff_main(message1, message2));
        int longestMessageSize = Math.max(message1.length(), message2.length());
        int similarityPercentage = (differences * 100) / longestMessageSize;

        return similarityPercentage < MESSAGE_SIMILARITY_THRESHOLD;
    }

    private void sendDiscordWebhookChatfilter(ProxiedPlayer player, ChatEvent event,
                                              Matcher matcher, ChatFilterManager.FilterActionProfile actionProfile) {
        bungeeCore.getProxy().getScheduler().runAsync(bungeeCore, () -> {
            DiscordWebhook webhook = new DiscordWebhook(DISCORD_WEBHOOK_URL);
            webhook.setAvatarUrl("https://i.imgur.com/k3mtKpE.png");
            webhook.setUsername("ChatFilter");
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("Chatfilter")
                .addField(player.getName() + " wrote", event.getMessage(), true)
                .addField("may contain", matcher.group(), false)
                .addField("Server", player.getServer().getInfo().getName(), false)
                .addField("Action", actionProfile.filterAction(), false)
                .setColor(Color.ORANGE)
                .setThumbnail("https://visage.surgeplay.com/face/512/" + player.getUniqueId() + ".png")
                .setFooter("TeamHolyDE", "https://i.imgur.com/0w7sO7f.png")
            );
            webhook.execute();
        });
    }

    private void sendDiscordWebhookDomainfilter(ProxiedPlayer player, ChatEvent event, Matcher matcher) {
        bungeeCore.getProxy().getScheduler().runAsync(bungeeCore, () -> {
            DiscordWebhook webhook = new DiscordWebhook(DISCORD_WEBHOOK_URL);
            webhook.setAvatarUrl("https://i.imgur.com/k3mtKpE.png");
            webhook.setUsername("ChatFilter");
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("Chatfilter")
                .addField(player.getName() + " wrote", event.getMessage(), true)
                .addField("may contain", matcher.group(), false)
                .addField("Server", player.getServer().getInfo().getName(), false)
                .addField("Action", "Warn", false)
                .setColor(Color.ORANGE)
                .setThumbnail("https://visage.surgeplay.com/face/512/" + player.getUniqueId() + ".png")
                .setFooter("TeamHolyDE", "https://i.imgur.com/0w7sO7f.png")
            );
            webhook.execute();
        });
    }

    private void loadDomains() {
        bungeeCore.getProxy().getScheduler().runAsync(bungeeCore, () -> {
            bungeeCore.getLogger().info("Loading domains for domain filter...");

            try {
                URL url = new URL("https://data.iana.org/TLD/tlds-alpha-by-domain.txt");
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim().toLowerCase();
                    if (!line.startsWith("#") && !line.isEmpty()) {
                        domains.add(line);
                    }
                }

                scanner.close();
                bungeeCore.getLogger().info("Loaded " + domains.size() + " domains for filtering");
            } catch (IOException e) {
                bungeeCore.getLogger().severe("Failed to load domains for domain filter: " + e.getMessage());
            }
        });
    }
}