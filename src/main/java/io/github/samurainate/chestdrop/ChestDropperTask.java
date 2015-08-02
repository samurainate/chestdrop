package io.github.samurainate.chestdrop;

import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestDropperTask implements Runnable {

	private ChestDropConfig config;
	private String worldname;
	private World world;

	/**
	 * @param config
	 *            The plugin config
	 * @param worldname
	 *            World to drop chests on
	 */
	public ChestDropperTask(ChestDropConfig config, String worldname) {
		this.config = config;
		this.worldname = worldname;
		this.world = config.getServer().getWorld(worldname);
	}

	public void run() {
		/* Drop only if players in world */
		if(config.getPlayerCount(world)==0) return;
		
		/* Drop one in ten opportunities */
		if(config.getRandom().nextDouble()>0.1) return;
				
		/* Get drop coords */
		double[] coords = config.getDrop(worldname);
		
		/* Load drop chunk */
		Chunk chunk = world.getChunkAt((int) coords[0], (int) coords[1]);
		if (!chunk.isLoaded()){
			//config.getServer().getLogger().info("[ChestDrop] loading chunk...");
			chunk.load(true);
			//config.getServer().getLogger().info("[ChestDrop] chunk loaded...");
		}
		
		/* Drop to ground loop */
		Block block=null;
		int y;
		//config.getServer().getLogger().info("[ChestDrop] chest is falling...");
		for (y = world.getMaxHeight(); y > 0; y--) {
			block = world.getBlockAt((int) coords[0], y - 1, (int) coords[1]);
			if (block.getType() != Material.AIR)
				break; // found a surface, maybe
		}
		if (y == 0) {
			config.getServer().getLogger().info("[ChestDrop] chest fell into the void...");
			return; // Abort if we make it to above the void
		}
		//config.getServer().getLogger().info("[ChestDrop] chest has landed on "+block.getType().name());
		
		/* Place chest */
		block = world.getBlockAt((int) coords[0], y, (int) coords[1]);
		block.setType(Material.CHEST);
		//config.getServer().getLogger().info("[ChestDrop] generating treasure...");
		Chest chest = (Chest) block.getState();
		Inventory in = chest.getBlockInventory();
		ItemStack gem = new ItemStack(Material.DIAMOND,1);
		gem.getItemMeta().setDisplayName("Hidden Gem");
		in.addItem(new ItemStack(Material.DIAMOND,1));
		world.playEffect(block.getLocation(), Effect.PARTICLE_SMOKE, 0);
		//config.getServer().getLogger().info("[ChestDrop] notifying players...");
		
		/* Notify players */
		config.worldBroadcast(world,String.format("Chest dropped at %1.0f, %1.0f!", coords[0], coords[1]));
	}

}
