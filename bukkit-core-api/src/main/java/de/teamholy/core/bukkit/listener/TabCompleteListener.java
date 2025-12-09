package de.teamholy.core.bukkit.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

/* copyright by Yassino */
public class TabCompleteListener extends PacketAdapter {

    public TabCompleteListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    public void onPacketReceiving(PacketEvent event) {
        PacketType packetType = event.getPacketType();
        if (!packetType.equals(PacketType.Play.Client.TAB_COMPLETE))
            return;
        PacketContainer packetContainer = event.getPacket();
        String message = (packetContainer.getSpecificModifier(String.class).read(0)).toLowerCase();

        if (message.equals("/") || message.contains(":"))
            event.setCancelled(true);

        for (String blockedCommands : CommandListener.blockedCommands) {
            if (message.toLowerCase(Locale.ROOT).startsWith("/" + blockedCommands)
                    && !event.getPlayer().hasPermission("teamholy.plugins")) {
                event.setCancelled(true);
            }
        }

        if (message.startsWith("/vulcan") && !event.getPlayer().hasPermission("teamholy.anticheat"))
            event.setCancelled(true);

    }

}
