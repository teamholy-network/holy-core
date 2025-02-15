package de.teamholy.core.bungee.commands.link;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class LinkV2Command extends Command {
    public LinkV2Command(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (strings.length == 0) {
            TextComponent header = new TextComponent("§6/link\n");
            TextComponent separator = new TextComponent("§8------ §aTeamHoly.de §8------\n");
            TextComponent clickable = new TextComponent("§7" + BungeeTranslateAPI.translate(player, "Besuchen Sie unsere Webseite. -> teamholy.de/link") + "\n");
            clickable.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://teamholy.de/link"));
            TextComponent footer = new TextComponent("§7" + BungeeTranslateAPI.translate(player, "Dort könnten Sie ihr Konto verknüpfen und kostenlose Coins erhalten!"));
            
            header.addExtra(separator);
            header.addExtra(clickable);
            header.addExtra(footer);
            commandSender.sendMessage(header);
            return;
        }

        String code = strings[0];

        if (code.length() != 39) {
            commandSender.sendMessage("§c" + BungeeTranslateAPI.translate(player, "Error: Your code is invalid"));
            return;
        }

        sendAsyncHttpRequest("http://185.14.92.243:3004/holy/link/v2/verify/adasaisuoa2j2j2j2jnvalkooiwuhlkabvd/" + player.getUniqueId().toString() + "/" + code).thenAccept(response -> {
            if (response == null) {
                commandSender.sendMessage("§c" + BungeeTranslateAPI.translate(player, "Error: An error occurred while processing your request"));
                return;
            }
            String message = response.split("\"")[1];

            switch (message) {
                case "ok" -> {
                    commandSender.sendMessage("§6Web §8× §a" + BungeeTranslateAPI.translate(player, "You have successfully linked your account!"));
                }
                case "failed" -> {
                    commandSender.sendMessage("§c" + BungeeTranslateAPI.translate(player, "Error: Your code is invalid or something went wrong"));
                }
            }
        });


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
}
