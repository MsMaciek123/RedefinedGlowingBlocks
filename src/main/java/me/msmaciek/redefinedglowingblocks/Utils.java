package me.msmaciek.redefinedglowingblocks;

import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.VoxelShape;

public class Utils {
	// https://www.spigotmc.org/threads/how-to-check-if-a-block-is-realy-a-block.536470/
	public static boolean isCube(Block block) {
		VoxelShape voxelShape = block.getCollisionShape();
		BoundingBox boundingBox = block.getBoundingBox();
		return (voxelShape.getBoundingBoxes().size() == 1
			&& boundingBox.getWidthX() == 1.0
			&& boundingBox.getHeight() == 1.0
			&& boundingBox.getWidthZ() == 1.0
		);
	}

	public static void testForPaper() {
		try {
			Class.forName("io.papermc.paper.event.packet.PlayerChunkLoadEvent");
		} catch (ClassNotFoundException ex) {
			throw new UnsupportedOperationException("Running unsupported software. Please use Paper.");
		}
	}
}
