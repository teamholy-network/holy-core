package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.utility.Report;
import org.redisson.api.RMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReportManager {

    private final RMap<String, Report> reportRMap;

    public ReportManager(CoreAPI coreAPI) {
        reportRMap = coreAPI.getRedissonManager().getRedissonClient().getMap("reports");
    }

    public void addReport(Report report) {
        reportRMap.put(report.getTarget().toString(),report);
    }

    public void removeReport(UUID uuid) {
        reportRMap.remove(uuid.toString());
    }

    public boolean isReported(UUID uuid) {
        return reportRMap.containsKey(uuid.toString());
    }

    public Report getReport(UUID target) {
        return reportRMap.get(target.toString());
    }

    public Map<String, Report> getAllReports() {
        if (reportRMap == null) return new HashMap<>();
        return reportRMap;
    }

}
