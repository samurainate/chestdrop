package io.github.samurainate.chestdrop;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {

	public static void worldBroadcast(Server server, String worldname, String format) {
		World world = server.getWorld(worldname);
		for (Player player : server.getOnlinePlayers()) {
			if (player.getWorld().equals(world))
				player.sendMessage(format);
		}

	}

	public static int getPlayerCount(Server server, String worldname) {
		int count = 0;
		World world = server.getWorld(worldname);
		for (Player player : server.getOnlinePlayers()) {
			if (player.getWorld().equals(world))
				count++;
		}
		return count;
	}

	/**
	 * @param config
	 *            the PluginConfig
	 * @param worldname
	 *            the world to get drop location in
	 * @return a random drop location NOTE: If WorldBorder integration is
	 *         enabled AND THERE IS A BORDER DEFINED, then drops are centered on
	 *         the center of the world's border. Otherwise, the drops are
	 *         centered on the world spawn location.
	 */
	public static double[] getDrop(PluginConfig config, String worldname) {
		double[] drop = null;
		WorldConfig worldConfig = config.getWorldConfig(worldname);
		int maxRange = worldConfig.getMaxRangeForDrops();
		// try world border method first
		if (config.isWorldBorderEnabled()) {
			// this returns null if no border enabled
			drop = config.getWb().randomCoordWithinBordersOf(worldname, maxRange);
			if (drop != null) return drop;
		}

		// either no world border integration or no border defined
		drop = new double[2];
		Location l = config.getServer().getWorld(worldname).getSpawnLocation();
		int minx = (int) (l.getX() - maxRange);
		int minz = (int) (l.getZ() - maxRange);
		drop[0] = config.getRandom().nextInt(maxRange) + minx;
		drop[1] = config.getRandom().nextInt(maxRange) + minz;

		return drop;
	}

	public static void scheduleTasks(PluginConfig config) {
		for (String worldname : config.getConfiguredWorlds()) {
			WorldConfig worldConfig = config.getWorldConfig(worldname);
			if (worldConfig.isEnabled()) {
			}
			config.getServer().getScheduler().scheduleSyncRepeatingTask(config.getPlugin(),
					new ChestDropTask(config, worldname), worldConfig.getDropInterval(), worldConfig.getDropInterval());
		}
	}
	
	public static boolean dropChest(PluginConfig config, String worldname) {
		/* Get random drop coords */
		double[] coords = Utils.getDrop(config, worldname);
		
		World world = config.getServer().getWorld(worldname);
		if(world==null) {
			return false;
		}

		/* Load drop chunk */
		loadChunk(world, coords);

		/* Drop to ground loop */
		Block block = null;
		int y;
		for (y = world.getMaxHeight(); y > 0; y--) {
			block = world.getBlockAt((int) coords[0], y - 1, (int) coords[1]);
			switch (block.getType()) {
			/* fall through air, water, leaves */
			case AIR:
			case WATER:
			case STATIONARY_WATER:
			case LEAVES:
				continue;
			/* shift so we don't land on or in trees */
			case LOG:
			case LOG_2:
				boolean axis = config.getRandom().nextBoolean();
				boolean direction = config.getRandom().nextBoolean();
				int a=axis?0:1;
				int d=direction?1:-1;
				/* move one */
				coords[a]+=d;
				/* start over */
				loadChunk(world,coords);
				y=world.getMaxHeight()+1;
				continue;
			case LAVA:
			case STATIONARY_LAVA:
				/* better luck next time */
				return false;
			default:
				break;
			}
			break;
		}
		if (y == 0) {
			config.getServer().getLogger().info("[ChestDrop] chest fell into the void...");
			return false; // Abort if we make it to the void
		}

		/* Place Chest with Marker */
		block = world.getBlockAt((int) coords[0], y+3, (int) coords[1]);
		block.setType(Material.GLOWSTONE);
		block = world.getBlockAt((int) coords[0], y+2, (int) coords[1]);
		block.setType(Material.FENCE);
		block = world.getBlockAt((int) coords[0], y+1, (int) coords[1]);
		block.setType(Material.FENCE);
		block = world.getBlockAt((int) coords[0], y, (int) coords[1]);
		block.setType(Material.CHEST);

		/* Open Chest */
		Chest chest = (Chest) block.getState();
		Inventory in = chest.getBlockInventory();

		/* Generate Treasure */
		ItemStack gem = new ItemStack(Material.EMERALD, 1 + config.getRandom().nextInt(5));
		ItemMeta meta = gem.getItemMeta();
		meta.setDisplayName("Hidden Gem");
		gem.setItemMeta(meta);

		/* Put Treasure in Chest */
		in.setItem(in.firstEmpty(), gem);

		/* Notify players */
		Utils.worldBroadcast(config.getServer(), worldname,
				String.format("Chest dropped at %1.0fX, %1.0fZ", coords[0], coords[1]));
		return true;
	}
	
	public static void loadChunk(World world, double[] coords) {
		Chunk chunk = world.getChunkAt((int) coords[0], (int) coords[1]);
		if (!chunk.isLoaded()) {
			chunk.load(true);
		}
	}

	public static void displayTrades(PluginConfig pluginConfig, Player player) {
		Inventory inv = pluginConfig.getServer().createInventory(null,27,"Trade Hidden Gems");
		for(Trade trade : pluginConfig.getTrades()) {
			ItemStack item = trade.getItems().clone();
			ItemMeta meta = item.getItemMeta();
			meta.setLore(Arrays.asList("Trade for "+trade.getCost()+" Hidden Gems",trade.getName()));
			item.setItemMeta(meta);
			inv.setItem(inv.firstEmpty(), item);
		}
		player.openInventory(inv);
	}

	public static int gemCount(Player p) {
    	int gems = 0;
    	Inventory inv = p.getInventory();
    	HashMap<Integer, ? extends ItemStack> emeralds = inv.all(Material.EMERALD);
    	for (Integer key:emeralds.keySet()) {
    		/* check balance */
    		ItemStack emerald = emeralds.get(key);
    		if (emerald.getItemMeta().getDisplayName().equals("Hidden Gem"));
    		gems+=emerald.getAmount();
    	}
    	return gems;
	}

	public static boolean executeTrade(Player p, Trade trade) {
		int costToGo = trade.getCost();
    	Inventory inv = p.getInventory();
		HashMap<Integer, ? extends ItemStack> emeralds = inv.all(Material.EMERALD);
    	for (Integer key:emeralds.keySet()) {
    		/* check balance */
    		ItemStack emerald = emeralds.get(key);
    		if (emerald.getItemMeta().getDisplayName().equals("Hidden Gem")) {
    			if (emerald.getAmount()>costToGo) {
    				emerald.setAmount(emerald.getAmount()-costToGo);
    				costToGo=0;
    			} else if (emerald.getAmount()==costToGo) {
    				inv.setItem(key, null);
    				costToGo=0;
    			} else {
    				costToGo-=emerald.getAmount();
    				inv.setItem(key, null);
    			}
    		}
    		if (costToGo==0) {
    			HashMap<Integer, ItemStack> drops = inv.addItem(trade.getItems().clone());
    			for (ItemStack stack: drops.values()) {
    				p.getWorld().dropItem(p.getLocation(), stack);
    			}
    			p.updateInventory();
    			return true;
    		}
    	}
    	return false;
	}


}
