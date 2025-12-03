package de.teamholy.core.bungee.listener;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.teamholy.core.api.CoreAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * The CloudMessageListener class listens to messages received via CloudNet's messaging system
 * and handles specific message types such as metric reporting and command execution.
 * It registers itself as an event listener to the CloudNet event manager when instantiated.
 */
public record CloudMessageListener(CoreAPI coreAPI) {

    private static final String MESSAGE_OHIO_REPORT = "ohio:report";
    private static final String MESSAGE_COMMAND = "command";

    public CloudMessageListener(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        CloudNetDriver.getInstance().getEventManager().registerListener(this);
    }

    @EventListener
    public void onListen(ChannelMessageReceiveEvent event) {
        if (event.getMessage() == null) {
            return;
        }

        String message = event.getMessage();

        if (MESSAGE_OHIO_REPORT.equals(message)) {
            handleMetricReport(event.getData());
        } else if (MESSAGE_COMMAND.equalsIgnoreCase(message)) {
            handleCommandExecution(event.getData());
        }
    }

    private void handleMetricReport(JsonDocument data) {
        if (data == null) {
            return;
        }

        coreAPI.getMetricsManager().saveMetric(data);
    }

    private void handleCommandExecution(JsonDocument data) {
        if (data == null) {
            return;
        }

        String command = data.getString("command");

        if (command == null || command.isEmpty()) {
            return;
        }

        UUID targetUuid = data.get("uuid", UUID.class);

        if (targetUuid != null) {
            executeCommandAsPlayer(targetUuid, command);
        } else {
            executeCommandAsConsole(command);
        }
    }

    private void executeCommandAsPlayer(UUID playerId, String command) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerId);

        if (player == null) {
            return;
        }

        ProxyServer.getInstance().getPluginManager().dispatchCommand(player, command);
    }

    private void executeCommandAsConsole(String command) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(
            ProxyServer.getInstance().getConsole(),
            command
        );
    }
}