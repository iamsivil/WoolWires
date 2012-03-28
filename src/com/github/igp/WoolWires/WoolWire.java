package com.github.igp.WoolWires;

import java.util.ArrayList;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.NoteBlock;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

public class WoolWire
{
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	private final Block baseBlock;
	private final byte color;
	private final ArrayList<Block> wire;
	private final ArrayList<Block> mechanisms;

	// temp
	final ArrayList<Material> validMechanisms = new ArrayList<Material>(7);
	private final byte inputColor = DyeColor.BROWN.getData();
	//

	public WoolWire(final Block baseBlock, final JavaPlugin plugin)
	{
		this.plugin = plugin;
		this.baseBlock = baseBlock;
		this.color = baseBlock.getData();
		wire = new ArrayList<Block>();
		mechanisms = new ArrayList<Block>();

		// temp
		validMechanisms.add(Material.LEVER);
		validMechanisms.add(Material.FENCE_GATE);
		validMechanisms.add(Material.TRAP_DOOR);
		validMechanisms.add(Material.GLOWSTONE);
		validMechanisms.add(Material.GLASS);
		validMechanisms.add(Material.NOTE_BLOCK);
		validMechanisms.add(Material.DISPENSER);
		validMechanisms.add(Material.WOODEN_DOOR);
		//

		wire.add(baseBlock);
		findWire();
		findMechanisms();
	}

	private void findWire()
	{
		for (int i = 0; i < wire.size(); i++)
		{
			for (final BlockFace f : BlockFaces.getAdjacentFaces())
			{
				final Block b = wire.get(i).getRelative(f);

				if (b.getType().equals(Material.WOOL) && (b.getData() == color) && !wire.contains(b))
					wire.add(b);
			}
		}
	}

	private void findMechanisms()
	{

		for (final Block b : wire)
		{
			for (final BlockFace f : BlockFaces.getAdjacentFaces())
			{
				final Block mb = b.getRelative(f);				
				
				if (mb.getRelative(BlockFace.DOWN).getType().equals(Material.WOOL) && (mb.getRelative(BlockFace.DOWN).getData() == inputColor))
					continue;
				
				if (validMechanisms.contains(mb.getType()) && !mechanisms.contains(mb))
					mechanisms.add(mb);
			}
		}
	}
	
	private Boolean serverInteract(final Block b, final Boolean state)
	{
		Boolean blockState = null;
		
		if (b.getType().equals(Material.LEVER))
			blockState = (b.getData() & 0x8) != 0;
		
		if (b.getType().equals(Material.TRAP_DOOR) || b.getType().equals(Material.WOODEN_DOOR))
			blockState = (b.getData() & 0x4) != 0;
		
		if (blockState != null)
		{
			if (blockState != state)
				net.minecraft.server.Block.byId[b.getType().getId()].interact(((CraftWorld)b.getWorld()).getHandle(), b.getX(), b.getY(), b.getZ(), null);
			
			return true;
		}
		return false;
	}

	public void setMechanismState(final Boolean state)
	{
		for (final Block b : mechanisms)
		{
			if (serverInteract(b, state))
				continue;
			
			if (b.getType().equals(Material.FENCE_GATE))
			{
				final byte data = b.getData();
				final int newData = (byte)(state ? (data | 0x4) : (data & 0x3));
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
