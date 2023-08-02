package de.teamholy.core.bungee.listener;

import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.mute.MuteProfile;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.api.utility.Punish;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.ChatFilterManager;
import de.teamholy.core.bungee.util.DiffMatch;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/* copyright by Yassino & Greg */
public class ChatFilterListener implements Listener {

    public static final HashMap<UUID, String> LASTMESSAGES = new HashMap<>();



    DiffMatch diffMatch = new DiffMatch();

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();

        String message = event.getMessage().toLowerCase();
        for (String bannedWord : ChatFilterManager.FILTEREDWORDS.keySet()) {
            String lowerBannedWord = bannedWord.toLowerCase();
            String regex = "\\b" + String.join("[^a-zA-Z]*", lowerBannedWord.split("")) + "\\b";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                ChatFilterManager.FilterActionProfile actionProfile = ChatFilterManager.FILTEREDWORDS.get(bannedWord);

                switch (actionProfile.filterAction()) {
                    case "mute" -> {
                        MuteProfile punishProfile = BungeeCore.getAPI().getMuteService().getEntity(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
                        if (punishProfile != null) {
                            return;
                        } else {
                            event.setCancelled(true);
                            proxiedPlayer.sendMessage("§cChatFilter §8× §7This word is not allowed! §8(§c" + matcher.group() + "§8. §7will be reviewed by our team)");
                            proxiedPlayer.sendMessage("§cChatFilter §8× §7You have been Punished for §c" + Punish.parseMuteReasonById(actionProfile.filterActionId()));
                            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "mute " + proxiedPlayer.getName() + " " + actionProfile.filterActionId());
                        }
                    }
                    case "ban" ->  { ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "ban " + proxiedPlayer.getName() + actionProfile.filterActionId()); event.setCancelled(true); }
                    case "kick" -> { ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "kick " + proxiedPlayer.getName()) ; event.setCancelled(true); }
                    case "warn" -> { proxiedPlayer.sendMessage("§cChatFilter §8× §7This word is not allowed! §8(§c" + matcher.group() + "§8. §7will be reviewed by our team)") ; event.setCancelled(true); }
                }

                DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1136093941612163241/OdV3rYMQtN9wtBU6xcu4IVnQrVPZb5hMveIAmXNFgHynd1JzDxg3QdiD5mzEs8JHyf8-");
                webhook.setAvatarUrl("https://i.imgur.com/k3mtKpE.png");
                webhook.setUsername("ChatFilter");
                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("Chatfilter")
                        .addField(proxiedPlayer.getName() +" wrote", event.getMessage(), true)
                        .addField("may contain", matcher.group(), false)
                        .addField("Server", proxiedPlayer.getServer().getInfo().getName(), false)
                        .addField("Action", actionProfile.filterAction(), false)
                        .setColor(Color.ORANGE)
                        .setThumbnail("https://visage.surgeplay.com/face/512/" + proxiedPlayer.getUniqueId().toString() + ".png")
                        .setFooter("TeamHolyDE", "https://i.imgur.com/0w7sO7f.png"));


                webhook.execute();

                return;
            }
        }


        if (event.getMessage().startsWith("/")) return;

        if (proxiedPlayer.hasPermission("teamholy.team") || proxiedPlayer.hasPermission("teamholy.perk.holy")) return;

        if (LASTMESSAGES.get(proxiedPlayer.getUniqueId()) == null) {
            LASTMESSAGES.put(proxiedPlayer.getUniqueId(),event.getMessage());
            return;
        }

        if (areMessagesEquals(event.getMessage(),LASTMESSAGES.get(proxiedPlayer.getUniqueId()))) {
            proxiedPlayer.sendMessage("§cChatFilter §8× §7Your last message is 70% similar");
            event.setCancelled(true);
        }

        LASTMESSAGES.put(proxiedPlayer.getUniqueId(),event.getMessage());

    }



    public boolean areMessagesEquals(String lastMessage, String message) {

        int differences = diffMatch.diff_levenshtein(diffMatch.diff_main(lastMessage, message));
        int longestMessageSize = Math.max(lastMessage.length(), message.length());

        return (differences * 100) / longestMessageSize < 30;
    }

}
