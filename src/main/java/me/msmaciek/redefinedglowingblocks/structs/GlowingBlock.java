package me.msmaciek.redefinedglowingblocks.structs;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import me.eliab.sbcontrol.SbControl;
import me.eliab.sbcontrol.enums.CollisionRule;
import me.eliab.sbcontrol.network.PacketManager;
import me.eliab.sbcontrol.network.packets.PacketTeam;
import me.msmaciek.redefinedglowingblocks.enums.FullBlockEnum;
import me.msmaciek.redefinedglowingblocks.packets.FakeEntityMetadataPacket;
import me.msmaciek.redefinedglowingblocks.packets.FakeRemoveEntitiesPacket;
import me.msmaciek.redefinedglowingblocks.packets.FakeSpawnEntityPacket;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class GlowingBlock {
    private static final AtomicInteger ENTITY_ID_COUNTER =
            new AtomicInteger(ThreadLocalRandom.current().nextInt(1_000_000, 2_000_000_000));


    // TODO: Use lombok and make those private
    public Player receiver;
    public int entityId;
    public UUID entityUUID;
    public Block block;
    public ChatColor color;

    public FullBlockEnum fullBlock;
    public Location entityLocation;
    public float pitch;
    public float yaw;


    private final ProtocolManager protocolManager;
    private final PacketManager packetManager;


    public GlowingBlock(Player receiver, Block block, ChatColor color, FullBlockEnum fullBlock) {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.packetManager = SbControl.getPacketManager();

        this.receiver = receiver;

        this.entityId = ENTITY_ID_COUNTER.getAndIncrement();
        this.entityUUID = UUID.randomUUID();

        this.block = block;
        this.color = color;

        this.fullBlock = fullBlock;
        if(fullBlock.equals(FullBlockEnum.Detect)) {
            if(block.getType().isOccluding())
                this.fullBlock = FullBlockEnum.FullOpaque;
            else if(isCube(block))
                this.fullBlock = FullBlockEnum.FullTransparent;
            else
                this.fullBlock = FullBlockEnum.Nonfull;
        }

        calculateEntityLocation();
    }

    // https://www.spigotmc.org/threads/how-to-check-if-a-block-is-realy-a-block.536470/
    public boolean isCube(Block block) {
        VoxelShape voxelShape = block.getCollisionShape();
        BoundingBox boundingBox = block.getBoundingBox();
        return (voxelShape.getBoundingBoxes().size() == 1
                && boundingBox.getWidthX() == 1.0
                && boundingBox.getHeight() == 1.0
                && boundingBox.getWidthZ() == 1.0
        );
    }

    public void RewriteGlowingBlock(GlowingBlock otherGlowingBlock) {
        // TODO: Recalculate fullBlock and entity location
        this.color = otherGlowingBlock.color;
    }

    void calculateEntityLocation() {
        Location loc = block.getLocation();

        Vector positionModifier = new Vector(0, 0, 0);

        // Stairs
        if(this.fullBlock.equals(FullBlockEnum.Nonfull))
        {
            if (block.getBlockData() instanceof Bisected blockDataBiselected &&
                    blockDataBiselected.getHalf() == Bisected.Half.TOP) {
                positionModifier.add(new Vector(0, 1, 0));
                pitch = 90 * 256.0F / 360.0F;
            }

            if(block.getBlockData() instanceof Directional blockDataDirectional) {
                loc.setDirection(blockDataDirectional.getFacing().getDirection());
                if (blockDataDirectional.getFacing() == BlockFace.NORTH) {
                    yaw = 0 * 256.0F / 360.0F;
                } else if (blockDataDirectional.getFacing() == BlockFace.EAST) {
                    yaw = 90 * 256.0F / 360.0F;
                    positionModifier.add(new Vector(1, 0, 0));
                } else if (blockDataDirectional.getFacing() == BlockFace.SOUTH) {
                    yaw = 180 * 256.0F / 360.0F;
                    positionModifier.add(new Vector(1, 0, 1));
                } else if (blockDataDirectional.getFacing() == BlockFace.WEST) {
                    yaw = 270 * 256.0F / 360.0F;
                    positionModifier.add(new Vector(0, 0, 1));
                } else {
                    yaw = 0;
                }
            }

            if(block.getBlockData() instanceof Slab bockDataSlab) {
                if (bockDataSlab.getType() == Slab.Type.TOP) {
                    positionModifier.add(new Vector(0, 0.5, 0));
                }
            }
        } else positionModifier.add(new Vector(0.5, 0, 0.5));

        entityLocation = loc.clone();
        entityLocation.add(positionModifier);
    }

    public void ensureVisiblity() {
        spawnEntity();
        createTeam();
    }

    public void ensureInvisibility() {
        destroyEntity();
        removeTeam();
    }

    private void spawnEntity() {
        switch (this.fullBlock) {
            case FullOpaque:
                spawnShulker();
                break;

            case FullTransparent:
                spawnMagmaCube();
                break;

            case Nonfull:
                spawnBlockDisplay();
                break;
        }
    }
    private void createTeam() {
        PacketTeam packetTeam = packetManager.createPacketTeam();
        packetTeam.setTeamName(getTeamName(receiver, entityLocation));
        packetTeam.setMode(PacketTeam.Mode.CREATE);

        packetTeam.setTeamColor(color);
        packetTeam.setCollisionRule(CollisionRule.NEVER);
        packetTeam.setEntities(Collections.singleton(entityUUID.toString()));

        try {
            packetManager.sendPacket(receiver, packetTeam);
        } catch (Exception ignored) {}
    }

    private void spawnBlockDisplay() {
        PacketContainer spawnEntityPacket = new FakeSpawnEntityPacket(
                entityId,
                entityUUID,
                EntityType.BLOCK_DISPLAY,
                entityLocation,
                (byte) pitch,
                (byte) yaw
        );

        protocolManager.sendServerPacket(receiver, spawnEntityPacket);

        float distance = 0.01f;
        float centerDiff = 0.002f;

        PacketContainer entityMetadataPacket = new FakeEntityMetadataPacket(
                entityId,
                block.getType(),
                new Vector3f(1 - distance, 1 - distance, 1 - distance),
                new Vector3f(
                        distance / 2,
                        distance / 2 - centerDiff,
                        distance / 2 - centerDiff
                )
        );
        protocolManager.sendServerPacket(receiver, entityMetadataPacket);
    }

    private void spawnMagmaCube() {
        PacketContainer spawnEntityPacket = new FakeSpawnEntityPacket(
                entityId,
                entityUUID,
                EntityType.MAGMA_CUBE,
                entityLocation,
                (byte) 0,
                (byte) 0
        );

        protocolManager.sendServerPacket(receiver, spawnEntityPacket);
        PacketContainer entityMetadataPacket = new FakeEntityMetadataPacket(
                entityId
        );
        protocolManager.sendServerPacket(receiver, entityMetadataPacket);
    }

    private void spawnShulker() {
        PacketContainer spawnEntityPacket = new FakeSpawnEntityPacket(
                entityId,
                entityUUID,
                EntityType.SHULKER,
                entityLocation,
                (byte) 0,
                (byte) 0
        );

        protocolManager.sendServerPacket(receiver, spawnEntityPacket);
        PacketContainer entityMetadataPacket = new FakeEntityMetadataPacket(
                entityId
        );
        protocolManager.sendServerPacket(receiver, entityMetadataPacket);
    }

    // TODO: Add shulker

    private void destroyEntity() {
        PacketContainer fakeRemoveEntitiesPacket = new FakeRemoveEntitiesPacket(Collections.singletonList(entityId));
        protocolManager.sendServerPacket(receiver, fakeRemoveEntitiesPacket);
    }

    private void removeTeam() {
        PacketTeam packetTeam = packetManager.createPacketTeam();
        packetTeam.setTeamName(getTeamName(receiver, block.getLocation()));
        packetTeam.setMode(PacketTeam.Mode.REMOVE);

        try {
            packetManager.sendPacket(receiver, packetTeam);
        } catch (Exception ignored) {}
    }

    public static String getTeamName(Player receiver, Location loc) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return "glowingBlocks-" + receiver.getName() + ":" + x + ":" + y + ":" + z;
    }
}
