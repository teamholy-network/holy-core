package de.teamholy.core.api.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Utility class handling punishment-related operations for player moderation.
 * Provides access to ban and mute reasons with their respective durations and identifiers.
 */
public class Punish {
    // Maps to efficiently retrieve reasons by their ID
    private static final Map<Integer, BanReason> banReasonMap = new HashMap<>();
    private static final Map<Integer, MuteReason> muteReasonMap = new HashMap<>();

    // Cached enum values for efficient access
    private static final BanReason[] banValues = BanReason.values();
    private static final MuteReason[] muteValues = MuteReason.values();

    // UUID representing the console for system-initiated punishments
    private static final UUID CONSOLE_UUID = UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670");

    // Initialize the lookup maps with all ban and mute reasons
    static {
        Stream.of(getBanValues()).forEach(reason -> banReasonMap.put(reason.getId(), reason));
        Stream.of(getMuteValues()).forEach(reason -> muteReasonMap.put(reason.getId(), reason));
    }

    /**
     * Returns all available ban reasons.
     *
     * @return Array of all ban reason constants
     */
    public static BanReason[] getBanValues() {
        return banValues;
    }

    /**
     * Returns all available mute reasons.
     *
     * @return Array of all mute reason constants
     */
    public static MuteReason[] getMuteValues() {
        return muteValues;
    }

    /**
     * Returns the UUID representing the console/system.
     * Used to identify punishments issued by the system rather than a player.
     *
     * @return The UUID of the console
     */
    public static UUID getConsoleUuid() {
        return CONSOLE_UUID;
    }

    /**
     * Finds a ban reason by its numeric ID.
     *
     * @param id The ID of the ban reason to find
     * @return The matching BanReason, or null if not found
     */
    public static BanReason parseBanReasonById(int id) {
        return banReasonMap.getOrDefault(id, null);
    }

    /**
     * Finds a mute reason by its numeric ID.
     *
     * @param id The ID of the mute reason to find
     * @return The matching MuteReason, or null if not found
     */
    public static MuteReason parseMuteReasonById(int id) {
        return muteReasonMap.getOrDefault(id, null);
    }

    /**
     * Represents the type of punishment that can be applied.
     */
    public enum Type {
        BAN, MUTE;
    }

    /**
     * Enumeration of all available ban reasons with their associated details.
     * Each reason includes an ID, display text, and duration in milliseconds.
     * A duration of -1 indicates a permanent ban.
     */
    public enum BanReason implements PunishReason {

        HACKING(1, "Client Modifications", TimeUnit.DAYS.toMillis(30)),
        AUTO_CLICKER(2, "AutoClicker", TimeUnit.DAYS.toMillis(3)),
        BUG_USING(3, "Bug-Using", TimeUnit.DAYS.toMillis(15)),
        TROLLING(4, "Trolling", TimeUnit.DAYS.toMillis(3)), // changed from 7 to 3 days
        TEAMING(5, "Teaming", TimeUnit.DAYS.toMillis(7)),   // changed from 30 to 7 days
        STATS_BOOSTING(6, "Stats Boosting", TimeUnit.DAYS.toMillis(7)), // changed from 14 to 7 days
        BAD_SKIN(7, "Malicious Skin", TimeUnit.DAYS.toMillis(7)),
        BAD_NAME(8, "Malicious Name", TimeUnit.DAYS.toMillis(90)),
        BAD_CLAN(9, "Malicious Clan", TimeUnit.DAYS.toMillis(7)),
        BAN_BYPASS(10, "Ban Bypassing", -1L),
        HOUSE_BAN(11, "Houseban", -1L);

        private final int id;
        private final String englishText;
        private final long duration;

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

    /**
     * Enumeration of all available mute reasons with their associated details.
     * Each reason includes an ID, display text, and duration in milliseconds.
     * A duration of -1 indicates a permanent mute.
     */
    public enum MuteReason implements PunishReason {

        //TEST(0,"Test", TimeUnit.MINUTES.toMillis(5)),
        ADVERTISING(1, "Advertising", TimeUnit.DAYS.toMillis(30)),
        RACISM(2, "Racism", -1L),
        INSULT(3, "Insulting", TimeUnit.DAYS.toMillis(7)),
        SPAM(4, "Spamming", TimeUnit.DAYS.toMillis(1)),
        PROVOCATION(5, "Provocation", TimeUnit.DAYS.toMillis(3)),
        CHOICEOFWORDS(6, "Choice of Words", TimeUnit.DAYS.toMillis(7));

        private final int id;
        private final String englishText;
        private final long duration;

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

    /**
     * Interface for punishment reasons, implemented by both BanReason and MuteReason.
     * Defines the common properties that all punishment reasons must have.
     */
    public interface PunishReason {
        /**
         * Gets the unique identifier for this reason.
         *
         * @return The numeric ID of this reason
         */
        int getId();

        /**
         * Gets the English display text for this reason.
         *
         * @return The textual description of this reason
         */
        String getEnglishText();

        /**
         * Gets the duration of the punishment in milliseconds.
         * A value of -1 indicates a permanent punishment.
         *
         * @return The duration in milliseconds, or -1 if permanent
         */
        long getDuration();

        /**
         * Gets the type of punishment this reason is for.
         *
         * @return The punishment type (BAN or MUTE)
         */
        Type getType();
    }
}
