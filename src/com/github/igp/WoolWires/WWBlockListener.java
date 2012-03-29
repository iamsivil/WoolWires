package com.github.igp.WoolWires;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.DetectorRail;
import org.bukkit.material.Lever;
import org.bukkit.material.Button;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PressurePlate;
import org.bukkit.material.RedstoneWire;
import org.bukkit.material.RedstoneTorch;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.igp.IGHelpers.BlockFaces;

public class WWBlockListener implements Listener
{
	private final JavaPlugin plugin;
	private WWConfiguration config;
	// temp
	private final byte inputColor = DyeColor.BROWN.getData();

	//

	public WWBlockListener(final JavaPlugin plugin)
	{
		this.plugin = plugin;
		config = new WWConfiguration(plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockRedstoneChanged(final BlockRedstoneEvent event)
	{
		final Boolean state;
		if ((event.getNewCurrent() == 0) && (event.getOldCurrent() != 0))
			state = false;
		else if ((event.getNewCurrent() != 0) && (event.getOldCurrent() == 0))
			state = true;
		else
			return;

		final Block sourceBlock = event.getBlock();
		final Block inputBlock;

		if (sourceBlock.getType().equals(Material.LEVER))
			inputBlock = sourceBlock.getRelative(((Lever) sourceBlock.getState().getData()).getAttachedFace());
		else if (sourceBlock.getType().equals(Material.REDSTONE_TORCH_ON) || sourceBlock.getType().equals(Material.REDSTONE_TORCH_OFF))
			inputBlock = sourceBlock.getRelative(((RedstoneTorch) sourceBlock.getState().getData()).getAttachedFace());
		else if (sourceBlock.getType().equals(Material.STONE_BUTTON))
			inputBlock = sourceBlock.getRelative(((Button) sourceBlock.getState().getData()).getAttachedFace());
		else if (sourceBlock.getType().equals(Material.REDSTONE_WIRE))
			inputBlock = sourceBlock.getRelative(BlockFace.DOWN);
		else if (sourceBlock.getType().equals(Material.WOOD_PLATE) || sourceBlock.getType().equals(Material.STONE_PLATE))
			inputBlock = sourceBlock.getRelative(BlockFace.DOWN);
		else if (sourceBlock.getType().equals(Material.DETECTOR_RAIL))
			inputBlock = sourceBlock.getRelative(BlockFace.DOWN);
		else
			return;

		if (!(inputBlock.getType().equals(Material.WOOL) && (inputBlock.getData() == inputColor)))
			return;

		final List<Block> secondarySourceBlocks = new ArrayList<Block>(6);

		for (final BlockFace f : BlockFaces.getAdjacentFaces())
		{
			final Block b = inputBlock.getRelative(f);

			if (!b.equals(sourceBlock))
			{
				if (b.getType().equals(Material.LEVER) || b.getType().equals(Material.STONE_BUTTON) || b.getType().equals(Material.REDSTONE_WIRE) || b.getType().equals(Material.REDSTONE_TORCH_ON) || b.getType().equals(Material.REDSTONE_TORCH_OFF))
					secondarySourceBlocks.add(b);
			}
		}

		for (final Block b : secondarySourceBlocks)
		{
			if (b.getType().equals(Material.REDSTONE_WIRE))
			{
				if (b.getRelative(BlockFace.DOWN).equals(inputBlock))
				{
					if (((RedstoneWire) b.getState().getData()).isPowered())
						return;
				}
			}
			
			if (b.getType().equals(Material.WOOD_PLATE) || b.getType().equals(Material.STONE_PLATE))
			{
				if (b.getRelative(BlockFace.DOWN).equals(inputBlock))
				{
					if (((PressurePlate) b.getState().getData()).isPressed())
						return;
				}
			}
			
			if (b.getType().equals(Material.DETECTOR_RAIL))
			{
				if (b.getRelative(BlockFace.DOWN).equals(inputBlock))
				{
					if (((DetectorRail) b.getState().getData()).isPressed())
						return;
				}
			}

			final MaterialData md = b.getState().getData();

			if (b.getType().equals(Material.LEVER))
			{
				if (b.getRelative(((Lever) md).getAttachedFace()).equals(inputBlock))
				{
					if (((Lever) md).isPowered())
						return;
				}
			}

			if (b.getType().equals(Material.STONE_BUTTON))
			{
				if (b.getRelative(((Button) md).getAttachedFace()).equals(inputBlock))
				{
					if (((Button) md).isPowered())
						return;
				}
			}

			if (b.getType().equals(Material.REDSTONE_TORCH_ON) || b.getType().equals(Material.REDSTONE_TORCH_OFF))
			{
				if (b.getRelative(((RedstoneTorch) md).getAttachedFace()).equals(inputBlock))
				{
					if (((RedstoneTorch) md).isPowered())
						return;
				}
			}

		}

		changeWireState(inputBlock, state);
	}

	public void changeWireState(final Block inputBlock, final Boolean state)
	{
		final ArrayList<WoolWire> wires = new ArrayList<WoolWire>(5);

		for (final BlockFace f : BlockFaces.getAdjacentFaces())
		{
			final Block b = inputBlock.getRelative(f);
			Boolean exists = false;

			if (!b.getType().equals(Material.WOOL) || (b.getData() == inputColor))
				continue;

			for (final WoolWire wire : wires)
			{
				if ((wire.getColor() == b.getData()) && wire.contains(b))
				{
					exists = true;
					break;
				}
			}

			if (exists)
				continue;

			wires.add(new WoolWire(b, plugin));
		}

		for (final WoolWire wire : wires)
			wire.setMechanismState(state);
	}

}
