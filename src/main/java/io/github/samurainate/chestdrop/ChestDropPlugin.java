package io.github.samurainate.chestdrop;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestDropPlugin extends JavaPlugin implements Listener {

	private Logger logger;
	private PluginConfig pluginConfig;

	@Override
	public void onDisable() {
		super.onDisable();
		getServer().getScheduler().cancelTasks(this);
		/* Announce ready */
		getServer().getLogger().info("[ChestDrop] Disabled");
	}

	@Override
	public void onEnable() {
		super.onEnable();
		logger = getServer().getLogger();

		/* Load plugin config */
		this.pluginConfig = new PluginConfig(this);

		/* Announce WorldBorder integration */
		if (pluginConfig.isWorldBorderEnabled())
			logger.info("[ChestDrop] WorldBorder integration enabled");

		/* Schedule drops */
		Utils.scheduleTasks(pluginConfig);

		/* Register as event handler */
		getServer().getPluginManager().registerEvents(new ChestDropEvents(pluginConfig), this);

		/* Announce ready */
		getServer().getLogger().info("[ChestDrop] Enabled");

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dropchest")) {
			return ChestDropCommands.dropChest(sender, args, pluginConfig);
		} else if (cmd.getName().equalsIgnoreCase("tradegems")) {
			return ChestDropCommands.openTradeUI(sender, pluginConfig);
		} else if (cmd.getName().equalsIgnoreCase("addtrade")) {
			return ChestDropCommands.addTrade(sender, args, pluginConfig);
		} else if (cmd.getName().equalsIgnoreCase("givegems")) {
			return ChestDropCommands.giveGems(sender, args, pluginConfig);
		}
		return false;
	}

}
