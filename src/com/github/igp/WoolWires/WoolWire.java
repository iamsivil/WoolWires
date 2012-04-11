package com.github.igp.WoolWires;

import java.util.ArrayList;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.NoteBlock;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.material.Button;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.igp.IGHelpers.BlockFaceHelper;
import com.github.igp.WoolWires.WWConfiguration.WireConfiguration;

public class WoolWire
{
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	private final Block baseBlock;
	private final byte color;
	private int size;
	private final ArrayList<Block> wire;
	private final ArrayList<Block> mechanisms;
	private final WireConfiguration config;

	public WoolWire(final Block baseBlock, final WireConfiguration config, final JavaPlugin plugin)
	{
		this.plugin = plugin;
		this.baseBlock = baseBlock;
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
			for (final BlockFace f : BlockFaceHelper.getAdjacentFaces())
			{
				final Block mb = b.getRelative(f);

				if (mb.getType().equals(Material.LEVER))
				{
					final Block attached = mb.getRelative(((Lever) mb.getState().getData()).getAttachedFace());
					if (attached.getType().equals(Material.WOOL) && (attached.getData() == config.getInputColor()))
						continue;
				}
				else if (mb.getType().equals(Material.STONE_BUTTON))
				{
					final Block attached = mb.getRelative(((Button) mb.getState().getData()).getAttachedFace());
					if (attached.getType().equals(Material.WOOL) && (attached.getData() == config.getInputColor()))
						continue;
				}
				else if (mb.getRelative(BlockFace.DOWN).getType().equals(Material.WOOL) && (mb.getRelative(BlockFace.DOWN).getData() == config.getInputColor()))
					continue;

				if (config.getValidMechanisms().contains(mb.getType()) && !mechanisms.contains(mb))
					mechanisms.add(mb);
			}
		}
	}

	private Boolean serverInteract(final Block b, final Boolean state)
	{
		Boolean blockState = null;

		if (b.getType().equals(Material.LEVER))
			blockState = (b.getData() & 8) != 0;

		if (b.getType().equals(Material.TRAP_DOOR))
			blockState = (b.getData() & 4) != 0;

		if (b.getType().equals(Material.WOODEN_DOOR) && !((Door) b.getState().getData()).isTopHalf())
			blockState = (b.getData() & 4) != 0;
		
		if (b.getType().equals(Material.STONE_BUTTON))
			blockState = (b.getData() & 8) != 0;
		

		if (blockState != null)
		{
			if (blockState != state)
				net.minecraft.server.Block.byId[b.getType().getId()].interact(((CraftWorld) b.getWorld()).getHandle(), b.getX(), b.getY(), b.getZ(), null);

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

			if (b.getType().equals(Material.FENCE_GATE))
			{
				final byte data = b.getData();
				final int newData = (byte) (state ? (data | 4) : (data & ~4));
				if (newData != data)
					b.setData((byte) newData, true);

				continue;
			}

			if (b.getType().equals(Material.GLOWSTONE))
			{
				if (!state)
					b.setType(Material.GLASS);

				continue;
			}

			if (b.getType().equals(Material.GLASS))
			{
				if (state)
					b.setType(Material.GLOWSTONE);

				continue;
			}

			if (b.getType().equals(Material.NOTE_BLOCK))
			{
				if (state)
					((NoteBlock) b.getState()).play();

				continue;
			}

			if (b.getType().equals(Material.DISPENSER))
			{
				if (state)
					((Dispenser) b.getState()).dispense();

				continue;
			}
		}
	}

	public Block getBaseBlock()
	{
		return baseBlock;
	}

	public byte getColor()
	{
		return color;
	}

	public DyeColor getDyeColor()
	{
		return DyeColor.getByData(color);
	}

	public Boolean contains(final Block block)
	{
		if (wire.contains(block))
			return true;
		return false;
	}
}
