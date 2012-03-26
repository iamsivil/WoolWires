package com.github.igp.WoolWires;

import java.util.ArrayList;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WWBlockListener implements Listener {
	private final JavaPlugin plugin;
	// temp
	private final byte inputColor = DyeColor.BROWN.getData();

	//

	public WWBlockListener(final JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockRedstoneChanged(final BlockRedstoneEvent event) {
		final Block sourceBlock = event.getBlock();
		final Block inputBlock = sourceBlock.getRelative(BlockFace.DOWN);
		final Boolean state;

		if (!((inputBlock.getType() != Material.WOOL) || (inputBlock.getData() != inputColor))) {

			if ((event.getNewCurrent() == 0) && (event.getOldCurrent() != 0)) {
				state = false;
				changeWireState(inputBlock, state);
			}
			else if ((event.getNewCurrent() != 0) && (event.getOldCurrent() == 0)) {
				state = true;
				changeWireState(inputBlock, state);
			}
		}
	}

	public void changeWireState(final Block inputBlock, final Boolean state) {
		final ArrayList<WoolWire> wires = new ArrayList<WoolWire>(5);

		for (final BlockFace f : Faces.getAdjacentFaces()) {
			final Block b = inputBlock.getRelative(f);
			Boolean exists = false;

			if ((b.getType() != Material.WOOL) || (b.getData() == inputColor))
				continue;

			for (final WoolWire wire : wires) {
				if ((wire.getColor() == b.getData()) && (wire.contains(b))) {
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
