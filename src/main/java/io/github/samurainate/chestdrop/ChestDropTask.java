package io.github.samurainate.chestdrop;

public class ChestDropTask implements Runnable {

	private PluginConfig config;
	private String worldname;
	private WorldConfig worldConfig;

	/**
	 * @param config
	 *            The plugin config
	 * @param worldname
	 *            World to drop chests on
	 */
	public ChestDropTask(PluginConfig config, String worldname) {
		this.config = config;
		this.worldname = worldname;
		this.worldConfig = config.getWorldConfig(worldname);
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
