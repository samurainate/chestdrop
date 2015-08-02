package io.github.samurainate.chestdrop;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class PluginConfig {

	private HashMap<String, WorldConfig> configuredWorlds;
	private WorldBorderIntegration wb;
	private boolean isWorldBorderEnabled = false;
	private Random rand;

	private Server server;
	private Plugin plugin;

	public PluginConfig(ChestDropPlugin plugin) {
		this.plugin = plugin;
		this.server = plugin.getServer();
		this.rand = new Random();
		this.configuredWorlds = new HashMap<String, WorldConfig>();
		FileConfiguration configFile = plugin.getConfig();

		/* initialize config with defaults for each world */
		for (World world : plugin.getServer().getWorlds()) {
			WorldConfig worldConfig = new WorldConfig(world.getName());
			configuredWorlds.put(world.getName(), worldConfig);
			configFile.addDefault("worlds." + world.getName()+".enabled", worldConfig.isEnabled());
			configFile.addDefault("worlds." + world.getName()+".dropWhenEmpty", worldConfig.isDropWhenEmpty());
			configFile.addDefault("worlds." + world.getName()+".maxRangeForDrops", worldConfig.getMaxRangeForDrops());
			configFile.addDefault("worlds."+world.getName()+".dropInterval", worldConfig.getDropInterval());
			configFile.addDefault("worlds."+world.getName()+".dropChance", worldConfig.getDropChance());
		}
		configFile.options().copyDefaults(true);
		plugin.saveConfig();

		/* load worlds configuration from settings (or defaults set above) */
		for (String worldname : configuredWorlds.keySet()) {
			WorldConfig worldConfig = configuredWorlds.get(worldname);
			worldConfig.setEnabled(configFile.getBoolean("worlds."+worldname+".enabled"));
			worldConfig.setDropWhenEmpty(configFile.getBoolean("worlds."+worldname+".dropWhenEmpty"));
			worldConfig.setMaxRangeForDrops(configFile.getInt("worlds."+worldname+".maxRangeForDrops"));
			worldConfig.setDropInterval(configFile.getInt("worlds."+worldname+".dropInterval"));
			worldConfig.setDropChance(configFile.getDouble("worlds."+worldname+".dropChance"));
			worldConfig.rationalize();
		}

		/* Integrate with WorldBorder when available */
		Plugin test = server.getPluginManager().getPlugin("WorldBorder");
		if (test == null || !test.isEnabled()) {
			// no world border
		} else {
			// load world border integration
			try {
				this.wb = new WorldBorderIntegration(this,test);
				this.isWorldBorderEnabled = true;
			} finally {
			}
		}

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

	public Random getRandom() {
		return rand;
	}

	public Set<String> getConfiguredWorlds() {
		return configuredWorlds.keySet();
	}

	public WorldConfig getWorldConfig(String worldname) {
		return configuredWorlds.get(worldname);
	}

	public Plugin getPlugin() {
		return plugin;
	}

}
