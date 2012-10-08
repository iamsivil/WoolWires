package com.github.igp.WoolWires;

import org.bukkit.plugin.java.JavaPlugin;

public class WWMain extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		WWBlockListener blockListener = new WWBlockListener(new WWConfiguration(this));

		getServer().getPluginManager().registerEvents(blockListener, this);

		getLogger().info("Enabled.");
	}

	@Override
	public void onDisable()
	{
		getLogger().info("Disabled.");
	}
}
