package de.teamholy.bungee.login.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import de.teamholy.bungee.login.BungeeLogin;
import de.teamholy.bungee.login.api.TaskAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CaptchaManager {

    public static Set<Captcha> list = ConcurrentHashMap.newKeySet();

//	public static Set<Captcha> capchaready = ConcurrentHashMap.newKeySet();

    public static void init() {
        TaskAPI.runScheduledAtFixedRate(() -> {
//			list.removeAll(list.stream().filter(c -> !c.player.isConnected()).collect(Collectors.toList()));
            list.parallelStream().forEach(captcha -> {
                if (captcha.getPlayer().isConnected()) {
                    sendAsyncHttpRequest(captcha.getCheckurl()).thenAccept(result -> {
                        if (result.contains("true")) {
                            captcha.player.sendMessage(TextComponent.fromLegacyText(BungeeLogin.PREFIX + "§aCaptcha Solved"));
                            list.remove(captcha);
                        } else {
                            sendCaptchaMessage(captcha);
                        }
                    });
                } else {
                    list.remove(captcha);
                }
            });
        }, 5, 5, TimeUnit.SECONDS);
    }

    public static CompletableFuture<Optional<Captcha>> createCaptcha(ProxiedPlayer player) {
        String urlString = "http://164.132.57.107:3000/holy/captcha/get/" + player.getName().toLowerCase(Locale.ROOT) + "/adasaisuoa2j2j2j2jnvasvcxds43efglkooiwuhlkabvd";
        Captcha captcha = new Captcha("", "", player);
        return CompletableFuture.supplyAsync(() -> {
            try {
                list.add(captcha);
                String uuid = sendAsyncHttpRequest(urlString).get();
                String linkurl = "https://teamholy.de/captcha/" + uuid;
                String checkurl = "http://164.132.57.107:3000/holy/captcha/check/" + player.getName().toLowerCase(Locale.ROOT) + "/" + uuid + "/adasaisuoa2j2j2j2jnvasvcxds43efglkooiwuhlkabvd";
                captcha.setLink(linkurl);
                captcha.setCheckurl(checkurl);
                sendCaptchaMessage(captcha);
                return Optional.of(captcha);
            } catch (InterruptedException | ExecutionException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                // TODO Is mir doch egal!
            }
            return Optional.empty();
        });
    }

    public static void sendCaptchaMessage(Captcha c) {
//		c.getPlayer().sendMessage(TextComponent.fromLegacyText(c.getLink()));
        TextComponent text = new TextComponent();
        text.addExtra("§aClick Here to Verify");
        text.setClickEvent(new ClickEvent(Action.OPEN_URL, c.getLink()));
        c.getPlayer().sendMessage(text);
    }

    public static Optional<Captcha> getCapcha(ProxiedPlayer player) {
        for (Captcha c : list) {
            if (c.getPlayer().equals(player)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    private static CompletableFuture<String> sendAsyncHttpRequest(String urlString) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(5000);
                connection.setRequestMethod("GET");
                // Lesen der Antwort
                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                connection.disconnect();
                return response.toString();
            } catch (IOException e) {
//                e.printStackTrace();
                return null;
            }
        });
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Captcha {
        //		String requesturl;
        public String checkurl;
        public String link;
		public ProxiedPlayer player;
    }
}
