package eu.koboo.markup.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import eu.koboo.markup.MarkupAPI;
import eu.koboo.markup.util.PlayerMeta;
import eu.koboo.markup.wrapper.WrapperPlayServerNamedEntitySpawn;

public class PacketPlayServerNamedEntitySpawnAdapter extends PacketAdapter {

    private final MarkupAPI markupAPI;

    public PacketPlayServerNamedEntitySpawnAdapter(MarkupAPI markupAPI) {
        super(markupAPI, ListenerPriority.LOWEST, PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        this.markupAPI = markupAPI;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        WrapperPlayServerNamedEntitySpawn packet = new WrapperPlayServerNamedEntitySpawn(event.getPacket());

        if (event.getPlayer().getUniqueId().equals(packet.getPlayerUUID())) {
            return;
        }

        PlayerMeta playerMeta = markupAPI.getNickManager().getPlayerMeta(packet.getPlayerUUID());

        if (playerMeta == null) return;

        WrapperPlayServerNamedEntitySpawn newPacket = new WrapperPlayServerNamedEntitySpawn();

        newPacket.setEntityID(packet.getEntityID());
        newPacket.setPitch(packet.getPitch());
        newPacket.setPlayerUUID(playerMeta.getNickUUID());
        newPacket.setPosition(packet.getPosition());
        newPacket.setYaw(packet.getYaw());
        newPacket.setPitch(packet.getPitch());

        event.setPacket(newPacket.getHandle());

    }

}