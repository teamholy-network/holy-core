package de.teamholy.bungee.login.manager;

import de.teamholy.bungee.login.api.TaskAPI;

import java.util.concurrent.TimeUnit;

public class BotManager {

	private static int registers = 0;

	private static int loggins = 0;
	
	public static void init() {
		TaskAPI.runScheduledAtFixedRate(() -> {
			registers = 0;
			loggins = 0;
		}, 10, 10, TimeUnit.MINUTES);
	}

	public static boolean isBotProtectionEnableRegister() {
		return true;
        //registers >= 5;
	}

	public static boolean isBotProtectionEnableLogin() {
		return true;
        //loggins >= 10;
	}
	
	public static void register() {
		registers += 1;
	}
	
	public static void loggin() {
		loggins += 1;
	}
}
