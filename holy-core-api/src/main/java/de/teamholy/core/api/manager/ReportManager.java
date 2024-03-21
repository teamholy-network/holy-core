package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.utility.Report;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.redisson.api.RMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
@Getter
public class ReportManager {

    RMap<UUID, Report> reportRMap;

    public ReportManager(CoreAPI coreAPI) {
        reportRMap = coreAPI.getRedissonManager().getRedissonClient().getMap("reports");
    }

    public void addReport(Report report) {
        reportRMap.put(report.getTarget(), report);
    }

    public void removeReport(UUID uuid) {
        reportRMap.remove(uuid);
    }

    public boolean isReported(UUID uuid) {
        return reportRMap.containsKey(uuid);
    }

    public Report getReport(UUID target) {
        return reportRMap.get(target);
    }

    public Map<UUID, Report> getAllReports() {
        if (reportRMap == null) return new HashMap<>();
        return reportRMap;
    }
}
