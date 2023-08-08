package de.teamholy.core.bungee.util;

import com.sun.management.OperatingSystemMXBean;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import net.md_5.bungee.api.ProxyServer;

import java.lang.management.ManagementFactory;

/* copyright by Greg */
public class Helpers {

    private static final long BYTES_TO_MB = 1024 * 1024;

    public String centerMessage(String message) {
        String stripped = message.replaceAll("§.", "");
        int length = stripped.length();
        int space = (60 - length) / 2;

        return repeat(" ", Math.max(0, space)) + message;
    }

    public JsonDocument getMetrics(ProxyServer proxyServer) {
        long cRamUsageBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long cRamTotalBytes = Runtime.getRuntime().totalMemory();
        long cCpuUsage = (long) getProcessCpuLoad();

        return new JsonDocument()
            .append("serverName", CloudNetDriver.getInstance().getComponentName())
            .append("serverPort", proxyServer.getConfig().getListeners().iterator().next().getHost().getPort())
            .append("serverOnlineCount", proxyServer.getPlayers().size())
            .append("cRamUsageMb", cRamUsageBytes / (BYTES_TO_MB))
            .append("cRamTotalMb", cRamTotalBytes / (BYTES_TO_MB))
            .append("cCpuUsage", cCpuUsage);
    }

    private String repeat(String str, int times) {
        return new String(new char[times]).replace("\0", str);
    }

    private double getProcessCpuLoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
            OperatingSystemMXBean.class);
        return osBean.getProcessCpuLoad() * 100;
    }
}

