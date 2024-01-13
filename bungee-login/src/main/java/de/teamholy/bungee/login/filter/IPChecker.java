package de.teamholy.bungee.login.filter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import de.teamholy.bungee.login.api.RestAPI;
import de.teamholy.bungee.login.api.RestAPIResponse;
import lombok.Getter;
import lombok.Setter;

public class IPChecker {

	@Setter
    @Getter
    private static IPChecker Instance = new IPChecker();

	@Getter
	private boolean serviceonline = false;

	Set<String> badips = ConcurrentHashMap.newKeySet();

	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	ExecutorService threads = Executors.newCachedThreadPool();

	public IPChecker() {
		scheduler.scheduleAtFixedRate(() -> {
			threads.execute(() -> {
				try {
					RestAPIResponse ipcheckeralive = RestAPI.getInstance().get("http://ipcheck.skydb.de/alive");
					if (ipcheckeralive.getFailed()) {
						serviceonline = false;
					} else {
						serviceonline = true;
					}
					Thread.sleep(60000);
				} catch (Exception e) {
				}
			});
		}, 0, 1, TimeUnit.MINUTES);
	}

	public boolean isipresidental(String ip) {
		if (badips.contains(ip)) {
			return false;
		}
		if (serviceonline) {
			RestAPIResponse isipresidental = RestAPI.getInstance().get("http://ipcheck.skydb.de/residental?ip=" + ip);
			if (isipresidental.getFailed()) {
				serviceonline = false;
			} else {
				if (isipresidental.getText().contains("false")) {
					badips.add(ip);
					return false;
				} else {
					return true;
				}
			}
		}
		return true;
	}

	public IPCheckerResult getIPInfo(String ip) {
		if (serviceonline) {
			RestAPIResponse getIPInfo = RestAPI.getInstance().get("http://ipcheck.skydb.de/getinfo?ip=" + ip);
			if (getIPInfo.getFailed()) {
				serviceonline = false;
			} else {
				Gson gson = new Gson();
				return gson.fromJson(getIPInfo.getText(), IPCheckerResult.class);
			}

		}
		return null;
	}

	public void start(Runnable run, long time) {
		scheduler.schedule(() -> threads.execute(run) , time, TimeUnit.MILLISECONDS);
	}

}
