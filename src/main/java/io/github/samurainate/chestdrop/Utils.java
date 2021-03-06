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
		if (config.isWorldBorderEnabled() && config.getWb().worldHasBorder(worldname)) {
			if (config.isTownyEnabled()) {
				/*
				 * Try to get a wild spawn location but take whatever after 100
				 * iterations
				 */
				for (int i = 0; i < 100; i++) {
					drop = config.getWb().randomCoordWithinBordersOf(worldname, maxRange);
					if (config.getTowny()
							.isWild(new Location(config.getServer().getWorld(worldname), drop[0], 0, drop[1])))
						break;
				}
			} else {
				drop = config.getWb().randomCoordWithinBordersOf(worldname, maxRange);
			}
			return drop;
		}

		// either no world border integration or no border defined
		/*
		 * Try to get a wild spawn location but take whatever after 100
		 * iterations
		 */
		if (config.isTownyEnabled()) {
			for (int i = 0; i < 100; i++) {
				drop = randomXZ(config, worldname, maxRange);

				if (config.getTowny().isWild(new Location(config.getServer().getWorld(worldname), drop[0], 0, drop[1])))
					break;
			}
		} else {

			drop = randomXZ(config, worldname, maxRange);
		}
		return drop;
	}

	private static double[] randomXZ(PluginConfig config, String worldname, int maxRange) {
		double[] drop;
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
		if (world == null) {
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
			/* fall through air, water, leaves, other soft things */
			case AIR:
			case WATER:
			case STATIONARY_WATER:
			case ICE:
			case SNOW:
			case LEAVES:
			case YELLOW_FLOWER:
			case RED_ROSE:
			case DEAD_BUSH:
			case LONG_GRASS:
			case DOUBLE_PLANT:
			case SUGAR_CANE_BLOCK:
			case MELON:
			case PUMPKIN:
			case WATER_LILY:
			case JACK_O_LANTERN:
			case LEAVES_2:
			case SAPLING:
			case VINE:
			case WEB:
			case BROWN_MUSHROOM:
			case RED_MUSHROOM:
			case CACTUS:
			case COCOA:
				continue;
			/* shift so we don't land on or in trees (and other things) */
			case LOG:
			case LOG_2:
			case CROPS:
			case MELON_STEM:
			case PUMPKIN_STEM:
			/* Other denizens of Roofed Forest */
			case HUGE_MUSHROOM_1:
			case HUGE_MUSHROOM_2:
			/* Village Roof */
			case WOOD:
			case WOOD_STAIRS:
			/* Desert Temple */
			case SANDSTONE:
			case SANDSTONE_STAIRS:
			/* Hot Stuff */
			case FIRE:
			case LAVA:
			case STATIONARY_LAVA:
			/* Stuff I don't want to see a chest on top of */
			case CARPET:
			case SIGN:
			case SIGN_POST:
			case ARMOR_STAND:
			case RAILS:
			case ACTIVATOR_RAIL:
			case DETECTOR_RAIL:
			case POWERED_RAIL:
			case REDSTONE_WIRE:
			case DRAGON_EGG:
			/* could be more here but I'll wait for complaints */
				/* recurse */
				return dropChest(config, worldname);
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
		block = world.getBlockAt((int) coords[0], y + 3, (int) coords[1]);
		block.setType(Material.GLOWSTONE);
		block = world.getBlockAt((int) coords[0], y + 2, (int) coords[1]);
		block.setType(Material.FENCE);
		block = world.getBlockAt((int) coords[0], y + 1, (int) coords[1]);
		block.setType(Material.FENCE);
		block = world.getBlockAt((int) coords[0], y, (int) coords[1]);
		block.setType(Material.CHEST);

		/* Open Chest */
		Chest chest = (Chest) block.getState();
		Inventory in = chest.getBlockInventory();

		/* Put Treasure in Chest */
		in.setItem(in.firstEmpty(), config.gemModel().hiddenGem(1 + config.getRandom().nextInt(5)));

		/* Notify players */
		Utils.worldBroadcast(config.getServer(), worldname,
				String.format("Chest dropped at %1.0fX, %1.0fZ", coords[0], coords[1]));
		return true;
	}

	public static Chunk loadChunk(World world, double[] coords) {
		Chunk chunk = world.getChunkAt((int) coords[0], (int) coords[1]);
		if (!chunk.isLoaded()) {
			chunk.load(true);
		}
		return chunk;
	}

	public static void displayTrades(PluginConfig pluginConfig, Player player) {
		Inventory inv = pluginConfig.getServer().createInventory(null, 27, "Trade "+pluginConfig.gemModel().getName());
		for (Trade trade : pluginConfig.getTrades()) {
			ItemStack item = trade.getItems().clone();
			ItemMeta meta = item.getItemMeta();
			meta.setLore(Arrays.asList("Trade for " + trade.getCost() + " " + pluginConfig.gemModel().getName(), trade.getName()));
			item.setItemMeta(meta);
			inv.setItem(inv.firstEmpty(), item);
		}
		player.openInventory(inv);
	}

	public static void manageTrades(PluginConfig pluginConfig, Player player) {
		Inventory inv = pluginConfig.getServer().createInventory(null, 27, "Delete "+pluginConfig.gemModel().getName()+" Trades");
		for (Trade trade : pluginConfig.getTrades()) {
			ItemStack item = trade.getItems().clone();
			ItemMeta meta = item.getItemMeta();
			meta.setLore(Arrays.asList("Click to delete", trade.getName()));
			item.setItemMeta(meta);
			inv.setItem(inv.firstEmpty(), item);
		}
		player.openInventory(inv);
	}

	public static int gemCount(Player p, GemModel gemModel) {
		int gems = 0;
		Inventory inv = p.getInventory();
		HashMap<Integer, ? extends ItemStack> emeralds = inv.all(Material.EMERALD);
		for (Integer key : emeralds.keySet()) {
			/* check balance */
			ItemStack emerald = emeralds.get(key);
			if (gemModel.isHiddenGem(emerald))
				gems += emerald.getAmount();
		}
		return gems;
	}

	public static boolean executeTrade(Player p, Trade trade, GemModel gemModel) {
		int costToGo = trade.getCost();
		Inventory inv = p.getInventory();
		HashMap<Integer, ? extends ItemStack> emeralds = inv.all(Material.EMERALD);
		for (Integer key : emeralds.keySet()) {
			/* check balance */
			ItemStack emerald = emeralds.get(key);
			if (gemModel.isHiddenGem(emerald)) {
				if (emerald.getAmount() > costToGo) {
					emerald.setAmount(emerald.getAmount() - costToGo);
					costToGo = 0;
				} else if (emerald.getAmount() == costToGo) {
					inv.setItem(key, null);
					costToGo = 0;
				} else {
					costToGo -= emerald.getAmount();
					inv.setItem(key, null);
				}
			}
			if (costToGo == 0) {
				ItemStack item = trade.getItems().clone();
				giveItem(p, item);
				return true;
			}
		}
		return false;
	}

	public static void giveItem(Player p, ItemStack item) {
		HashMap<Integer, ItemStack> drops = p.getInventory().addItem(item);
		for (ItemStack stack : drops.values()) {
			p.getWorld().dropItem(p.getLocation(), stack);
		}
		p.updateInventory();
	}

	public static void displayConfirmDelete(PluginConfig pluginConfig, Player p, ItemStack item) {
		Inventory inv = pluginConfig.getServer().createInventory(null, 27, "Confirm Delete Trade?");
		ItemStack ok = null;
		ItemStack cancel = null;
		// TODO: finish here
		inv.setItem(2 * 9 + 2, ok);
		inv.setItem(2 * 9 + 7, cancel);
		p.openInventory(inv);
	}

}
