package eu.koboo.markup.util;

import java.security.SecureRandom;
import java.util.Locale;

public class NameGenerator {

    private static SecureRandom SECURE_RANDOM = new SecureRandom();

    private static String[] NAME_PREFIX_PARTS = new String[]{
            "itz", "the", "just", "by", "que", "i", "its", "ign", "king", "death", "color",
            "mr", "mrs", "sir", "general", "flash", "pro", "yt", "hd", "re", "un",
            "fastest", "legit", "leqit", "super", "soul", "pvp", "too", "uhc", "yxz", "real", "r3al",
            "re4l", "l3git", "l3qit", "light", "liqht", "only", "aqua", "fire", "on", "devil", "war",
            "six", "seven", "one", "two", "three", "four", "loop", "hope", "exe", "story", "mother"
    };

    private static String[] NAME_NICK_PARTS = new String[]{
            "slime", "mine", "craft", "solar", "control", "detroit", "curryman", "prestige",
            "phantom", "fisher", "anything", "monster", "runner", "majong", "santaclaus",
            "infinity", "shadow", "tiger", "tank", "kingkong", "slayer", "fighter", "knight",
            "crusher", "grinder", "cookie", "cubic", "soldier", "electro", "gurl", "ghoul", "tokyo",
            "berlin", "tommy", "thunder", "hope", "cheater", "less"
    };

    public static String generateName() {

        StringBuilder nameBuilder = new StringBuilder();
        if (randomChoice() && randomChoice()) {
            nameBuilder.append("x");
        }
        if (randomChoice()) {
            String namePrefix = NAME_PREFIX_PARTS[SECURE_RANDOM.nextInt(NAME_PREFIX_PARTS.length)];
            if (randomChoice()) {
                namePrefix = namePrefix.substring(0, 1).toUpperCase(Locale.ROOT) + namePrefix.substring(1);
            } else if (randomChoice()) {
                namePrefix = namePrefix.toUpperCase(Locale.ROOT);
            } else if (randomChoice()) {
                namePrefix = replaceLeet(namePrefix);
            }

            nameBuilder.append(namePrefix);
        } else {
            int randomNumber = generateLength(1, 999);
            nameBuilder.append(randomNumber);
        }
        if (randomChoice()) {
            nameBuilder.append("_");
        }
        String namePart = NAME_NICK_PARTS[SECURE_RANDOM.nextInt(NAME_NICK_PARTS.length)];
        if (randomChoice()) {
            namePart = namePart.substring(0, 1).toUpperCase(Locale.ROOT) + namePart.substring(1);
        } else if (randomChoice()) {
            namePart = replaceLeet(namePart);
        }
        nameBuilder.append(namePart);
        if (nameBuilder.toString().length() < 10) {
            if (randomChoice()) {
                nameBuilder.append("_");
            }
            if (randomChoice()) {
                int randomNumber = generateLength(1, 10000);
                nameBuilder.append(randomNumber);
            }
        }
        if (randomChoice() && randomChoice()) {
            nameBuilder.append("x");
        }
        String nickName = nameBuilder.toString();
        if (nickName.length() > 16) {
            nickName = nickName.substring(0, 16);
        }
        return nickName;
    }

    private static boolean randomChoice() {
        return generateLength(0, 1) == 1;
    }

    private static int generateLength(int from, int to) {
        return SECURE_RANDOM.nextInt((to + 1) - from) + from;
    }

    private static String replaceLeet(String string) {
        if (randomChoice()) {
            string = string.replaceAll("i", "1");
        }
        if (randomChoice()) {
            string = string.replaceAll("o", "0");
        }
        if (randomChoice()) {
            string = string.replaceAll("e", "3");
        }
        if (randomChoice()) {
            string = string.replaceAll("a", "4");
        }
        if (randomChoice()) {
            string = string.replaceAll("s", "5");
        }
        if (randomChoice()) {
            string = string.replaceAll("o", "0");
        }
        if (randomChoice()) {
            string = string.replaceAll("t", "7");
        }
        return string;
    }
}
