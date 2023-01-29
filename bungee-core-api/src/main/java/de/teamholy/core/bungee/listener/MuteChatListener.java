package de.teamholy.core.bungee.listener;

import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.mute.MuteProfile;
import de.teamholy.core.api.utility.PunishType;
import de.teamholy.core.api.entities.punishhistory.PunishHistoryProfile;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BanUtil;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/* copyright by Yassino */
public class MuteChatListener implements Listener {


    public MuteChatListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            if (!event.isCancelled()) {
                String message = event.getMessage();


                if (!message.startsWith("/")) {
                    if (!BungeeUtil.hasPermission(player, "teamholy.mute.bypass")) {

                        MuteProfile punishProfile = BungeeCore.getAPI().getMuteService().getEntity(player.getUniqueId(),
                                () -> BungeeCore.getAPI().getMuteService().getRepository().findFirstById(player.getUniqueId()));

                        if (punishProfile != null) {
                            if (punishProfile.active()) {
                                event.setCancelled(true);
                                player.sendMessage(BanUtil.generateMuteChatMessage(punishProfile));
                            } else {


                                PunishHistoryProfile punishHistoryProfile = BungeeCore.getAPI().getPunishHistoryService().getEntity(player.getUniqueId(), () -> BungeeCore.getAPI().getPunishHistoryService().getRepository().findFirstById(player.getUniqueId()));
                                if (punishHistoryProfile == null)
                                    punishHistoryProfile = new PunishHistoryProfile();

                                punishHistoryProfile.getMuteProfileMap().put(UUID.randomUUID().toString(),punishProfile);

                                BungeeCore.getAPI().getPunishHistoryService().saveEntity(punishHistoryProfile,true,true);
                                BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(BanUtil.generateUnmuteMessage("Console", punishProfile));
                                BungeeCore.getInstance().getCoreAPI().getMuteService().deleteEntity(punishProfile);

                            }
                        }
                    }
                }
            }
        }
    }

}
