package de.teamholy.core.bungee.commands.link;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

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
            commandSender.sendMessage("§6Web §8× §7 Visit §6teamholy.de/link §7to link your account and get free §ecoins!");
            return;
        }

        String code = strings[0];

        if (code.length() != 39) {
            commandSender.sendMessage("§cError: Your code is invalid");
            return;
        }

        sendAsyncHttpRequest("http://185.244.25.8:3004/holy/link/v2/verify/adasaisuoa2j2j2j2jnvalkooiwuhlkabvd/" + player.getUniqueId().toString() + "/" + code).thenAccept(response -> {
            if (response == null) {
                commandSender.sendMessage("§cError: An error occurred while processing your request");
                return;
            }
            String message = response.split("\"")[1];

            switch (message) {
                case "ok" -> {
                    commandSender.sendMessage("§6Web §8× §aYou have successfully linked your account!");
                }
                case "failed" -> {
                    commandSender.sendMessage("§cError: Your code is invalid or something went wrong");
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
