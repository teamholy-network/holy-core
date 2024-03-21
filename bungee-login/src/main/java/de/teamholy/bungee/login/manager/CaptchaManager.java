package de.teamholy.bungee.login.manager;

import de.teamholy.bungee.login.BungeeLogin;
import de.teamholy.bungee.login.api.TaskAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static de.teamholy.core.bungee.BungeeCore.RESTBASE;

public class CaptchaManager {

    public Set<Captcha> list = ConcurrentHashMap.newKeySet();

//	public static Set<Captcha> capchaready = ConcurrentHashMap.newKeySet();

    private Title statusTitle;

    private static CaptchaManager instance;

    public CaptchaManager() {
        createStatusTitle();
    }

    public static synchronized CaptchaManager getInstance() {
        if (instance == null) {
            instance = new CaptchaManager();
        }
        return instance;
    }

    public void init() {
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
                            sendCaptchaStatus(captcha);
                        }
                    });
                } else {
                    list.remove(captcha);
                }
            });
        }, 5, 5, TimeUnit.SECONDS);
    }

    public CompletableFuture<Optional<Captcha>> createCaptcha(ProxiedPlayer player) {
        String urlString = RESTBASE + "holy/captcha/generate/" + "/" + BungeeLogin.APIKEY + "/" + player.getName().toLowerCase(Locale.ROOT);
        Captcha captcha = new Captcha("", "", player);

        for (int i = 0; i < 10; i++) {
            BungeeLogin.getInstance().getLogger().info("Captcha URL: " + urlString);
        }

        BungeeLogin.getInstance().getLogger().info("Generating Captcha for " + player.getName() + " ...");

        return sendAsyncHttpRequest(urlString).thenApply(result -> {
            if (result == null || result.isEmpty()) {

                BungeeLogin.getInstance().getLogger().info("Captcha for " + player.getName() + " failed!");

                return Optional.empty();
            }

            BungeeLogin.getInstance().getLogger().info("Captcha for " + player.getName() + " generated!");


            list.add(captcha);

            String linkurl = "https://teamholy.de/captcha/" + result;
            String checkurl =  RESTBASE + "holy/captcha/get/" + "/" + BungeeLogin.APIKEY + "/" + player.getName().toLowerCase(Locale.ROOT);

            captcha.setLink(linkurl);
            captcha.setCheckurl(checkurl);

            for (int i = 0; i < 10; i++) {
                BungeeLogin.getInstance().getLogger().info("Captcha Link: " + linkurl);
            }

            sendCaptchaMessage(captcha);

            return Optional.of(captcha);

        });
    }

    public void sendCaptchaMessage(Captcha c) {
//		c.getPlayer().sendMessage(TextComponent.fromLegacyText(c.getLink()));
        TextComponent text = new TextComponent();
        text.addExtra("§aClick Here to Verify");
        text.setClickEvent(new ClickEvent(Action.OPEN_URL, c.getLink()));
        c.getPlayer().sendMessage(text);
    }

    public void sendCaptchaStatus(Captcha c) {
        c.getPlayer().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c§kN§c §aPlease §6verify §ayour connection to continue §c§kd"));
        c.getPlayer().sendTitle(statusTitle);
    }

    public Optional<Captcha> getCapcha(ProxiedPlayer player) {
        for (Captcha c : list) {
            if (c.getPlayer().equals(player)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    private CompletableFuture<String> sendAsyncHttpRequest(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try (Scanner scanner = new Scanner(new URL(url).openStream())) {
                StringBuilder stringBuilder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    stringBuilder.append(scanner.nextLine());
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Captcha {
        public String checkurl;
        public String link;
        public ProxiedPlayer player;
    }

    private void createStatusTitle() {
        statusTitle = BungeeLogin.getInstance().getProxy().createTitle();
        statusTitle.title(TextComponent.fromLegacyText(""));
        statusTitle.subTitle(TextComponent.fromLegacyText("§c§kN§c §aPlease §6verify §ayour connection to continue §c§kd"));
        statusTitle.fadeIn(0);
        statusTitle.stay(20 * 60 * 60);
        statusTitle.fadeOut(0);
    }

}