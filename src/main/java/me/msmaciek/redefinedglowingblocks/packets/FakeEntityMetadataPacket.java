package me.msmaciek.redefinedglowingblocks.packets;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.bukkit.block.Block;

import java.util.List;

public class FakeEntityMetadataPacket extends WrapperPlayServerEntityMetadata {

    // Display entity
    public FakeEntityMetadataPacket(int eID, Block block, Vector3f scale, Vector3f position) {
        super(eID, List.of());

        int blockStateId = 1;

        try {
            String blockStateName = block.getBlockData().getAsString();
            blockStateId = WrappedBlockState.getByString(blockStateName).getGlobalId();
        } catch (Exception e) {
            System.out.println("[WARNING] Failed to get block state id for " + block.getBlockData().getAsString());
            e.printStackTrace();
        }

        setEntityMetadata(List.of(
            new EntityData(0, EntityDataTypes.BYTE, (byte) 0x40), // glowing byte
            new EntityData(23, EntityDataTypes.BLOCK_STATE, blockStateId),
            new EntityData(12, EntityDataTypes.VECTOR3F, scale), // scale, made it little bit smaller than full block, so it doesn't bug with texture
            new EntityData(11, EntityDataTypes.VECTOR3F, position) // position (translation)
        ));
    }

    // Magma entity
    public FakeEntityMetadataPacket(int eID, int magmaSize) {
        super(eID, List.of(
            new EntityData(0, EntityDataTypes.BYTE, (byte) (0x20 | 0x40)), // invisible and glowing byte
            new EntityData(15, EntityDataTypes.BYTE, (byte) 0x01), // NoAI
            new EntityData(4, EntityDataTypes.BOOLEAN, true), // isSilent
            new EntityData(5, EntityDataTypes.BOOLEAN, true), // noGravity
            new EntityData(16, EntityDataTypes.INT, magmaSize)
        ));
    }

    // Shulker
    public FakeEntityMetadataPacket(int eID) {
        super(eID, List.of(
            new EntityData(0, EntityDataTypes.BYTE, (byte) (0x20 | 0x40)), // invisible and glowing byte
            new EntityData(15, EntityDataTypes.BYTE, (byte) 0x01), // NoAI
            new EntityData(4, EntityDataTypes.BOOLEAN, true), // isSilent
            new EntityData(5, EntityDataTypes.BOOLEAN, true) // noGravity
        ));
    }
}
