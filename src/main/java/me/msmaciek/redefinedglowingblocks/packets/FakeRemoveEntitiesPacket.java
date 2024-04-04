package me.msmaciek.redefinedglowingblocks.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import java.util.List;

public class FakeRemoveEntitiesPacket extends PacketContainer {
    public FakeRemoveEntitiesPacket(List<Integer> eIDs) {
        super(PacketType.Play.Server.ENTITY_DESTROY);
        getModifier().writeDefaults();
        getIntLists().write(0, eIDs);
    }
}
