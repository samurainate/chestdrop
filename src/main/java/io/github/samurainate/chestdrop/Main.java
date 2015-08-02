package io.github.samurainate.chestdrop;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private Logger logger;

	@Override
	public void onDisable() {
		super.onDisable();
		getServer().getScheduler().cancelTasks(this);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		logger=getServer().getLogger();
		ChestDropConfig config = new ChestDropConfig(this);

		if (config.getWorlds().size() == 0) {
			/* Nothing to do */
			logger.info("[ChestDrop] No worlds enabled");
		} else {
			/* Schedule drops */
			String name;
			for (World world : config.getWorlds()) {
				name = world.getName();
				getServer().getScheduler().scheduleSyncRepeatingTask(this, new ChestDropperTask(config, name),
						config.getDelay(name), config.getInterval(name));
			}
			logger.info("[ChestDrop] Ready");
		}
	}

}
