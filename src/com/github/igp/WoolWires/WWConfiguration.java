package com.github.igp.WoolWires;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class WWConfiguration {
	private final Logger log;
	private final FileConfiguration config;
	private final ArrayList<WireConfiguration> wireConfigs;
	private final byte inputColor;

	public WWConfiguration(final JavaPlugin plugin) {
		log = plugin.getLogger();
		config = plugin.getConfig();
		wireConfigs = new ArrayList<WireConfiguration>(16);

		inputColor = stringToColor(config.getString("InputColor", "BROWN"));

		// default
		wireConfigs.add(new WireConfiguration((byte) 24, config.getInt("Wires.Default.Type", 0), config.getInt("Wires.Default.MaxSize", 1024), stringsToMaterials(config.getStringList("Wires.Default.Allowed"))));

		for (final String s : config.getStringList("Wires")) {
			final byte color = stringToColor(s);
			if ((color != ((byte) 17)) && (color != inputColor)) {
				final WireConfiguration tempConfig;
				final int type = config.getInt(("Wires." + s + ".Type"), wireConfigs.get(0).getType());
				final int maxSize = config.getInt(("Wires." + s + ".MaxSize"), wireConfigs.get(0).getMaxSize());
				ArrayList<Material> allowedMaterials = stringsToMaterials(config.getStringList("Wires." + s + ".Allowed"));

				if (allowedMaterials.size() == 0)
					allowedMaterials = wireConfigs.get(0).getAllowedMaterials();

				tempConfig = new WireConfiguration(color, type, maxSize, allowedMaterials);

				wireConfigs.add(tempConfig);
			}
		}

		log.info("Configuration loaded.");
	}

	public WireConfiguration getWireConfig(final byte color) {
		for (final WireConfiguration wireConfig : wireConfigs) {
			if (wireConfig.getColor() == color)
				return wireConfig;
		}
		return wireConfigs.get(0);
	}

	public byte getInputColor() {
		return inputColor;
	}

	private final byte stringToColor(final String string) {
		if (string.equalsIgnoreCase("WHITE"))
			return DyeColor.WHITE.getData();

		if (string.equalsIgnoreCase("ORANGE"))
			return DyeColor.ORANGE.getData();

		if (string.equalsIgnoreCase("MAGENTA"))
			return DyeColor.MAGENTA.getData();

		if (string.equalsIgnoreCase("LIGHT_BLUE"))
			return DyeColor.LIGHT_BLUE.getData();

		if (string.equalsIgnoreCase("YELLOW"))
			return DyeColor.YELLOW.getData();

		if (string.equalsIgnoreCase("LIME"))
			return DyeColor.LIME.getData();

		if (string.equalsIgnoreCase("PINK"))
			return DyeColor.PINK.getData();

		if (string.equalsIgnoreCase("GRAY"))
			return DyeColor.GRAY.getData();

		if (string.equalsIgnoreCase("GREY"))
			return DyeColor.GRAY.getData();

		if (string.equalsIgnoreCase("LIGHT_GRAY"))
			return DyeColor.SILVER.getData();

		if (string.equalsIgnoreCase("LIGHT_GREY"))
			return DyeColor.SILVER.getData();

		if (string.equalsIgnoreCase("SILVER"))
			return DyeColor.SILVER.getData();

		if (string.equalsIgnoreCase("CYAN"))
			return DyeColor.CYAN.getData();

		if (string.equalsIgnoreCase("PURPLE"))
			return DyeColor.PURPLE.getData();

		if (string.equalsIgnoreCase("BLUE"))
			return DyeColor.BLUE.getData();

		if (string.equalsIgnoreCase("BROWN"))
			return DyeColor.BROWN.getData();

		if (string.equalsIgnoreCase("GREEN"))
			return DyeColor.GREEN.getData();

		if (string.equalsIgnoreCase("RED"))
			return DyeColor.RED.getData();

		if (string.equalsIgnoreCase("BLACK"))
			return DyeColor.BLACK.getData();

		return ((byte) 24);
	}

	private final ArrayList<Material> stringsToMaterials(final List<String> strings) {
		final ArrayList<Material> materials = new ArrayList<Material>();

		if (strings.isEmpty()) {
			materials.add(Material.LEVER);
			materials.add(Material.FENCE_GATE);
			materials.add(Material.TRAP_DOOR);
			materials.add(Material.NOTE_BLOCK);
			materials.add(Material.DISPENSER);
			materials.add(Material.GLASS);
			materials.add(Material.GLOWSTONE);

			return materials;
		}

		for (final String s : strings) {
			if (s.equalsIgnoreCase("ALL")) {
				if (!materials.contains(Material.LEVER))
					materials.add(Material.LEVER);
				if (!materials.contains(Material.FENCE_GATE))
					materials.add(Material.FENCE_GATE);
				if (!materials.contains(Material.TRAP_DOOR))
					materials.add(Material.TRAP_DOOR);
				if (!materials.contains(Material.NOTE_BLOCK))
					materials.add(Material.NOTE_BLOCK);
				if (!materials.contains(Material.DISPENSER))
					materials.add(Material.DISPENSER);
				if (!materials.contains(Material.GLASS))
					materials.add(Material.GLASS);
				if (!materials.contains(Material.GLOWSTONE))
					materials.add(Material.GLOWSTONE);
				break;
			}

			if ((s.equalsIgnoreCase("LEVER")) && (!materials.contains(Material.LEVER))) {
				materials.add(Material.LEVER);
				continue;
			}

			if ((s.equalsIgnoreCase("FENCE_GATE")) && (!materials.contains(Material.FENCE_GATE))) {
				materials.add(Material.FENCE_GATE);
				continue;
			}

			if ((s.equalsIgnoreCase("TRAP_DOOR")) && (!materials.contains(Material.TRAP_DOOR))) {
				materials.add(Material.TRAP_DOOR);
				continue;
			}

			if ((s.equalsIgnoreCase("NOTE_BLOCK")) && (!materials.contains(Material.NOTE_BLOCK))) {
				materials.add(Material.NOTE_BLOCK);
				continue;
			}

			if ((s.equalsIgnoreCase("DISPENSER")) && (!materials.contains(Material.DISPENSER))) {
				materials.add(Material.DISPENSER);
				continue;
			}

			if ((s.equalsIgnoreCase("GLASS")) && (!materials.contains(Material.GLASS))) {
				materials.add(Material.GLASS);
				if (!materials.contains(Material.GLOWSTONE))
					materials.add(Material.GLOWSTONE);
				continue;
			}

			if ((s.equalsIgnoreCase("GLOWSTONE")) && (!materials.contains(Material.GLOWSTONE))) {
				materials.add(Material.GLOWSTONE);
				if (!materials.contains(Material.GLASS))
					materials.add(Material.GLASS);
				continue;
			}
		}

		return materials;
	}

	public class WireConfiguration {
		private final byte color;
		private final int type;
		private final int maxSize;
		private final ArrayList<Material> allowedMaterials;

		WireConfiguration(final byte color, final int type, final int maxSize, final ArrayList<Material> allowedMaterials) {
			this.color = color;
			this.type = type;
			this.maxSize = maxSize;
			this.allowedMaterials = allowedMaterials;
		}

		public final byte getColor() {
			return color;
		}

		public final byte getInputColor() {
			return inputColor;
		}

		public final ArrayList<Material> getAllowedMaterials() {
			return allowedMaterials;
		}

		public final int getType() {
			return type;
		}

		public final int getMaxSize() {
			return maxSize;
		}
	}
}
