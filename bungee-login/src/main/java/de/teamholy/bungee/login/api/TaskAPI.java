package de.teamholy.bungee.login.api;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskAPI {

    private static ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void runAsync(Runnable run) {
        threads.execute(() -> {
            try {
                run.run();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        });
    }

    public static void runScheduled(Runnable run, long delay, TimeUnit unit) {
        scheduler.schedule(() -> runAsync(run), delay, unit);
    }

    public static void runScheduledAtFixedRate(Runnable run, long firstdelay, long delay, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(() -> runAsync(run), firstdelay, delay, unit);
    }

    public static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
