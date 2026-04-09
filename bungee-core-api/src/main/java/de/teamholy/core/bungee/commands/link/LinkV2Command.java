package de.teamholy.core.bungee.commands.link;

//import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import de.teamholy.core.api.constants.Message;

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
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(
                ChatColor.RED).create());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (strings.length == 0) {
            // Updated to use help command design with Message.TOPLINE and Message.HELP_BULLET symbols
            TextComponent main = new TextComponent(Message.TOPLINE);
            TextComponent separator = new TextComponent("\n");
            main.addExtra(separator);

            TextComponent clickable = new TextComponent(Message.HELP_BULLET + "Besuchen Sie unsere Webseite." + " -> teamholy.de/link"+ "\n");
            clickable.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://teamholy.de/link"));
            main.addExtra(clickable);

            TextComponent footer = new TextComponent(Message.HELP_BULLET + "Dort könnten Sie ihr Konto verknüpfen und kostenlose Coins erhalten!");
            main.addExtra(footer);

            commandSender.sendMessage(main);
            return;
        }

        String code = strings[0];

        if (code.length() != 39) {
            commandSender.sendMessage("§c" + "Error: Your code is invalid");
            return;
        }

        sendAsyncHttpRequest("https://teamholy.de/api/holy/link/v2/verify/adasaisuoa2j2j2j2jnvalkooiwuhlkabvd/" + player.getUniqueId().toString() + "/" + code).thenAccept(response -> {
            if (response == null) {
                commandSender.sendMessage("§c" + "Error: An error occurred while processing your request");
                return;
            }
            String message = response.split("\"")[1];

            switch (message) {
                case "ok" -> commandSender.sendMessage("§6Web §8× §a" + "You have successfully linked your account!");
                case "failed" -> commandSender.sendMessage("§c" + "Error: Your code is invalid or something went wrong");
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
