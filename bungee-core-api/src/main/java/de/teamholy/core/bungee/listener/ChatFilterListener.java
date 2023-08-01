package de.teamholy.core.bungee.listener;

import de.teamholy.core.bungee.manager.ChatFilterManager;
import de.teamholy.core.bungee.util.DiffMatch;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.UUID;


/* copyright by Yassino & Greg */
public class ChatFilterListener implements Listener {

    public static final HashMap<UUID, String> LASTMESSAGES = new HashMap<>();



    DiffMatch diffMatch = new DiffMatch();

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();

        String[] filteredwords = event.getMessage().toLowerCase().split("\\s+");
        for (String word : filteredwords) {
            if (ChatFilterManager.FILTEREDWORDS.containsKey(word.toLowerCase())) {
                proxiedPlayer.sendMessage("§cChatFilter §8× §7This word is not allowed! §8(§c" + word + "§8. §7will be reviewed by our team)");
                event.setCancelled(true);
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
