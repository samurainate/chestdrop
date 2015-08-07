package io.github.samurainate.chestdrop;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChestDropCommands {

	public static boolean addTrade(CommandSender sender, String[] args, PluginConfig pluginConfig) {
		if (sender instanceof Player && sender.hasPermission("chestdrop.addtrade") && (args.length >= 1)) {
			try {
				int cost = Integer.parseInt(args[0]);
				ItemStack itemInHand = ((Player) sender).getItemInHand();
				pluginConfig.registerTrade(new Trade("t" + System.currentTimeMillis(), itemInHand, cost));
				sender.sendMessage(String.format("Trade created: %d %s for %d Hidden Gems", itemInHand.getAmount(),
						itemInHand.toString(), cost));
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}

	public static boolean openTradeUI(CommandSender sender, PluginConfig pluginConfig) {
		if (sender instanceof Player && sender.hasPermission("chestdrop.tradegems")) {
			Utils.displayTrades(pluginConfig, (Player) sender);
			return true;
		}
		return false;
	}

	public static boolean dropChest(CommandSender sender, String[] args, PluginConfig pluginConfig) {
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
		return false;
	}

}
