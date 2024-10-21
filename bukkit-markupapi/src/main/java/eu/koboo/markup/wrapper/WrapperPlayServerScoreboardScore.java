package eu.koboo.markup.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;


public class WrapperPlayServerScoreboardScore extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_SCORE;

    /**
     * Enumeration of all the known packet modes.
     *
     * @author Kristian
     */


    public WrapperPlayServerScoreboardScore() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerScoreboardScore(PacketContainer packet) {
        super(packet, TYPE);
    }

    public String getItemName() {
        return handle.getStrings().read(0);
    }

    /**
     * Set an unique name to be displayed in the list..
     * @param value - new value.
     */
    public void setItemName(String value) {
        handle.getStrings().write(0, value);
    }

    public byte getPacketMode() {
        return handle.getIntegers().read(1).byteValue();
    }


    public void setPacketMode(byte value) {
        handle.getIntegers().write(1, (int) value);
    }

    /**
     * Retrieve the unique name for the scoreboard to be updated. Only sent when setting a score.
     * @return The current Score Name
     */
    public String getScoreName() {
        return handle.getStrings().read(1);
    }

    /**
     * Set the unique name for the scoreboard to be updated. Only sent when setting a score.
     * @param value - new value.
     */
    public void setScoreName(String value) {
        handle.getStrings().write(1, (String) value);
    }

    /**
     * Retrieve the score to be displayed next to the entry. Only sent when setting a score.
     * @return The current Value
     */
    public int getValue() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set the score to be displayed next to the entry. Only sent when setting a score.
     * @param value - new value.
     */
    public void setValue(int value) {
        handle.getIntegers().write(0, (int) value);
    }
}