package de.teamholy.core.bukkit.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BukkitFormatUtil {

    public String format(String template, String... args) {
        for (String arg : args) {
            int idx = template.indexOf("{}");
            if (idx >= 0) {
                template = template.substring(0, idx) + arg + template.substring(idx + 2);
            }
        }
        return template;
    }

}
