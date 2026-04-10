package de.teamholy.bungee.login.filter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

	private static final String IP_LOOKUP_URL = "https://insights-api.top/v1/public/ip/lookup?ip=";

	public IPChecker() {
		scheduler.scheduleAtFixedRate(() -> {
			threads.execute(() -> {
				try {
					RestAPIResponse response = RestAPI.getInstance().get(IP_LOOKUP_URL + "0.0.0.0");
					serviceonline = !response.getFailed();
					Thread.sleep(60000);
				} catch (Exception e) {
					serviceonline = false;
				}
			});
		}, 0, 1, TimeUnit.MINUTES);
	}

	public boolean isipresidental(String ip) {
		if (badips.contains(ip)) {
			return false;
		}
		if (serviceonline) {
			IPCheckerResult result = getIPInfo(ip);
			if (result == null) {
				serviceonline = false;
			} else {
				return result.isResidental();
			}
		}
		return true;
	}

	public IPCheckerResult getIPInfo(String ip) {
		if (serviceonline) {
			RestAPIResponse response = RestAPI.getInstance().get(IP_LOOKUP_URL + ip);
			if (response.getFailed()) {
				serviceonline = false;
			} else {
				Gson gson = new Gson();
				JsonObject json = gson.fromJson(response.getText(), JsonObject.class);
				JsonObject data = json.getAsJsonObject("data");
				JsonObject network = data.getAsJsonObject("network");
				JsonObject geo = data.getAsJsonObject("geo");

				IPCheckerResult result = new IPCheckerResult();
				result.setIP(ip);
				result.setCompany(network.has("isp") ? network.get("isp").getAsString() : null);
				result.setASN(network.has("asn") ? parseASN(network.get("asn").getAsString()) : null);
				result.setCity(geo.has("city") ? geo.get("city").getAsString() : null);
				result.setCountry(geo.has("country") ? geo.get("country").getAsString() : null);
				result.setCountryCode(geo.has("country_code") ? geo.get("country_code").getAsString() : null);
				result.setHosting(network.has("hosting") ? network.get("hosting").getAsBoolean() : false);
				result.setProxy(network.has("proxy") ? network.get("proxy").getAsBoolean() : false);
				result.setVPN(network.has("vpn") ? network.get("vpn").getAsBoolean() : false);
				result.setTOR(network.has("tor") ? network.get("tor").getAsBoolean() : false);
				result.setResidental(network.has("residential") ? network.get("residential").getAsBoolean() : false);
				return result;
			}
		}
		return null;
	}

	private Integer parseASN(String asn) {
		if (asn == null || asn.isEmpty()) {
			return null;
		}
		String numeric = asn.replaceAll("[^0-9]", "");
		return numeric.isEmpty() ? null : Integer.parseInt(numeric);
	}

	public void start(Runnable run, long time) {
		scheduler.schedule(() -> threads.execute(run) , time, TimeUnit.MILLISECONDS);
	}

}