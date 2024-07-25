package me.msmaciek.redefinedglowingblocks;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.msmaciek.redefinedglowingblocks.enums.FullBlockEnum;
import me.msmaciek.redefinedglowingblocks.listeners.EventListener;
import me.msmaciek.redefinedglowingblocks.structs.GlowingBlock;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("unused")
public class GlowingBlocksAPI {
    @Getter private HashMap<UUID, HashMap<Location, GlowingBlock>> glowingBlocks = new HashMap<>();
    @Getter private static GlowingBlocksAPI instance;

    public GlowingBlocksAPI(JavaPlugin plugin) {
        Utils.testForPaper();

        if(PacketEvents.getAPI().isLoaded())
            return;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));

        PacketEvents.getAPI().getSettings()
                .reEncodeByDefault(true)
                .checkForUpdates(false)
                .bStats(false);

        PacketEvents.getAPI().load();
        instance = this;

        plugin.getServer().getPluginManager().registerEvents(new EventListener(), plugin);
    }

    public void setGlowing(Player receiver, Location blockLocation, NamedTextColor color) {
        setGlowing(receiver, blockLocation.getBlock(), color);
    }

    public void setGlowing(Player receiver, Block block, NamedTextColor color) {
        setGlowing(receiver, block, color, FullBlockEnum.Detect);
    }

    public void setGlowing(Player receiver, Location blockLocation, NamedTextColor color, FullBlockEnum fullBlock) {
        setGlowing(receiver, blockLocation.getBlock(), color, fullBlock);
    }

    public void setGlowing(Player receiver, Block block, NamedTextColor color, FullBlockEnum fullBlock) {
        if(receiver == null || !receiver.isOnline())
            return;

        UUID receiverUUID = receiver.getUniqueId();
        Location blockLocation = block.getLocation();

        glowingBlocks.putIfAbsent(receiverUUID, new HashMap<>());

        GlowingBlock glowingBlock = new GlowingBlock(receiver, block, color, fullBlock);

        if(glowingBlocks.get(receiverUUID).containsKey(blockLocation))
            glowingBlocks.get(receiverUUID).get(blockLocation).RewriteGlowingBlock(glowingBlock);
        else
            glowingBlocks.get(receiverUUID).put(blockLocation, glowingBlock);

        glowingBlocks.get(receiverUUID).get(blockLocation).ensureVisiblity();
    }

    public void unsetGlowing(Player receiver, Location blockLocation) {
        unsetGlowing(receiver, blockLocation.getBlock());
    }

    public void unsetGlowing(Player receiver, Block block) {
        if(receiver == null || !receiver.isOnline())
            return;

        if(!isGlowing(receiver, block))
            return;

        UUID receiverUUID = receiver.getUniqueId();
        Location blockLocation = block.getLocation();

        glowingBlocks.get(receiverUUID).get(blockLocation).ensureInvisibility();
        glowingBlocks.get(receiverUUID).remove(blockLocation);

        if(glowingBlocks.get(receiverUUID).isEmpty())
            glowingBlocks.remove(receiverUUID);
    }

    public boolean isGlowing(Player receiver, Location blockLocation) {
        return isGlowing(receiver, blockLocation.getBlock());
    }

    public boolean isGlowing(Player receiver, Block block) {
        if(receiver == null || !receiver.isOnline())
            return false;

        return glowingBlocks.containsKey(receiver.getUniqueId())
                && glowingBlocks.get(receiver.getUniqueId()).containsKey(block.getLocation());
    }
}
