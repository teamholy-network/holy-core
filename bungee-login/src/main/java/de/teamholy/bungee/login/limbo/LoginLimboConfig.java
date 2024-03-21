package de.teamholy.login.limbo;

import ua.nanit.limbo.configuration.LimboConfig;
import ua.nanit.limbo.server.data.BossBar;
import ua.nanit.limbo.server.data.InfoForwarding;
import ua.nanit.limbo.server.data.PingData;
import ua.nanit.limbo.server.data.Title;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class LoginLimboConfig implements LimboConfig {

    @Override
    public SocketAddress getAddress() {
        return InetSocketAddress.createUnresolved("0.0.0.0", 65532);
    }

    @Override
    public int getMaxPlayers() {
        return 500;
    }

    @Override
    public PingData getPingData() {
        return new PingData() {
            private String version = "Server";
            private String description = "{\"text\": \"&7Server\"}";
            private int protocol = -1;

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public int getProtocol() {
                return protocol;
            }

            public void setProtocol(int protocol) {
                this.protocol = protocol;
            }
        };
    }

    @Override
    public String getDimensionType() {
        return "THE_END";
    }

    @Override
    public int getGameMode() {
        return 3;
    }

    @Override
    public InfoForwarding getInfoForwarding() {
        return new InfoForwarding() {
            private Type type = Type.NONE;
            private byte[] secretKey = "test".getBytes();
            private List<String> tokens = Arrays.asList("test");
        };
    }

    @Override
    public long getReadTimeout() {
        return 30000;
    }

    @Override
    public int getDebugLevel() {
        return 0;
    }

    @Override
    public boolean isUseBrandName() {
        return true;
    }

    @Override
    public boolean isUseJoinMessage() {
        return false;
    }

    @Override
    public boolean isUseBossBar() {
        return true;
    }

    @Override
    public boolean isUseTitle() {
        return false;
    }

    @Override
    public boolean isUsePlayerList() {
        return false;
    }

    @Override
    public boolean isUseHeaderAndFooter() {
        return false;
    }

    @Override
    public String getBrandName() {
        return "Holy Login";
    }

    @Override
    public String getJoinMessage() {
        return null;
    }

    @Override
    public BossBar getBossBar() {
        return new BossBar() {
            private String text = "{\"text\": \"/login <password> <password>\"}";
            private float health = 1.0f;
            private Color color = Color.PINK;
            private Division division = BossBar.Division.SOLID;
        };
    }

    @Override
    public Title getTitle() {
        return null;
    }

    @Override
    public String getPlayerListUsername() {
        return null;
    }

    @Override
    public String getPlayerListHeader() {
        return null;
    }

    @Override
    public String getPlayerListFooter() {
        return null;
    }

    @Override
    public boolean isUseEpoll() {
        return true;
    }

    @Override
    public int getBossGroupSize() {
        return 1;
    }

    @Override
    public int getWorkerGroupSize() {
        return 4;
    }

}
