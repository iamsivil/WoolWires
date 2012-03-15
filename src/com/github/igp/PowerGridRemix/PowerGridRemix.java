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
	int maxMechSize;

	@Override
	public void onEnable()
	{
		log = this.getLogger();
		getServer().getPluginManager().registerEvents(this, this);

		unpwrdWoolColor = DyeColor.WHITE.getData();
		pwrdWoolColor = DyeColor.YELLOW.getData();
		unpwrdInvWoolColor = DyeColor.PURPLE.getData();
		pwrdInvWoolColor = DyeColor.PINK.getData();
		pwrNodeWoolColor = DyeColor.BROWN.getData();
		maxGridSize = 1000;
		maxMechSize = 5;

		log.info("Your plugin has been enabled!");
	}

	@Override
	public void onDisable()
	{
		log.info("Your plugin has been disabled.");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockRedstoneChanged(BlockRedstoneEvent event)
	{
		Block sourceBlock = event.getBlock();
		Block baseBlock = sourceBlock.getRelative(BlockFace.DOWN);

		if ((baseBlock.getType() == Material.WOOL) && (baseBlock.getData() == pwrNodeWoolColor) && (sourceBlock.getType() == Material.REDSTONE_WIRE))
		{
			if (event.getNewCurrent() == 0)
				changeGridState(baseBlock, false);
			else
				changeGridState(baseBlock, true);
		}

	}

	public void changeGridState(Block baseBlock, Boolean state)
	{
		ArrayList<Block> grid = new ArrayList<Block>(1000);
		ArrayList<Block> mechs = new ArrayList<Block>(1000);
		ArrayList<Block> invmechs = new ArrayList<Block>(1000);
		grid.add(baseBlock);		

		for (int i = 0; i < grid.size(); i++)
		{
			if (grid.size() > (maxGridSize))
				break;
			for (int f = 0; f < Faces.length; f++)
			{
				if (grid.size() > (maxGridSize))
					break;
				Block b = grid.get(i).getRelative(Faces[f]);
				if ((b.getType() != Material.WOOL) || (b.getData() == pwrNodeWoolColor))
					continue;
				if (!grid.contains(b))
					grid.add(b);
			}

		}

		for (int i = 1; i < grid.size(); i ++)
		{
			Block b = grid.get(i);

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
				Block mb = b.getRelative(Faces[f]);

				if (!(mechs.contains(mb)) && !(invmechs.contains(mb)))
				{
					if ((b.getData() != unpwrdInvWoolColor) && (b.getData() != pwrdInvWoolColor))
						mechs.add(mb);
					else
						invmechs.add(mb);
				}
			}
		}

		for (Block b : mechs)
			changeMechanismState(b, state);

		for (Block b : invmechs)
			changeMechanismState(b, !state);
	}

	public void changeMechanismState(Block mechanism, Boolean state)
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
