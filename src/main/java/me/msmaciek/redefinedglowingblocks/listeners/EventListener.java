package me.msmaciek.redefinedglowingblocks.listeners;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import me.msmaciek.redefinedglowingblocks.GlowingBlocksAPI;
import me.msmaciek.redefinedglowingblocks.structs.GlowingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class EventListener implements Listener {
    @EventHandler
    public void PlayerChunkLoadEvent(PlayerChunkLoadEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        if(!GlowingBlocksAPI.instance.glowingBlocks.containsKey(playerUUID))
            return;

        Collection<GlowingBlock> glowingBlocks = GlowingBlocksAPI.instance.glowingBlocks.get(playerUUID).values();

        // https://github.com/SkytAsul/GlowingEntities/blob/master/src/main/java/fr/skytasul/glowingentities/GlowingBlocks.java
        glowingBlocks.forEach((glowingBlock) -> {
            if (Objects.equals(glowingBlock.block.getLocation().getWorld(), event.getWorld())
                    && glowingBlock.block.getLocation().getBlockX() >> 4 == event.getChunk().getX()
                    && glowingBlock.block.getLocation().getBlockZ() >> 4 == event.getChunk().getZ()) {
                glowingBlock.ensureVisiblity();
            }
        });
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        GlowingBlocksAPI.instance.glowingBlocks.remove(event.getPlayer().getUniqueId());
    }
}
