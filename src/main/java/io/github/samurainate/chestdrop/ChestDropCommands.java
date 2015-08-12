package io.github.samurainate.chestdrop;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChestDropCommands {

	public static boolean addTrade(CommandSender sender, String[] args, PluginConfig pluginConfig) {
		if (sender instanceof Player && sender.hasPermission("chestdrop.addtrade") && (args.length >= 1)) {
			try {
				int cost = Integer.parseInt(args[0]);
				ItemStack itemInHand = ((Player) sender).getItemInHand();
				if (itemInHand.getType() == Material.AIR) {
					sender.sendMessage("You aren't holding an item!");
					return true;
				}
				pluginConfig.registerTrade(new Trade("t" + System.currentTimeMillis(), itemInHand, cost));
				sender.sendMessage(String.format("Trade created: %d %s for %d Hidden Gems", itemInHand.getAmount(),
						itemInHand.toString(), cost));
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else {
			sender.sendMessage("This command is only usable by players");
			return true;
		}
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
			if (args.length >= 1) {
				try {
					count = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					;
				}
			}
			for (int i = 0; i < count; i++)
				Utils.dropChest(pluginConfig, ((Player) sender).getWorld().getName());
			return true;
		}
		return false;
	}

	public static boolean giveGems(CommandSender sender, String[] args, PluginConfig pluginConfig) {
		if (sender instanceof Player && sender.hasPermission("chestdrop.givegems")) {
			/* Default 1 gem to sender */
			int count = 1;
			Player player = (Player) sender;
			if (args.length >= 2) {
				try {
					count = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					return false;
				}
				for (int i = 1; i < args.length; i++) {
					if (count <= 0) {
						sender.sendMessage("Gave no Hidden Gems to " + args[i]);
					} else {
						player = pluginConfig.getServer().getPlayer(args[i]);
						if (player == null) {
							sender.sendMessage("Player not found: " + args[i]);
						} else {
							int gemsToGo = count;
							ItemStack gems;
							while (gemsToGo > 0) {
								gems = pluginConfig.gemModel().hiddenGem(gemsToGo);
								gemsToGo -= gems.getAmount();
								Utils.giveItem(player, gems);
								//sender.sendMessage("Gave " + gems.getAmount() + " Hidden Gems to " + args[i]);
							}
							sender.sendMessage("Gave " + count + " Hidden Gems to " + args[i]);
						}
					}
				}
			} else {
				return false;
			}
			return true;
		}
		return false;
	}

}
