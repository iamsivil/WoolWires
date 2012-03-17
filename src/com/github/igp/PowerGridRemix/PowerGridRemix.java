package com.github.igp.PowerGridRemix;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PowerGridRemix extends JavaPlugin implements Listener 
{	
	private Logger log;

	byte inputColor;

	int maxGridSize;

	@Override
	public void onEnable()
	{
		log = this.getLogger();

		inputColor = DyeColor.BROWN.getData();

		maxGridSize = 1000;

		getServer().getPluginManager().registerEvents(this, this);

		log.info("Enabled.");
	}

	@Override
	public void onDisable()
	{
		log.info("Disabled.");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockRedstoneChanged(final BlockRedstoneEvent event)
	{
		final Block sourceBlock = event.getBlock();
		final Block inputBlock = sourceBlock.getRelative(BlockFace.DOWN);
		final Boolean state;

		if (!((inputBlock.getType() != Material.WOOL) || (inputBlock.getData() != inputColor)))
		{

			if (event.getNewCurrent() == 0) { state = false; }
			else { state = true; }

			changeGridState(inputBlock, state);
		}
	}

	public void changeGridState(final Block inputBlock, final Boolean state)
	{
		final ArrayList<WoolWire> wires = new ArrayList<WoolWire>(5);

		for (final BlockFace f : Faces)
		{
			final Block b = inputBlock.getRelative(f);
			Boolean exists = false;

			if ((b.getType() != Material.WOOL) || (b.getData() == inputColor))
				continue;

			for (final WoolWire wire : wires)
			{
				if ((wire.getColor() == b.getData()) && (wire.contains(b)))
				{
					exists = true;
					break;
				}
			}

			if (exists)
				continue;

			wires.add(new WoolWire(b, this));
		}

		for (final WoolWire wire : wires)
			wire.setMechanismState(state);
	}

	public BlockFace[] Faces =
		{
			BlockFace.UP,
			BlockFace.DOWN,
			BlockFace.NORTH,
			BlockFace.EAST,
			BlockFace.SOUTH,
			BlockFace.WEST
		};
}
