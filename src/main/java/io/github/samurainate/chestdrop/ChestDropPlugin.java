package io.github.samurainate.chestdrop;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class ChestDropPlugin extends JavaPlugin {

	private Logger logger;

	@Override
	public void onDisable() {
		super.onDisable();
		getServer().getScheduler().cancelTasks(this);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		logger = getServer().getLogger();

		/* Load plugin config */
		PluginConfig config = new PluginConfig(this);

		/* Announce WorldBorder integration */
		if (config.isWorldBorderEnabled())
			logger.info("[ChestDrop] WorldBorder integration enabled");

		/* Schedule drops */
		Utils.scheduleTasks(config);
		
		/* Announce ready */
		getServer().getLogger().info("[ChestDrop] Ready");

	}


}
