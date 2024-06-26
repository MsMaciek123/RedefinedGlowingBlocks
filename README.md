# Redefined Glowing Blocks
![Release](https://jitpack.io/v/MsMaciek123/RedefinedGlowingBlocks.svg)

**Redefined Glowing Blocks** is an API for developers to make blocks glowing. \
Supported Minecraft versions: 1.20+ (due to use of block displays) \
Dependencies: PacketEvents \
Any block could glow as full block or as depending on its texture.

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

    gbapi.setGlowing(player, block, NamedTextColor.BLUE);

    getServer().getScheduler().runTaskLater(this, () ->
        gbapi.unsetGlowing(player, block),
    20);
}
```

Additionally, you can pass FullBlockEnum to change how glowing will affect block. \
FullOpaque - uses shulkers (can place blocks on them) \
FullTransparent - uses magma cubes (can't place blocks on them) \
Nonfull - uses display block (can place blocks on them) \
Detect - detects based on block type

Replace VERSION with current version. \
Gradle:
```gradle
repositories {
    mavenCentral()
    maven { url "https://jitpack.io" }
}
   
dependencies {
    implementation 'com.github.MsMaciek123:RedefinedGlowingBlocks:VERSION'
}
```

Maven:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.MsMaciek123</groupId>
    <artifactId>RedefinedGlowingBlocks</artifactId>
    <version>VERSION</version>
</dependency>
```