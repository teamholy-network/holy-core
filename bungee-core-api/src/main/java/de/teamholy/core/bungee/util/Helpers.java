package de.teamholy.core.bungee.util;

/* copyright by Greg */
public class Helpers {

    public String centerMessage(String message) {
        String stripped = message.replaceAll("§.", "");
        int length = stripped.length();
        int space = (60 - length) / 2;

        return repeat(" ", Math.max(0, space)) + message;
    }

    private String repeat(String str, int times) {
        return new String(new char[times]).replace("\0", str);
    }
}

