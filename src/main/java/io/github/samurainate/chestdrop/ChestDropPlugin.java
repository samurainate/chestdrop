package io.github.samurainate.chestdrop;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestDropPlugin extends JavaPlugin {

	private Logger logger;
	private PluginConfig pluginConfig;

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
		PluginConfig pluginConfig = new PluginConfig(this);
		this.pluginConfig = pluginConfig;

		/* Announce WorldBorder integration */
		if (pluginConfig.isWorldBorderEnabled())
			logger.info("[ChestDrop] WorldBorder integration enabled");

		/* Schedule drops */
		Utils.scheduleTasks(pluginConfig);
		
		/* Announce ready */
		getServer().getLogger().info("[ChestDrop] Ready");

	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dropchest")) { 
			if (sender instanceof Player && sender.hasPermission("chestdrop.dropchest")) {
				Utils.dropChest(pluginConfig, ((Player)sender).getWorld().getName());
				return true;
			}
		} 
		return false; 
	}

}
