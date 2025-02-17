package de.teamholy.core.bukkit.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* copyright by Yassino */
@Getter
public class ScoreboardAPI {

    private Player player;
    private Scoreboard scoreboard;
    private Objective objective;
    private String colorCode;

    public ScoreboardAPI createScoreboard(Player player, String colorcode) {
        this.player = player;
        this.colorCode = colorcode;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        (this.objective = scoreboard.registerNewObjective("sidebar", "bbb")).setDisplayName(colorcode + "§lTEAMHOLY");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return this;
    }

    public ScoreboardAPI setLine(int line, String string) {
        if (!contains(line)) {
            Team team = scoreboard.registerNewTeam(ChatColor.values()[line].toString());
            team.addEntry(ChatColor.values()[line].toString());
            List<String> list = splitText(string);
            team.setPrefix(list.get(0));
            if (list.size() == 2)
                team.setSuffix(list.get(1));
            objective.getScore(ChatColor.values()[line].toString()).setScore(line);
            return this;
        }
        return this;
    }

    public void clearScoreboard() {
        for (int i = 0; i < 22; i++) {
            if (contains(i)) {
                removeLine(i);
            }
        }
    }

    public boolean contains(int line) {
        return (scoreboard.getTeam(ChatColor.values()[line].toString()) != null);
    }

    public ScoreboardAPI removeLine(int line) {
        if (contains(line)) {
            scoreboard.resetScores(ChatColor.values()[line].toString());
            scoreboard.getTeam(ChatColor.values()[line].toString()).unregister();
        }
        return this;
    }

    public ScoreboardAPI updateLine(int line, String string) {
        Team team = scoreboard.getTeam(ChatColor.values()[line].toString());
        List<String> list = splitText(string);
        team.setPrefix(list.get(0));
        if (list.size() == 2) {
            team.setSuffix(list.get(1));
        } else {
            team.setSuffix("");
        }
        return this;
    }

    public List<String> getLine(int line) {
        Team team = scoreboard.getTeam(ChatColor.values()[line].toString());
        if (team == null) {
            return Lists.newArrayList();
        }
        List<String> list = new ArrayList<>();
        if (!team.getPrefix().isEmpty()) {
            list.add(team.getPrefix());
        }
        if (!team.getSuffix().isEmpty()) {
            list.add(team.getSuffix());
        }
        return list;
    }

    public ScoreboardAPI updateLine(Team team, String string) {
        List<String> list = splitText(string);
        team.setPrefix(list.get(0));
        if (list.size() == 2) {
            team.setSuffix(list.get(1));
        } else {
            team.setSuffix(null);
        }
        return this;
    }


    public void build() {
        player.setScoreboard(scoreboard);
    }

    public List<String> splitText(String string) {
        List<String> list = new ArrayList<>();

        if (string.length() > 32) {
            string = string.substring(0, 31);
        }

        Splitter splitter = Splitter.fixedLength(16);
        Iterator<String> iterator = splitter.split(string).iterator();

        String prefix = iterator.next();

        if (string.length() > 16) {
            String lastColor = ChatColor.getLastColors(prefix);
            String suffix = lastColor + (prefix.endsWith("§") ? "§" : "") + iterator.next();
            list.add(prefix.endsWith("§") ? removeLastCharacter(prefix) : prefix);
            list.add((suffix.length() > 16 ? suffix.substring(0, 16) : suffix));
        } else {
            list.add(prefix.endsWith("§") ? removeLastCharacter(prefix) : prefix);
        }
        return list;
    }


    private String removeLastCharacter(String str) {
        String result = null;
        if ((str != null) && (!str.isEmpty())) {
            result = str.substring(0, str.length() - 1);
        }
        return result;
    }

}