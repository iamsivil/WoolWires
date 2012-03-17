package com.github.igp.PowerGridRemix;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.NoteBlock;
import org.bukkit.plugin.java.JavaPlugin;

public class WoolWire {
	private final Block baseBlock;
	private final byte color;
	private final ArrayList<Block> wire;
	private final ArrayList<Block> mechanisms;
	@SuppressWarnings("unused")
	private final Logger log;

	public WoolWire(final Block baseBlock, final JavaPlugin plugin)
	{
		this.baseBlock = baseBlock;
		this.color = baseBlock.getData();
		log = plugin.getLogger();
		wire = new ArrayList<Block>();
		mechanisms = new ArrayList<Block>();

		wire.add(baseBlock);
		findWire();
		findMechanisms();
	}

	private void findWire()
	{
		for (int i = 0; i < wire.size(); i++)
		{
			for (final BlockFace f : Faces)
			{
				final Block b = wire.get(i).getRelative(f);

				if ((b.getType() == Material.WOOL) && (b.getData() == color) && (!wire.contains(b)))
				{
					wire.add(b);
				}
			}
		}
	}

	private void findMechanisms()
	{
		final ArrayList<Material> validMechanisms = new ArrayList<Material>(7);
		validMechanisms.add(Material.LEVER);
		validMechanisms.add(Material.FENCE_GATE);
		validMechanisms.add(Material.TRAP_DOOR);
		validMechanisms.add(Material.GLOWSTONE);
		validMechanisms.add(Material.GLASS);
		validMechanisms.add(Material.NOTE_BLOCK);
		validMechanisms.add(Material.DISPENSER);

		for (final Block b : wire)
		{
			for (final BlockFace f : Faces)
			{
				final Block mb = b.getRelative(f);

				if ((validMechanisms.contains(mb.getType())) && (!mechanisms.contains(mb)))
					mechanisms.add(mb);
			}
		}
	}

	public void setMechanismState(final Boolean state)
	{
		for (final Block b : mechanisms)
		{
			final BlockState bs = b.getState();
			final byte data = b.getData();
			final int newData;

			if (b.getType() == Material.LEVER)
			{		
				if (!state)
					newData = data & 0x7;
				else
					newData = data | 0x8;

				if (newData != data) 
					b.setData((byte)newData, true);

				continue;
			}

			if ((b.getType() == Material.FENCE_GATE) || b.getType() == Material.TRAP_DOOR)
			{
				if (!state)
					newData = data & 0x3;
				else
					newData = data | 0x4;

				if (newData != data)
					b.setData((byte)newData, true);

				continue;
			}

			if (b.getType() == Material.GLOWSTONE)
			{
				if (!state)
					b.setType(Material.GLASS);

				continue;
			}

			if (b.getType() == Material.GLASS)
			{			
				if (state)
					b.setType(Material.GLOWSTONE);

				continue;
			}

			if (b.getType() == Material.NOTE_BLOCK)
			{
				if (state)
					((NoteBlock)bs).play();

				continue;
			}

			if (b.getType() == Material.DISPENSER)
			{
				if (state)
					((Dispenser)bs).dispense();

				continue;
			}
		}
	}

	public Block getBaseBlock() { return baseBlock; }

	public byte getColor() { return color; }

	public DyeColor getDyeColor() { return DyeColor.getByData(color); } 

	public Boolean contains(final Block block)
	{
		if (wire.contains(block))
			return true;
		return false;
	}

	private final BlockFace[] Faces =
		{
			BlockFace.UP,
			BlockFace.DOWN,
			BlockFace.NORTH,
			BlockFace.EAST,
			BlockFace.SOUTH,
			BlockFace.WEST
		};
}
