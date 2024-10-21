package eu.koboo.markup.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import java.util.Collection;
import java.util.List;

public class WrapperPlayServerScoreboardTeam extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_TEAM;

    public WrapperPlayServerScoreboardTeam() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerScoreboardTeam(PacketContainer packet) {
        super(packet, TYPE);
    }

    public String getTeamName() {
        return handle.getStrings().read(0);
    }

    public void setTeamName(String value) {
        handle.getStrings().write(0, value);
    }

    public int getMode() {
        return handle.getIntegers().read(1);
    }

    public void setMode(int value) {
        handle.getIntegers().write(1, value);
    }

    public String getTeamDisplayName() {
        return handle.getStrings().read(1);
    }

    public void setTeamDisplayName(String value) {
        handle.getStrings().write(1, value);
    }

    public String getTeamPrefix() {
        return handle.getStrings().read(2);
    }

    public void setTeamPrefix(String value) {
        handle.getStrings().write(2, value);
    }

    public String getTeamSuffix() {
        return handle.getStrings().read(3);
    }

    public void setTeamSuffix(String value) {
        handle.getStrings().write(3, value);
    }

    public int getFriendlyFire() {
        return handle.getIntegers().read(1);
    }

    public void setFriendlyFire(int value) {
        handle.getIntegers().write(1, value);
    }

    public String getNameTagVisibility() {
        return handle.getStrings().read(4);
    }

    public void setNameTagVisibility(String value) {
        handle.getStrings().write(4, value);
    }

    public int getColor() {
        return handle.getIntegers().read(2);
    }


    public void setColor(int value) {
        handle.getIntegers().write(2, value);
    }

    public int getPlayerCount() {
        return handle.getIntegers().read(0);
    }

    public void setPlayerCount(int value) {
        handle.getIntegers().write(0, value);
    }

    @SuppressWarnings("unchecked")
    public List<String> getPlayers() {
        return (List<String>) handle.getSpecificModifier(Collection.class).read(0);
    }

    public void setPlayers(List<String> value) {
        handle.getSpecificModifier(Collection.class).write(0, value);
    }

}