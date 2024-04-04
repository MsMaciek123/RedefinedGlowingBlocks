package me.msmaciek.redefinedglowingblocks.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Material;
import org.joml.Vector3f;

import java.util.List;

public class FakeEntityMetadataPacket extends PacketContainer {

    // Display entity
    public FakeEntityMetadataPacket(int eID, Material material, Vector3f scale, Vector3f position) {
        super(PacketType.Play.Server.ENTITY_METADATA);
        getModifier().writeDefaults();
        getIntegers().write(0, eID);

        List<WrappedDataValue> values = List.of(
            new WrappedDataValue(
                0,
                WrappedDataWatcher.Registry.get(Byte.class),
                (byte) 0x40 // glowing byte
            ),
            new WrappedDataValue(
                23,
                WrappedDataWatcher.Registry.get(MinecraftReflection.getIBlockDataClass()),
                WrappedBlockData.createData(material).getHandle() // block data
            ),
            new WrappedDataValue(
                12, // scale, made it little bit smaller than full block, so it doesn't bug with texture
                WrappedDataWatcher.Registry.get(Vector3f.class),
                scale
            ),
            new WrappedDataValue(
                11, // position (translation)
                WrappedDataWatcher.Registry.get(Vector3f.class),
                position
            )
        );

        getDataValueCollectionModifier().write(0, values);
    }

    // Magma entity
    public FakeEntityMetadataPacket(int eID) {
        super(PacketType.Play.Server.ENTITY_METADATA);
        getModifier().writeDefaults();
        getIntegers().write(0, eID);

        List<WrappedDataValue> values = List.of(
            new WrappedDataValue(
                0,
                WrappedDataWatcher.Registry.get(Byte.class),
                (byte) (0x20 | 0x40) // invisible and glowing byte
            ),
            new WrappedDataValue(
                16, // magma size
                WrappedDataWatcher.Registry.get(Integer.class),
                2
            ),
            new WrappedDataValue(
                15, // NoAI
                WrappedDataWatcher.Registry.get(Byte.class),
                (byte) 0x01
            ),
            new WrappedDataValue(
                4, // isSilent
                WrappedDataWatcher.Registry.get(Boolean.class),
                true
            ),
            new WrappedDataValue(
                5, // noGravity
                WrappedDataWatcher.Registry.get(Boolean.class),
                true
            )
        );

        getDataValueCollectionModifier().write(0, values);
    }
}
