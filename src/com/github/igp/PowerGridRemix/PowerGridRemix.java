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
	Logger log;

	byte unpwrdWoolColor;
	byte pwrdWoolColor;
	byte unpwrdInvWoolColor;
	byte pwrdInvWoolColor;
	byte pwrNodeWoolColor;
	int maxGridSize;

	@Override
	public void onEnable()
	{
		log = this.getLogger();

		unpwrdWoolColor = DyeColor.WHITE.getData();
		pwrdWoolColor = DyeColor.YELLOW.getData();
		unpwrdInvWoolColor = DyeColor.PURPLE.getData();
		pwrdInvWoolColor = DyeColor.PINK.getData();
		pwrNodeWoolColor = DyeColor.BROWN.getData();

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
		final Block baseBlock = sourceBlock.getRelative(BlockFace.DOWN);

		if ((baseBlock.getType() == Material.WOOL) && (sourceBlock.getType() == Material.REDSTONE_WIRE) || (baseBlock.getData() == pwrNodeWoolColor))
		{
			if (event.getNewCurrent() == 0)
				changeGridState(baseBlock, false);
			else
				changeGridState(baseBlock, true);
		}
	}

	public void changeGridState(final Block baseBlock, final Boolean state)
	{
		final ArrayList<Block> grid = new ArrayList<Block>(maxGridSize);
		final ArrayList<Block> mechs = new ArrayList<Block>();
		final ArrayList<Block> invmechs = new ArrayList<Block>();
		grid.add(baseBlock);

		for (int i = 0; i < grid.size(); i++)
		{
			if (grid.size() > (maxGridSize))
				break;
			for (int f = 0; f < Faces.length; f++)
			{
				if (grid.size() > (maxGridSize))
					break;
				final Block b = grid.get(i).getRelative(Faces[f]);
				if ((b.getType() != Material.WOOL) || (b.getData() == pwrNodeWoolColor))
					continue;					

				if (!grid.contains(b))
					grid.add(b);
			}

		}

		for (int i = 1; i < grid.size(); i ++)
		{
			final Block b = grid.get(i);

			if (state)
			{
				if (b.getData() == unpwrdWoolColor)
					b.setData(pwrdWoolColor);
				else if (b.getData() == pwrdInvWoolColor) 
					b.setData(unpwrdInvWoolColor);
			}
			else
			{
				if (b.getData() == pwrdWoolColor) 
					b.setData(unpwrdWoolColor);
				else if (b.getData() == unpwrdInvWoolColor) 
					b.setData(pwrdInvWoolColor);
			}

			for (int f = 0; f < Faces.length; f++)
			{
				final Block mb = b.getRelative(Faces[f]);

				if (!(mechs.contains(mb)) && !(invmechs.contains(mb)))
				{
					if ((b.getData() != unpwrdInvWoolColor) && (b.getData() != pwrdInvWoolColor))
						mechs.add(mb);
					else
						invmechs.add(mb);
				}
			}
		}

		for (final Block b : mechs)
			changeMechanismState(b, state);

		for (final Block b : invmechs)
			changeMechanismState(b, !state);
	}

	public void changeMechanismState(final Block mechanism, final Boolean state)
	{
		if (mechanism.getType() == Material.LEVER)
		{
			if (state)
			{
				if (mechanism.getData() < 8)
					mechanism.setData((byte) (mechanism.getData() + 0x8));
			}
			else
			{
				if (mechanism.getData() > 7)
					mechanism.setData((byte) (mechanism.getData() - 0x8));
			}
			return;
		}

		if ((mechanism.getType() == Material.FENCE_GATE) || mechanism.getType() == Material.TRAP_DOOR)
		{
			if (state)
			{
				if (mechanism.getData() < 4)
					mechanism.setData((byte) (mechanism.getData() + 0x4));
			}
			else
			{
				if (mechanism.getData() > 3)
					mechanism.setData((byte) (mechanism.getData() - 0x4));
			}
			return;
		}

		if (mechanism.getType() == Material.GLOWSTONE)
		{
			if (!state)
				mechanism.setType(Material.GLASS);
			return;
		}
		if (mechanism.getType() == Material.GLASS)
		{			
			if (state)
				mechanism.setType(Material.GLOWSTONE);
			return;
		}	
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
