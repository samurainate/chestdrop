package io.github.samurainate.chestdrop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ChestDropConfig {

	private static final long DEFAULT_INTERVAL = 1000L;
	private static final long DEFAULT_DELAY = 100L;
	private static final double DEFAULT_XRNG = 1000;
	private static final double DEFAULT_ZRNG = 1000;

	private List<World> worlds;
	private WorldBorderIntegration wb;
	private boolean isWorldBorderEnabled = false;
	private Random rand;

	private Server server;

	private Logger logger;

	private HashMap<String, double[]> bounds;

	public ChestDropConfig(Main main) {
		this.server = main.getServer();
		this.logger = server.getLogger();
		this.rand = new Random();
		this.bounds = new HashMap<String, double[]>();
		FileConfiguration configFile = main.getConfig();

		/* initialize config */
		for (World world : main.getServer().getWorlds()) {
			configFile.addDefault("worlds." + world.getName(), false);
		}
		configFile.options().copyDefaults(true);
		main.saveConfig();

		/* load worlds enabled by name */
		worlds = new ArrayList<World>();
		for (String key : configFile.getKeys(true)) {
			if (key.startsWith("worlds.")) {
				String world = key.split("\\.", 2)[1];
				if (configFile.getBoolean(key)) {
					World enabled = server.getWorld(world);
					if (enabled == null) {
						logger.warning(String.format("[ChestDrop] World not found: '%s'", world));
					} else {
						worlds.add(enabled);
						Location spawn = enabled.getSpawnLocation();
						double xrng = DEFAULT_XRNG;
						double zrng = DEFAULT_ZRNG;
						bounds.put(world, new double[] { spawn.getBlockX() - xrng / 2., xrng,
								spawn.getBlockZ() - zrng / 2., zrng });
						logger.info(String.format("[ChestDrop] Drops on for world: '%s'", world));
					}
				}
			}
		}

		/* get world border, if any */
		// TODO: integrate WorldBorder
		Plugin test = server.getPluginManager().getPlugin("WorldBorder");
		if (test == null || !test.isEnabled()) {
			// no world border
		} else {
			try {
				this.wb = new WorldBorderIntegration(this,test);
				this.isWorldBorderEnabled = true;
				logger.info("[ChestShop] WorldBorder integration enabled");
			} finally {
			}
		}

	}

	public double[] getDrop(String worldname) {
		double[] drop = null;
		if (isWorldBorderEnabled) {
			drop = wb.randomCoordWithinBordersOf(worldname);
		}
		return drop == null ? randomCoord(worldname) : drop;
	}

	private double[] randomCoord(String worldname) {
		double[] bound = bounds.get(worldname);
		double[] coord = new double[] { rand.nextDouble() * bound[1] + bound[0],
				rand.nextDouble() * bound[3] + bound[2] };
		return coord;
	}

	public Server getServer() {
		return server;
	}

	public boolean isWorldBorderEnabled() {
		return isWorldBorderEnabled;
	}

	public WorldBorderIntegration getWb() {
		return wb;
	}

	public List<World> getWorlds() {
		return worlds;
	}

	public long getInterval(String name) {
		return DEFAULT_INTERVAL;
	}

	public long getDelay(String name) {
		return DEFAULT_DELAY;
	}

	public Random getRandom() {
		return rand;
	}

	public void worldBroadcast(World world, String format) {
		for (Player player : server.getOnlinePlayers()) {
			if (player.getWorld().equals(world)) player.sendMessage(format);
		}
		
	}

	public int getPlayerCount(World world) {
		int count=0;
		for (Player player : server.getOnlinePlayers()) {
			if (player.getWorld().equals(world)) count++;
		}
		return count;
	}
}
