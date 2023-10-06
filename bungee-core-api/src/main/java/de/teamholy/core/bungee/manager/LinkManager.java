package de.teamholy.core.bungee.manager;

import de.teamholy.core.api.entities.stats.StatsProfile;
import de.teamholy.core.api.entities.stats.StatsProfileRepository;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.Helpers;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;

public class LinkManager {

    private StatsProfileRepository statsProfileRepository;
    private Helpers helpers = new Helpers();

    public LinkManager() {
        statsProfileRepository = BungeeCore.getAPI().getStatsProfileService().getRepository();
    }

    public void insertPlayer(ProxiedPlayer player) {

        if (playerExists(player)) {
            return;
        }

        String linkCode = helpers.generateSecureKey(50);

        StatsProfile statsProfile = new StatsProfile();
        statsProfile.setId(player.getUniqueId());
        statsProfile.setPlayerName(player.getName());
        statsProfile.setPlayerUUID(player.getUniqueId());
        statsProfile.setPlayerProfileLinked(false);
        statsProfile.setPlayerProfileVoted(false);
        statsProfile.setPlayerProfileFirstTimeLinked(false);
        statsProfile.setPlayerProfileLastTimeLinked(System.currentTimeMillis()); // Hurensohn
        statsProfile.setPlayerProfileLinkCode(linkCode);
        statsProfile.setPlayerProfileCookie(null);
        statsProfile.setPlayerProfileDiscordLinkCode(helpers.generateSecureKey(40));
        statsProfile.setPlayerProfileDiscordLinked(false);
        statsProfile.setPlayerProfileLastTimeLoggedIn(System.currentTimeMillis());

        StatsProfile.ProfileViews profileViews = new StatsProfile.ProfileViews();
        profileViews.setTotal(0);
        profileViews.setIps(new ArrayList<>());

        StatsProfile.PlayerProfileSocial playerProfileSocial = new StatsProfile.PlayerProfileSocial();
        playerProfileSocial.setProfileMessage(null);
        playerProfileSocial.setProfileComments(new ArrayList<>());
        playerProfileSocial.setProfileLikes(new ArrayList<>());

        StatsProfile.Streak profileStreak = new StatsProfile.Streak();
        profileStreak.setCurrent(0);
        profileStreak.setBest(0);
        profileStreak.setNext(0);
        profileStreak.setLimit(0);


        statsProfile.setProfileViews(profileViews);
        statsProfile.setPlayerProfileSocial(playerProfileSocial);
        statsProfile.setProfileStreak(profileStreak);

        statsProfileRepository.save(statsProfile);
        sendLinkMessageToPlayer(player, linkCode);


    }

    public void relinkPlayer(ProxiedPlayer player) {

        StatsProfile statsProfile = statsProfileRepository.findFirstById(player.getUniqueId());

        if (statsProfile == null) {
            player.sendMessage("§6Web §8× §7You are not linked!");
            return;
        }

        if (System.currentTimeMillis() - statsProfile.getPlayerProfileLastTimeLinked() < 60 * 60 * 1000) {
            player.sendMessage("§6Web §8× §7Please wait around §e" + helpers.getRemainingTime(statsProfile.getPlayerProfileLastTimeLinked(), 60 * 60 * 1000) + " §7minutes before you can relink your account!");
            return;
        }

        String linkCode = helpers.generateSecureKey(50);

        statsProfile.setPlayerProfileLinked(false);
        statsProfile.setPlayerProfileCookie(null);
        statsProfile.setPlayerProfileLinkCode(linkCode);

        statsProfileRepository.save(statsProfile);
        sendLinkMessageToPlayer(player, linkCode);

    }

    public boolean playerExists(ProxiedPlayer player) {
        return statsProfileRepository.existsById(player.getUniqueId());
    }

    public boolean playerLinked(ProxiedPlayer player) {
        return statsProfileRepository.findFirstById(player.getUniqueId()).isPlayerProfileLinked();
    }

    public String getPlayerLinkCode(ProxiedPlayer player) {
        return statsProfileRepository.findFirstById(player.getUniqueId()).getPlayerProfileLinkCode();
    }

    public long getPlayerLastTimeLoggedIn(ProxiedPlayer player) {
        return statsProfileRepository.findFirstById(player.getUniqueId()).getPlayerProfileLastTimeLoggedIn();
    }

    public void sendLinkMessageToPlayer(ProxiedPlayer player, String linkCode) {
        TextComponent message = new TextComponent("§6Web §8× §aYour Link has been generated. §7(Click me!)");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://teamholy.de/link/" + linkCode));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Click").create()));
        player.sendMessage(message);
    }

    public void sendConfirmRelinkOrLinkMessageToPlayer(ProxiedPlayer player) {
        TextComponent message = new TextComponent();
        if (playerLinked(player)) {
            message.addExtra("§6Web §8× §7You are already linked!");
            message.addExtra("\n");
        }
        message.addExtra("§6Web §8× §7We have noticed that you didn't login for a day! (§6Click me!§7) if you wish to relink.");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/link relink"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Relink").create()));
        player.sendMessage(message);

    }

}
