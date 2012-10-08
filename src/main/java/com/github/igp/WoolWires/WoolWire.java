package com.github.igp.WoolWires;

import com.github.igp.IGLib.Helpers.BlockFaceHelper;
import com.github.igp.WoolWires.WWConfiguration.WireConfiguration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.NoteBlock;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.material.Button;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;

import java.util.ArrayList;

class WoolWire
{
	private final byte color;
	private int size;
	private final ArrayList<Block> wire;
	private final ArrayList<Block> mechanisms;
	private final WireConfiguration config;

	public WoolWire(final Block baseBlock, final WireConfiguration config)
	{
		this.color = baseBlock.getData();
		this.config = config;
		wire = new ArrayList<Block>();
		mechanisms = new ArrayList<Block>();
		size = 1;

		wire.add(baseBlock);
		findWire();
		findMechanisms();
	}

	private void findWire()
	{
		for (int i = 0; i < wire.size(); i++)
		{
			for (final BlockFace f : BlockFaceHelper.getAdjacentFaces())
			{
				final Block b = wire.get(i).getRelative(f);

				if (b.getType().equals(Material.WOOL) && (b.getData() == color) && !wire.contains(b) && (size < config.getMaxSize()))
				{
					wire.add(b);
					size++;
				}
			}
		}
	}

	private void findMechanisms()
	{
		for (final Block b : wire)
		{
			if (b.getData() == config.getInputColor())
				continue;

			for (final BlockFace f : BlockFaceHelper.getAdjacentFaces())
			{
				final Block mb = b.getRelative(f);

				switch (mb.getType())
				{
					case LEVER:
						if (b != mb.getRelative(((Lever)mb.getState().getData()).getAttachedFace()))
							continue;
						break;
					case STONE_BUTTON:
						if (b != mb.getRelative(((Button)mb.getState().getData()).getAttachedFace()))
							continue;
						break;
				}

				if (config.getValidMechanisms().contains(mb.getType()) && !mechanisms.contains(mb))
					mechanisms.add(mb);
			}
		}
	}

	private Boolean serverInteract(final Block b, final Boolean state)
	{
		Boolean blockState = null;

		switch (b.getType())
		{
			case LEVER:
			case STONE_BUTTON:
				blockState = (b.getData() & 8) != 0;
				break;
			case WOODEN_DOOR:
				if (((Door) b.getState().getData()).isTopHalf())
					return false;
			case TRAP_DOOR:
				blockState = (b.getData() & 4) != 0;
				break;
		}

		if (blockState != null)
		{
			if (blockState != state)
				net.minecraft.server.Block.byId[b.getType().getId()].interact(((CraftWorld)b.getWorld()).getHandle(), b.getX(), b.getY(), b.getZ(), null, 0, 0F, 0F, 0F);
			
			return true;
		}
		return false;
	}

	public void setMechanismState(boolean state)
	{
		if (config.getType() == 1)
			state = !state;

		for (final Block b : mechanisms)
		{
			if (serverInteract(b, state))
				continue;

			switch (b.getType())
			{
				case FENCE_GATE:
					final byte data = b.getData();
					final int newData = (byte) (state ? (data | 4) : (data & ~4));
					if (newData != data)
						b.setData((byte) newData, true);
					break;
				case GLOWSTONE:
					if (!state)
						b.setType(Material.GLASS);
					break;
				case GLASS:
					if (state)
						b.setType(Material.GLOWSTONE);
					break;
				case NOTE_BLOCK:
					if (state)
						((NoteBlock) b.getState()).play();
					break;
				case DISPENSER:
					if (state)
						((Dispenser) b.getState()).dispense();
					break;
			}
		}
	}

	public byte getColor()
	{
		return color;
	}

	public Boolean contains(final Block block)
	{
		return wire.contains(block);
	}
}
