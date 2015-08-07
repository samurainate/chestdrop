package io.github.samurainate.chestdrop;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestDropPlugin extends JavaPlugin implements Listener {

	private Logger logger;
	private PluginConfig pluginConfig;
	private ChestDropEvents listener;

	@Override
	public void onDisable() {
		super.onDisable();
		getServer().getScheduler().cancelTasks(this);
		listener=null;
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
		PluginConfig pluginConfig = this.pluginConfig;
		if (cmd.getName().equalsIgnoreCase("dropchest")) {
			ChestDropCommands.dropChest(sender, args, pluginConfig);
		} else if (cmd.getName().equalsIgnoreCase("tradegems")) {
			ChestDropCommands.openTradeUI(sender, pluginConfig);
		} else if (cmd.getName().equalsIgnoreCase("addtrade")) {
			ChestDropCommands.addTrade(sender, args, pluginConfig);
		} else if (cmd.getName().equalsIgnoreCase("givegems")) {
			ChestDropCommands.addTrade(sender, args, pluginConfig);
		}
		return false;
	}

}
