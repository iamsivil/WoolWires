package com.github.igp.WoolWires;

import org.bukkit.block.BlockFace;

public class Faces {
	private final static BlockFace[] faces = { BlockFace.UP, BlockFace.DOWN,
			BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

	public static BlockFace[] getValidFaces() {
		return faces;
	}
}
