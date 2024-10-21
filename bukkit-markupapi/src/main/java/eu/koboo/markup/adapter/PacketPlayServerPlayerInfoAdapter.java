package eu.koboo.markup.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.mojang.authlib.properties.Property;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.util.PlayerMeta;
import eu.koboo.markup.wrapper.WrapperPlayServerPlayerInfo;

import java.util.ArrayList;
import java.util.List;

public class PacketPlayServerPlayerInfoAdapter extends PacketAdapter {

    private final MarkupAPI markupAPI;

    public PacketPlayServerPlayerInfoAdapter(MarkupAPI markupAPI) {
        super(markupAPI, ListenerPriority.LOWEST, PacketType.Play.Server.PLAYER_INFO);
        this.markupAPI = markupAPI;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event.getPacket());

        if (markupAPI.getNickManager().getPlayerMetaMap().containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        if (packet.getAction() != EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            return;
        }

        List<PlayerInfoData> playerInfoDataList = new ArrayList<>();
        for (PlayerInfoData infoData : packet.getData()) {

            PlayerMeta playerMeta = markupAPI.getNickManager().getPlayerMeta(infoData.getProfile().getUUID());

            if (playerMeta == null) {
                playerInfoDataList.add(infoData);
                continue;
            }

            if (infoData.getProfile().getUUID().equals(playerMeta.getNickUUID())) {
                playerInfoDataList.add(infoData);
                continue;
            }

            WrappedGameProfile profile = new WrappedGameProfile(playerMeta.getNickUUID(), playerMeta.getNickName());
            Property property = playerMeta.getNickTextures();

            WrappedSignedProperty signedProperty = new WrappedSignedProperty("textures", property.getValue(), property.getSignature());
            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", signedProperty);

            infoData = new PlayerInfoData(profile, infoData.getLatency(), infoData.getGameMode(), infoData.getDisplayName());
            playerInfoDataList.add(infoData);

        }

        packet.setData(playerInfoDataList);
        event.setPacket(packet.getHandle());
    }
}