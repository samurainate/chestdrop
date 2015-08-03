package io.github.samurainate.chestdrop;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PluginConfig {

	private HashMap<String, WorldConfig> configuredWorlds;
	private HashMap<String, Trade> trades;
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
			configFile.addDefault("worlds." + world.getName() + ".enabled", worldConfig.isEnabled());
			configFile.addDefault("worlds." + world.getName() + ".dropWhenEmpty", worldConfig.isDropWhenEmpty());
			configFile.addDefault("worlds." + world.getName() + ".maxRangeForDrops", worldConfig.getMaxRangeForDrops());
			configFile.addDefault("worlds." + world.getName() + ".dropInterval", worldConfig.getDropInterval());
			configFile.addDefault("worlds." + world.getName() + ".dropChance", worldConfig.getDropChance());
		}
		configFile.options().copyDefaults(true);

		/* load trades */
		trades = new HashMap<String, Trade>();
		ConfigurationSection tradesConfig = configFile.getConfigurationSection("trades");
		for (String key : tradesConfig.getKeys(false)) {
			server.getLogger().info("[ChestDrop] Load trade '" + key + "'");
			ConfigurationSection tradeConfig = tradesConfig.getConfigurationSection(key);
			ItemStack item = tradeConfig.getItemStack("item");
			int cost = tradeConfig.getInt("cost");
			trades.put(key, new Trade(key, item, cost));
		}

		/* if no trades set up, add example trade */
		if (!configFile.contains("trades")) {
			ItemStack xpBottles = new ItemStack(Material.EXP_BOTTLE, 10);
			configFile.set("trades.example.item", xpBottles);
			configFile.set("trades.example.cost", 1);
		}

		plugin.saveConfig();

		/* load worlds configuration from settings (or defaults set above) */
		for (String worldname : configuredWorlds.keySet()) {
			WorldConfig worldConfig = configuredWorlds.get(worldname);
			worldConfig.setEnabled(configFile.getBoolean("worlds." + worldname + ".enabled"));
			worldConfig.setDropWhenEmpty(configFile.getBoolean("worlds." + worldname + ".dropWhenEmpty"));
			worldConfig.setMaxRangeForDrops(configFile.getInt("worlds." + worldname + ".maxRangeForDrops"));
			worldConfig.setDropInterval(configFile.getInt("worlds." + worldname + ".dropInterval"));
			worldConfig.setDropChance(configFile.getDouble("worlds." + worldname + ".dropChance"));
			worldConfig.rationalize();
		}

		/* Integrate with WorldBorder when available */
		Plugin test = server.getPluginManager().getPlugin("WorldBorder");
		if (test == null || !test.isEnabled()) {
			// no world border
		} else {
			// load world border integration
			try {
				this.wb = new WorldBorderIntegration(this, test);
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

	public void registerTrade(Trade trade) {
		if (trade == null || trade.getItems() == null || trade.getCost() <= 0)
			return;
		FileConfiguration configFile = plugin.getConfig();
		if (trades.containsValue(trade)) {
			trades.remove(trade.getName());
			configFile.set("trades." + trade.getName(), null);
		}
		configFile.set("trades." + trade.getName() + ".item", trade.getItems());
		configFile.set("trades." + trade.getName() + ".cost", trade.getCost());
		trades.put(trade.getName(), trade);
		plugin.saveConfig();
	}

	public Collection<Trade> getTrades() {
		return trades.values();
	}

	public Trade getTrade(String tradeName) {
		return trades.get(tradeName);
	}

}
