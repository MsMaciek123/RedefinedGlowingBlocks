# Redefined Glowing Blocks

**Redefined Glowing Blocks** is an API for developers to make blocks glowing. \
Supported Minecraft versions: 1.20+ (due to use of block displays) \
Dependencies: ProtocolLib \
Any block could glow as full block or as depending on its texture. Some blocks with a lot of states like levers, buttons, flowers, candles may not work properly (use full block glow then).

Example usage (this is your main class implementing listener):
```java
GlowingBlocksAPI gbapi;

@Override
public void onEnable() {
    gbapi = new GlowingBlocksAPI(this);
    getServer().getPluginManager().registerEvents(this, this);
    // rest of your code
}

@EventHandler
public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Block block = event.getClickedBlock();

    gbapi.setGlowing(player, block, ChatColor.BLUE);

    getServer().getScheduler().runTaskLater(this, () ->
        gbapi.unsetGlowing(player, block),
    20);
}
```


Gradle is comming soon