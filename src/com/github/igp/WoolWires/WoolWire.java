package com.github.igp.WoolWires;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.NoteBlock;
import com.github.igp.WoolWires.WWConfiguration.WireConfiguration;

public class WoolWire {
	private final Block baseBlock;
	private final ArrayList<Block> wire;
	private final ArrayList<Block> mechanisms;
	private final WireConfiguration wConfig;
	private final byte color;

	public WoolWire(final Block baseBlock, final WireConfiguration wConfig) {
		this.baseBlock = baseBlock;
		this.wConfig = wConfig;
		wire = new ArrayList<Block>();
		mechanisms = new ArrayList<Block>();

		if (wConfig.getColor() == ((byte) 24))
			color = baseBlock.getData();
		else
			color = wConfig.getColor();

		wire.add(baseBlock);
		findWire();
		findMechanisms();
	}

	private void findWire() {
		for (int i = 0; i < wire.size(); i++) {
			for (final BlockFace f : Faces.getValidFaces()) {
				final Block b = wire.get(i).getRelative(f);

				if ((b.getType() == Material.WOOL) && (b.getData() == color) && (!wire.contains(b))) {
					wire.add(b);
				}
			}
		}
	}

	private void findMechanisms() {
		for (final Block b : wire) {
			for (final BlockFace f : Faces.getValidFaces()) {
				final Block mb = b.getRelative(f);

				if ((wConfig.getAllowedMaterials().contains(mb.getType())) && (!mechanisms.contains(mb)))
					mechanisms.add(mb);
			}
		}
	}

	public void setMechanismState(Boolean state) {
		if (wConfig.getType() == 1)
			state = !state;

		for (final Block b : mechanisms) {
			final BlockState bs = b.getState();
			final byte data = b.getData();
			final int newData;

			if (b.getType() == Material.LEVER) {
				if (!state)
					newData = data & 0x7;
				else
					newData = data | 0x8;

				if (newData != data)
					b.setData((byte) newData, true);

				continue;
			}

			if ((b.getType() == Material.FENCE_GATE) || b.getType() == Material.TRAP_DOOR) {
				if (!state)
					newData = data & 0x3;
				else
					newData = data | 0x4;

				if (newData != data)
					b.setData((byte) newData, true);

				continue;
			}

			if (b.getType() == Material.GLOWSTONE) {
				if (!state)
					b.setType(Material.GLASS);

				continue;
			}

			if (b.getType() == Material.GLASS) {
				if (state)
					b.setType(Material.GLOWSTONE);

				continue;
			}

			if (b.getType() == Material.NOTE_BLOCK) {
				if (state)
					((NoteBlock) bs).play();

				continue;
			}

			if (b.getType() == Material.DISPENSER) {
				if (state)
					((Dispenser) bs).dispense();

				continue;
			}
		}
	}

	public Block getBaseBlock() {
		return baseBlock;
	}

	public byte getColor() {
		return color;
	}

	public Boolean contains(final Block block) {
		if (wire.contains(block))
			return true;
		return false;
	}
}
