package de.teamholy.core.bukkit.manager;

import com.sun.management.OperatingSystemMXBean;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.teamholy.core.bukkit.BukkitCore;

import java.lang.management.ManagementFactory;

public class CloudMessageManager  {

    private BukkitCore bukkitCore;

    private static final long BYTES_TO_MB = 1024 * 1024;

    public CloudMessageManager(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
    }


    public void sendBungeeReport(String channel, String message) {

        long cRamUsageBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long cRamTotalBytes = Runtime.getRuntime().totalMemory();

        long cCpuUsage = (long) getProcessCpuLoad();


        JsonDocument command = new JsonDocument()
            .append("serverName", bukkitCore.getServer().getServerName())
            .append("serverPort", bukkitCore.getServer().getPort())
            .append("serverOnlineCount", bukkitCore.getServer().getOnlinePlayers().size())
            .append("cRamUsageMb", cRamUsageBytes / (BYTES_TO_MB))
            .append("cRamTotalMb", cRamTotalBytes / (BYTES_TO_MB))
            .append("cCpuUsage", cCpuUsage);

        CloudNetDriver.getInstance().getMessenger().sendChannelMessage(channel, message, command);

    }

    private double getProcessCpuLoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
            OperatingSystemMXBean.class);
        return osBean.getProcessCpuLoad() * 100;
    }



}
