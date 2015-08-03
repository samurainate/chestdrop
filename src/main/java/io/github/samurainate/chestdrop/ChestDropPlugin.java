package io.github.samurainate.chestdrop;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestDropPlugin extends JavaPlugin implements Listener {

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

		/* Register as event handler */
		getServer().getPluginManager().registerEvents(this, this);

		/* Announce ready */
		getServer().getLogger().info("[ChestDrop] Ready");

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("dropchest")) {
			if (sender instanceof Player && sender.hasPermission("chestdrop.dropchest")) {
				int count = 1;
				if (args.length >= 1)
					try {
						count = Integer.parseInt(args[0]);
					} catch (NumberFormatException e) {
						count = 1;
					}
				for (int i = 0; i < count; i++)
					Utils.dropChest(pluginConfig, ((Player) sender).getWorld().getName());
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("tradegems")) {
			if (sender instanceof Player && sender.hasPermission("chestdrop.tradegems")) {
				Utils.displayTrades(pluginConfig, (Player) sender);
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("addtrade")) {
			if (sender instanceof Player && sender.hasPermission("chestdrop.addtrade") && (args.length >= 1)) {
				try {
					int cost = Integer.parseInt(args[0]);
					pluginConfig.registerTrade(
							new Trade("t" + System.currentTimeMillis(), ((Player) sender).getItemInHand(), cost));
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}
		}
		return false;
	}

	@EventHandler
	public void InventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();

		if (e.getInventory().getTitle().contains("Trade Hidden Gems")) {
			e.setCancelled(true); // Cancel the event so they can't take items
									// out of the GUI

			ItemStack item = e.getCurrentItem();
			if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null
					|| item.getItemMeta().getLore().size() < 2) {
				return;
			} else {
				String tradeName = item.getItemMeta().getLore().get(1);
				Trade trade = pluginConfig.getTrade(tradeName);
				if (trade==null)return;
				int gems = Utils.gemCount(p);
				if (gems >= trade.getCost()) {
					int slot = p.getInventory().firstEmpty();
					if (slot == -1 && gems > trade.getCost()) {
						p.sendMessage("You don't have room for that");
					} else {
						if (Utils.executeTrade(p, trade)) {
							p.sendMessage("Trade completed");
						} else {
							p.closeInventory();
							p.sendMessage("Trade failed");
						}
					}
				} else {
					p.sendMessage("You don't have enough Hidden Gems");
				}

			}
		}
	}
}
