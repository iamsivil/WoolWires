package com.github.igp.WoolWires;

import com.github.igp.IGLib.Helpers.BlockFaceHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.*;

import java.util.ArrayList;

class WWBlockListener implements Listener
{
	private final WWConfiguration config;
	
	public WWBlockListener(final WWConfiguration config)
	{
		this.config = config;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockRedstoneChanged(final BlockRedstoneEvent event)
	{
		Boolean state;
		if ((event.getNewCurrent() == 0) && (event.getOldCurrent() != 0))
			state = false;
		else if ((event.getNewCurrent() != 0) && (event.getOldCurrent() == 0))
			state = true;
		else
			return;

		final Block sourceBlock = event.getBlock();
		final Block inputBlock;

		switch (sourceBlock.getType())
		{
			case LEVER:
				inputBlock = sourceBlock.getRelative(((Lever) sourceBlock.getState().getData()).getAttachedFace());
				break;
			case STONE_BUTTON:
				inputBlock = sourceBlock.getRelative(((Button) sourceBlock.getState().getData()).getAttachedFace());
				break;
			case TRIPWIRE_HOOK:
				//TODO: Better way of doing this?  CB needs an update for this, also pretty slow
				state = ((TripwireHook) sourceBlock.getState().getData()).isActivated();
				inputBlock = sourceBlock.getRelative(((TripwireHook) sourceBlock.getState().getData()).getAttachedFace());
				break;
			//TODO: Figure out how to do diode detection | Diodes don't fire BlockRedstoneEvent
			//case DIODE:
			//case DIODE_BLOCK_OFF:
			//case DIODE_BLOCK_ON:
			case REDSTONE_TORCH_OFF:
			case REDSTONE_TORCH_ON:
				inputBlock = sourceBlock.getRelative(BlockFace.UP);
				break;
			//TODO: Allow for both redstone_wire from any side
			case REDSTONE_WIRE:
			case WOOD_PLATE:
			case STONE_PLATE:
			case DETECTOR_RAIL:
				inputBlock = sourceBlock.getRelative(BlockFace.DOWN);
				break;
			default:
				return;
		}

		if (!(inputBlock.getType().equals(Material.WOOL) && (inputBlock.getData() == config.getInputColor())))
			return;

		for (final BlockFace f : BlockFaceHelper.getAdjacentFaces())
		{
			final Block b = inputBlock.getRelative(f);

			if (!b.equals(sourceBlock))
			{
				switch (b.getType())
				{
					case REDSTONE_WIRE:
						if (b.getRelative(BlockFace.DOWN).equals(inputBlock))
						{
							if (((RedstoneWire) b.getState().getData()).isPowered())
								return;
						}
						break;
					case REDSTONE_TORCH_ON:
						if (b.getRelative(BlockFace.UP).equals(inputBlock))
							return;
						break;
					case WOOD_PLATE:
					case STONE_PLATE:
						if (b.getRelative(BlockFace.DOWN).equals(inputBlock))
						{
							if (((PressurePlate) b.getState().getData()).isPressed())
								return;
						}
						break;
					case DETECTOR_RAIL:
						if (b.getRelative(BlockFace.DOWN).equals(inputBlock))
						{
							if (((DetectorRail) b.getState().getData()).isPressed())
								return;
						}
						break;
					case LEVER:
						if (b.getRelative(((Lever) b.getState().getData()).getAttachedFace()).equals(inputBlock))
						{
							if (((Lever) b.getState().getData()).isPowered())
								return;
						}
						break;
					case STONE_BUTTON:
						if (b.getRelative(((Button) b.getState().getData()).getAttachedFace()).equals(inputBlock))
						{
							if (((Button) b.getState().getData()).isPowered())
								return;
						}
						break;
					case TRIPWIRE_HOOK:
						if (b.getRelative(((TripwireHook) b.getState().getData()).getAttachedFace()).equals(inputBlock))
						{
							if (((TripwireHook) b.getState().getData()).isPowered())
								return;
						}
						break;
				}
			}
		}

		changeWireState(inputBlock, state);
	}

	void changeWireState(final Block inputBlock, final Boolean state)
	{
		final ArrayList<WoolWire> wires = new ArrayList<WoolWire>(5);

		for (final BlockFace f : BlockFaceHelper.getAdjacentFaces())
		{
			final Block b = inputBlock.getRelative(f);
			Boolean exists = false;

			if (!b.getType().equals(Material.WOOL) || (b.getData() == config.getInputColor()))
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

			wires.add(new WoolWire(b, config.getWireConfiguration(b.getData())));
		}

		for (final WoolWire wire : wires)
			wire.setMechanismState(state);
	}
}
