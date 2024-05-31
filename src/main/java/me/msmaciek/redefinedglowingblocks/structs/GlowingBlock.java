package me.msmaciek.redefinedglowingblocks.structs;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import lombok.Getter;
import me.msmaciek.redefinedglowingblocks.Utils;
import me.msmaciek.redefinedglowingblocks.enums.FullBlockEnum;
import me.msmaciek.redefinedglowingblocks.packets.FakeEntityMetadataPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class GlowingBlock {
    private static final AtomicInteger ENTITY_ID_COUNTER =
        new AtomicInteger(ThreadLocalRandom.current().nextInt(1_000_000, 2_000_000_000));

    private final Player receiver;
    private final int entityId;
    private final UUID entityUUID;
    private final Block block;
    private NamedTextColor color;

    private FullBlockEnum fullBlock;
    private Location entityLocation;

    public GlowingBlock(Player receiver, Block block, NamedTextColor color, FullBlockEnum fullBlock) {
        this.receiver = receiver;

        this.entityId = ENTITY_ID_COUNTER.getAndIncrement();
        this.entityUUID = UUID.randomUUID();

        this.block = block;
        this.color = color;

        this.fullBlock = fullBlock;
        if(fullBlock.equals(FullBlockEnum.Detect)) {
            if(block.getType().isOccluding())
                this.fullBlock = FullBlockEnum.FullOpaque;
            else if(Utils.isCube(block))
                this.fullBlock = FullBlockEnum.FullTransparent;
            else
                this.fullBlock = FullBlockEnum.Nonfull;
        }

        calculateEntityLocation();
    }

    public void RewriteGlowingBlock(GlowingBlock otherGlowingBlock) {
        // TODO: Recalculate fullBlock and entity location
        this.color = otherGlowingBlock.color;
    }

    void calculateEntityLocation() {
        Location loc = block.getLocation();
        Vector positionModifier = new Vector(0, 0, 0);
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

    //#region entities
    private void spawnEntity() {
        switch (this.fullBlock) {
            case FullOpaque:
                spawnEntity(EntityTypes.SHULKER);
                break;

            case FullTransparent:
                spawnEntity(EntityTypes.MAGMA_CUBE, 2);
                break;

            case Nonfull:
                spawnEntity(EntityTypes.BLOCK_DISPLAY);
                break;
        }
    }

    private void spawnEntity(EntityType entityType) {
        spawnEntity(entityType, 2);
    }

    private void spawnEntity(EntityType entityType, int magmaSize) {
        var spawnEntityPacket = new WrapperPlayServerSpawnEntity(
            entityId,
            entityUUID,
            entityType,
            new com.github.retrooper.packetevents.protocol.world.Location(
                entityLocation.getX(),
                entityLocation.getY(),
                entityLocation.getZ(),
                entityLocation.getYaw(),
                entityLocation.getPitch()
            ),
            0, 0, null
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(receiver, spawnEntityPacket);

        FakeEntityMetadataPacket entityMetadataPacket = new FakeEntityMetadataPacket(entityId);

        if(entityType == EntityTypes.BLOCK_DISPLAY)
            entityMetadataPacket = getBlockDisplayEntityMedatataPacket();
        else if(entityType == EntityTypes.MAGMA_CUBE)
            entityMetadataPacket = new FakeEntityMetadataPacket(entityId, magmaSize);

        PacketEvents.getAPI().getPlayerManager().sendPacket(receiver, entityMetadataPacket);
    }

    private void destroyEntity() {
        var teamRemovePacket = new WrapperPlayServerDestroyEntities(entityId);
        PacketEvents.getAPI().getPlayerManager().sendPacket(receiver, teamRemovePacket);
    }
    //#endregion

    private FakeEntityMetadataPacket getBlockDisplayEntityMedatataPacket() {
        float distance = 0.01f;
        float centerDiff = 0.002f;

        return new FakeEntityMetadataPacket(
            entityId,
            block,
            new Vector3f(1 - distance, 1 - distance, 1 - distance),
            new Vector3f(
                distance / 2,
                distance / 2 - centerDiff,
                distance / 2 - centerDiff
            )
        );
    }


    //#region teams
    private void createTeam() {
        var teamCreatePacket = new WrapperPlayServerTeams(
            getTeamName(receiver, entityLocation),
            WrapperPlayServerTeams.TeamMode.CREATE,
            new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.empty(),
                null,
                null,
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.NEVER,
                color,
                WrapperPlayServerTeams.OptionData.NONE
            ),
            Collections.singletonList(entityUUID.toString())
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(receiver, teamCreatePacket);
    }

    private void removeTeam() {
        var teamRemovePacket = new WrapperPlayServerTeams(
            getTeamName(receiver, block.getLocation()),
            WrapperPlayServerTeams.TeamMode.REMOVE,
            new WrapperPlayServerTeams.ScoreBoardTeamInfo(null, null,null,null,null,null,null)
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(receiver, teamRemovePacket);
    }

    public static String getTeamName(Player receiver, Location loc) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return "glowingBlocks-" + receiver.getName() + ":" + x + ":" + y + ":" + z;
    }
    //#endregion
}
