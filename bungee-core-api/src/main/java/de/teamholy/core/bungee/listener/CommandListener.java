package de.teamholy.core.bungee.listener;

//import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class CommandListener implements Listener {

    public static final HashMap<UUID, Long> COOLDOWNS = new HashMap<>();

    @EventHandler
    public void onCommand(ChatEvent event) {
        if (!event.getMessage().startsWith("/")) return;
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();
        if (COOLDOWNS.containsKey(proxiedPlayer.getUniqueId()) && COOLDOWNS.get(proxiedPlayer.getUniqueId()) > System.currentTimeMillis()) {
            proxiedPlayer.sendMessage("§c" + "Dont spam commands!");
            event.setCancelled(true);
            return;
        }
        COOLDOWNS.remove(proxiedPlayer.getUniqueId());
        if (event.getMessage().startsWith("/login") || event.getMessage().startsWith("/register")) return;
        if (event.getMessage().toLowerCase().startsWith("/execute")) event.setCancelled(true);

        DiscordWebhook discordWebhook = new DiscordWebhook("https://discord.com/api/webhooks/1061719912730603530/zb7iKpRkLfk0Th9EcfTiBhd1LJ5gI5AV99s3n9aIuOQGrCdalU7QsIjj_YXdtgYpE8Jn");
        discordWebhook.setUsername("command");
        discordWebhook.addEmbed(new DiscordWebhook.EmbedObject()
            .setColor(Color.RED)
            .setDescription("[" + proxiedPlayer.getServer().getInfo().getName() + "] " + proxiedPlayer.getName() + ": " + event.getMessage())
            .setFooter(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now()), null));

        BungeeCore.getAPI().getExecutor().submit(discordWebhook::execute);

        COOLDOWNS.put(proxiedPlayer.getUniqueId(), System.currentTimeMillis() + (TimeUnit.SECONDS.toMillis(1) / 2));
    }

}
