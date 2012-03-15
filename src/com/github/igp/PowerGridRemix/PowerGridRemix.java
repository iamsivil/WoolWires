package com.github.igp.PowerGridRemix;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PowerGridRemix extends JavaPlugin implements Listener 
{	
	Logger log;
	
	byte unpoweredWoolColor;
	byte poweredWoolColor;
	byte invertingWoolColor;
	byte powerNodeWoolColor;
	

	@Override
	public void onEnable()
	{
		log = this.getLogger();
		getServer().getPluginManager().registerEvents(this, this);
		
		unpoweredWoolColor = DyeColor.WHITE.getData();
		poweredWoolColor = DyeColor.YELLOW.getData();
		invertingWoolColor = DyeColor.PURPLE.getData();
		powerNodeWoolColor = DyeColor.BROWN.getData();
		
		log.info("Your plugin has been enabled!");
	}

	@Override
	public void onDisable()
	{
		log.info("Your plugin has been disabled.");
	}

	@EventHandler
	public void onBlockRedstoneChanged(BlockRedstoneEvent event)
	{
		Block sourceBlock = event.getBlock();
		Block baseBlock = sourceBlock.getRelative(BlockFace.DOWN);

		
		if ((baseBlock.getType() == Material.WOOL) && (baseBlock.getData() == powerNodeWoolColor) && (sourceBlock.getType() == Material.REDSTONE_WIRE))
		{
			long start = System.currentTimeMillis();
			if (event.getNewCurrent() == 0)
			{
				log.info("State: "+ String.valueOf(false));
				changeGridState(baseBlock, false);
			}
			else
			{
				changeGridState(baseBlock, true);
				log.info("State: "+ String.valueOf(true));
			}
			long end = System.currentTimeMillis();
			
			
			log.info("Execution time was "+(end-start)+" ms.");
		}
		
	}

	public void changeGridState(Block baseBlock, Boolean state)
	{
		ArrayList<Block> grid = new ArrayList<Block>();
		ArrayList<Block> mechs = new ArrayList<Block>();
		ArrayList<Block> invmechs = new ArrayList<Block>();
		grid.add(baseBlock);

		for (int i = 0; i < grid.size(); i++)
		{
			for (int f = 0; f < Faces.length; f++)
			{
				Block b = grid.get(i).getRelative(Faces[f]);
				if (b.getType() != Material.WOOL)
					continue;
				if (!grid.contains(b))
					grid.add(b);
			}
		}

		for (int i = 1; i < grid.size(); i ++)
		{
			Block b = grid.get(i);
			if (state && (b.getData() == unpoweredWoolColor)) b.setData(poweredWoolColor);
			else if (!state && (b.getData() == poweredWoolColor)) b.setData(unpoweredWoolColor);
			for (int f = 0; f < Faces.length; f++)
			{
				Block mb = b.getRelative(Faces[f]);
				//if ((isValidMechanism(mb)) && !(mechs.contains(mb)) && !(invmechs.contains(mb)))
				if (!(mechs.contains(mb)) && !(invmechs.contains(mb)))	
					if (b.getData() != invertingWoolColor)
						mechs.add(mb);
					else
						invmechs.add(mb);
			}
		}

		for (Block b : mechs)
			changeMechanismState(b, state);
		
		for (Block b : invmechs)
			changeMechanismState(b, !state);
	}

	public boolean isValidMechanism(Block mechanism)
	{
		if ((mechanism.getType() == Material.LEVER) ||
				(mechanism.getType() == Material.GLOWSTONE) ||
				(mechanism.getType() == Material.GLASS) ||
				(mechanism.getType() == Material.FENCE_GATE) ||
				(mechanism.getType() == Material.TRAP_DOOR))
			return true;
		return false;
	}

	public void changeMechanismState(Block mechanism, Boolean state)
	{
		//log.info(String.valueOf(mechanism.getData()));

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
		}
		else if (mechanism.getType() == Material.GLOWSTONE)
		{
			if (!state)
				mechanism.setType(Material.GLASS);
		}
		else if (mechanism.getType() == Material.GLASS)
		{			
			if (state)
				mechanism.setType(Material.GLOWSTONE);
		}
		else if ((mechanism.getType() == Material.FENCE_GATE) || mechanism.getType() == Material.TRAP_DOOR)
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
