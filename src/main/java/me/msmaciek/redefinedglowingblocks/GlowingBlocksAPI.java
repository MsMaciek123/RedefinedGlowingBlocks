package me.msmaciek.redefinedglowingblocks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.eliab.sbcontrol.SbControl;
import me.eliab.sbcontrol.network.PacketManager;
import me.msmaciek.redefinedglowingblocks.enums.FullBlockEnum;
import me.msmaciek.redefinedglowingblocks.listeners.EventListener;
import me.msmaciek.redefinedglowingblocks.structs.GlowingBlock;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class GlowingBlocksAPI {

    public HashMap<UUID, HashMap<Location, GlowingBlock>> glowingBlocks = new HashMap<>();

    private JavaPlugin plugin;
    private ProtocolManager protocolManager;
    private PacketManager packetManager;

    public static GlowingBlocksAPI instance;

    public GlowingBlocksAPI(JavaPlugin plugin) {
        testForPaper();

        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.packetManager = SbControl.getPacketManager();
        instance = this;

        plugin.getServer().getPluginManager().registerEvents(new EventListener(), plugin);
    }

    private void testForPaper() {
        try {
            Class.forName("io.papermc.paper.event.packet.PlayerChunkLoadEvent");
        } catch (ClassNotFoundException ex) {
            throw new UnsupportedOperationException("Running unsupported software. Please use Paper.");
        }
    }

    public void setGlowing(Player receiver, Block block, ChatColor color) {
        setGlowing(receiver, block, color, FullBlockEnum.Detect);
    }

    public void setGlowing(Player receiver, Block block, ChatColor color, FullBlockEnum fullBlock) {
        glowingBlocks.putIfAbsent(receiver.getUniqueId(), new HashMap<>());

        if(!glowingBlocks.get(receiver.getUniqueId()).containsKey(block.getLocation()))
            glowingBlocks.get(receiver.getUniqueId()).put(block.getLocation(), new GlowingBlock(receiver, block, color, fullBlock));

        glowingBlocks.get(receiver.getUniqueId()).get(block.getLocation()).ensureVisiblity();
    }

    public void unsetGlowing(Player receiver, Block block) {
        if(glowingBlocks.containsKey(receiver.getUniqueId())
                && glowingBlocks.get(receiver.getUniqueId()).containsKey(block.getLocation())) {

            glowingBlocks.get(receiver.getUniqueId()).get(block.getLocation()).ensureInvisibility();
            glowingBlocks.get(receiver.getUniqueId()).remove(block.getLocation());

            if(glowingBlocks.get(receiver.getUniqueId()).isEmpty())
                glowingBlocks.remove(receiver.getUniqueId());
        }
    }
}
