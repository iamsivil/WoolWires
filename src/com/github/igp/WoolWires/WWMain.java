package com.github.igp.WoolWires;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class WWMain extends JavaPlugin {
	private Logger log;
	private WWBlockListener blockListener;

	byte inputColor;

	int maxGridSize;

	@Override
	public void onEnable() {
		log = this.getLogger();

		blockListener = new WWBlockListener(this);

		getServer().getPluginManager().registerEvents(blockListener, this);

		log.info("Enabled.");
	}

	@Override
	public void onDisable() {
		log.info("Disabled.");
	}
}
