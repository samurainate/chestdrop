package io.github.samurainate.chestdrop;

import java.util.logging.Logger;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChestDropTask implements Runnable {

	private PluginConfig config;
	private String worldname;
	private World world;
	private WorldConfig worldConfig;
	private Logger logger;

	/**
	 * @param config
	 *            The plugin config
	 * @param worldname
	 *            World to drop chests on
	 */
	public ChestDropTask(PluginConfig config, String worldname) {
		this.config = config;
		this.logger = config.getServer().getLogger();
		this.worldname = worldname;
		this.worldConfig = config.getWorldConfig(worldname);
		this.world = config.getServer().getWorld(worldname);
	}

	public void run() {

		//logger.info("[ChestDrop]["+worldname+"]: Drop opportunity starting");
		
		/* Only if enabled */
		if (!worldConfig.isEnabled())
			return;
		//logger.info("[ChestDrop]["+worldname+"]: Drops enabled");

		/* Drop only if players in world unless configured otherwise */
		if (!(worldConfig.isDropWhenEmpty() || Utils.getPlayerCount(config.getServer(), worldname) > 0))
			return;
		//logger.info("[ChestDrop]["+worldname+"]: Passed player count");

		/* Apply random drop chance */
		if (config.getRandom().nextDouble() > worldConfig.getDropChance()){
			//logger.info(String.format("[ChestDrop]["+worldname+"]: Failed random chance %1.2f",worldConfig.getDropChance()));
			return;
		}
		//logger.info(String.format("[ChestDrop]["+worldname+"]: Passed random chance %1.2f",worldConfig.getDropChance()));

		/* Get random drop coords */
		double[] coords = Utils.getDrop(config, worldname);

		/* Load drop chunk */
		loadChunk(coords);

		/* Drop to ground loop */
		Block block = null;
		int y;
		// config.getServer().getLogger().info("[ChestDrop] chest is
		// falling...");
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
				loadChunk(coords);
				y=world.getMaxHeight()+1;
				logger.info("[ChestDrop]["+worldname+"]: Chest moved due to tree");
				continue;
			case LAVA:
			case STATIONARY_LAVA:
				/* unlucky drop */
				logger.info("[ChestDrop]["+worldname+"]: Chest fell in lava");
				return;
			default:
				break;
			}
			break;
		}
		if (y == 0) {
			config.getServer().getLogger().info("[ChestDrop] chest fell into the void...");
			return; // Abort if we make it to the void
		}
		// config.getServer().getLogger().info("[ChestDrop] chest has landed on
		// "+block.getType().name());

		/* Place Chest On Glowstone */
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
	}

	private void loadChunk(double[] coords) {
		Chunk chunk = world.getChunkAt((int) coords[0], (int) coords[1]);
		if (!chunk.isLoaded()) {
			chunk.load(true);
		}
	}

}
