package de.teamholy.core.bungee.listener;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class RabbitQueueListener {


    BungeeCore bungeeCore;

    public RabbitQueueListener(BungeeCore bungeeCore) {
        this.bungeeCore = bungeeCore;
        listen();
    }

    public void listen() {

        String queue = ProxyServer.getInstance().getName().startsWith("TestProxy") ? "bungee.test.default" : "bungee.main.default";

        this.bungeeCore.getCoreAPI().getRabbit().subscribeRabbitQueue(queue, (consumerTag, delivery) -> {


            JsonDocument document = JsonDocument.newDocument(delivery.getBody());

            ProxyServer.getInstance().getPlayer("next_js").sendMessage(ChatColor.GREEN + "rabbit: " + document);

            String type = document.getString("type");

            if (type == null) {
                return;
            }

            switch (type) {
                case "link":

            }

        });
    }

    private ProxiedPlayer getPlayer(String name) {
       return ProxyServer.getInstance().getPlayer(name);
    }

}
