package me.msmaciek.redefinedglowingblocks;

import me.msmaciek.redefinedglowingblocks.enums.FullBlockEnum;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class RedefinedGlowingBlocks extends JavaPlugin implements Listener {

    GlowingBlocksAPI gbapi;

    @Override
    public void onEnable() {
        gbapi = new GlowingBlocksAPI(this);
        Player player = getServer().getPlayer("MsMaciek12345");
        Block block = getServer().getWorld("lobby").getBlockAt(-105, 51, 72);

        gbapi.setGlowing(player, block, ChatColor.GREEN);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if(block == null) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        System.out.println(block.getType());
        System.out.println(block.getLocation());

        gbapi.setGlowing(player, block, ChatColor.BLUE, FullBlockEnum.Force);

       /* getServer().getScheduler().runTaskLater(this, () ->
                        // gbapi.unsetGlowing(player, block),
        60);*/
    }
}
