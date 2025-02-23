package de.teamholy.core.api.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Punish {
    private static final Map<Integer, BanReason> banReasonMap = new HashMap<>();
    private static final Map<Integer, MuteReason> muteReasonMap = new HashMap<>();

    private static final BanReason[] banValues = BanReason.values();
    private static final MuteReason[] muteValues = MuteReason.values();

    private static final UUID CONSOLE_UUID = UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670");

    static {
        Arrays.stream(getBanValues()).forEach(reason -> banReasonMap.put(reason.getId(), reason));
        Arrays.stream(getMuteValues()).forEach(reason -> muteReasonMap.put(reason.getId(), reason));
    }

    public static BanReason[] getBanValues() {
        return banValues;
    }

    public static MuteReason[] getMuteValues() {
        return muteValues;
    }

    public static UUID getConsoleUuid() {
        return CONSOLE_UUID;
    }

    public static BanReason parseBanReasonById(int id) {
        return banReasonMap.getOrDefault(id, null);
    }

    public static MuteReason parseMuteReasonById(int id) {
        return muteReasonMap.getOrDefault(id, null);
    }

    public enum Type {
        BAN, MUTE;
    }

    public enum BanReason implements PunishReason {

        HACKING(1, "Client Modifications", TimeUnit.DAYS.toMillis(30)),
        AUTO_CLICKER(2, "AutoClicker", TimeUnit.DAYS.toMillis(14)),
        BUG_USING(3, "Bug-Using", TimeUnit.DAYS.toMillis(15)),
        TROLLING(4, "Trolling", TimeUnit.DAYS.toMillis(3)), // changed from 7 to 3 days
        TEAMING(5, "Teaming", TimeUnit.DAYS.toMillis(7)), // changed from 30 to 7 days
        STATS_BOOSTING(6, "Stats Boosting", TimeUnit.DAYS.toMillis(7)), // changed from 14 to 7 days
        BAD_SKIN(7, "Malicious Skin", TimeUnit.DAYS.toMillis(7)),
        BAD_NAME(8, "Malicious Name", TimeUnit.DAYS.toMillis(90)),
        BAD_CLAN(9, "Malicious Clan", TimeUnit.DAYS.toMillis(7)),
        BAN_BYPASS(10, "Ban Bypassing", -1L),
        HOUSE_BAN(11, "Houseban", -1L);

        int id;
        String englishText;
        long duration;

        BanReason(int id, String englishText, long duration) {
            this.id = id;
            this.englishText = englishText;
            this.duration = duration;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getEnglishText() {
            return englishText;
        }

        @Override
        public long getDuration() {
            return duration;
        }

        @Override
        public Type getType() {
            return Type.BAN;
        }
    }

    public enum MuteReason implements PunishReason {

        //TEST(0,"Test", TimeUnit.MINUTES.toMillis(5)),
        ADVERTISING(1, "Advertising", TimeUnit.DAYS.toMillis(30)),
        RACISM(2, "Racism", -1L),
        INSULT(3, "Insulting", TimeUnit.DAYS.toMillis(7)),
        SPAM(4, "Spamming", TimeUnit.DAYS.toMillis(1)),
        PROVOCATION(5, "Provocation", TimeUnit.DAYS.toMillis(3)),
        CHOICEOFWORDS(6, "Choice of Words", TimeUnit.DAYS.toMillis(7));


        int id;
        String englishText;
        long duration;

        MuteReason(int id, String englishText, long duration) {
            this.id = id;
            this.englishText = englishText;
            this.duration = duration;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getEnglishText() {
            return englishText;
        }

        @Override
        public long getDuration() {
            return duration;
        }

        @Override
        public Type getType() {
            return Type.MUTE;
        }


    }

    public interface PunishReason {

        int getId();

        String getEnglishText();

        long getDuration();

        Type getType();

    }
}
