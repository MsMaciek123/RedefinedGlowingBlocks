package me.msmaciek.redefinedglowingblocks.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class FakeSpawnEntityPacket extends PacketContainer {
    public FakeSpawnEntityPacket(int eID, UUID uuid, EntityType entityType, Location loc, byte pitch, byte yaw) {
        super(PacketType.Play.Server.SPAWN_ENTITY);
        getModifier().writeDefaults();

        getIntegers().write(0, eID);
        getUUIDs().write(0, uuid);
        getEntityTypeModifier().write(0, entityType);

        getDoubles()
            .write(0, loc.getX())
            .write(1, loc.getY())
            .write(2, loc.getZ());

        getBytes()
            .write(0, pitch)
            .write(1, yaw);
    }
}
