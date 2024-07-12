package de.teamholy.core.api.manager;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.api.CoreAPI;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.redisson.api.RMap;
import org.redisson.codec.JsonJacksonCodec;

import java.util.Map;
import java.util.HashMap;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class MetricsManager {

    RMap<String, Map<String, String>> metricsCollection;

    CoreAPI coreAPI;

    public MetricsManager(CoreAPI coreAPI) {
        metricsCollection = coreAPI.getRedissonManager().getRedissonClient().getMap("metrics_collection", new JsonJacksonCodec());
        this.coreAPI = coreAPI;

    }

    public void saveMetric(JsonDocument jsonDocument) {
        if (coreAPI == null) {
            System.out.println("coreAPI is null");
            return;
        }
        if (coreAPI.getRedissonManager().getRedissonClient().isShutdown()) {
            return;
        }
        if (metricsCollection == null) {
            System.out.println("metricsCollection is null");
            return;
        }

        if (jsonDocument.isEmpty()) {
            return;
        }

        Map<String, String> serverMetrics = new HashMap<>();

        String serverName = jsonDocument.getString("serverName");
        serverMetrics.put("serverPort", jsonDocument.getString("serverPort"));
        serverMetrics.put("serverOnlineCount", jsonDocument.getString("serverOnlineCount"));
        serverMetrics.put("cRamUsageMb", jsonDocument.getString("cRamUsageMb"));
        serverMetrics.put("cRamTotalMb", jsonDocument.getString("cRamTotalMb"));
        serverMetrics.put("cCpuUsage", jsonDocument.getString("cCpuUsage"));

        metricsCollection.fastPut(serverName, serverMetrics);
    }

    public void removeMetric(String serverName) {
        try {
            metricsCollection.fastRemove(serverName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
