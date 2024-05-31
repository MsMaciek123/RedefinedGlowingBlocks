package me.msmaciek.redefinedglowingblocks.listeners;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import me.msmaciek.redefinedglowingblocks.GlowingBlocksAPI;
import me.msmaciek.redefinedglowingblocks.structs.GlowingBlock;
import org.bukkit.Location;
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

        GlowingBlocksAPI instance = GlowingBlocksAPI.getInstance();
        if(!instance.getGlowingBlocks().containsKey(playerUUID))
            return;

        Collection<GlowingBlock> glowingBlocks = instance.getGlowingBlocks().get(playerUUID).values();

        // https://github.com/SkytAsul/GlowingEntities/blob/master/src/main/java/fr/skytasul/glowingentities/GlowingBlocks.java
        for(GlowingBlock glowingBlock : glowingBlocks) {
            Location loc = glowingBlock.getBlock().getLocation();
            if (Objects.equals(loc.getWorld(), event.getWorld())
                    && loc.getBlockX() >> 4 == event.getChunk().getX()
                    && loc.getBlockZ() >> 4 == event.getChunk().getZ()) {
                glowingBlock.ensureVisiblity();
            }
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        GlowingBlocksAPI.getInstance().getGlowingBlocks().remove(
            event.getPlayer().getUniqueId()
        );
    }
}
