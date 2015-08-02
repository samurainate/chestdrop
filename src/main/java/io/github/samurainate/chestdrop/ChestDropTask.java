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

		Utils.dropChest(config,worldname);
	}

}
