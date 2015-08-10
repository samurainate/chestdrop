package io.github.samurainate.chestdrop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChestDropEvents implements Listener {

	private PluginConfig pluginConfig;

	public ChestDropEvents(PluginConfig pluginConfig) {
		this.pluginConfig = pluginConfig;
	}

	@EventHandler
	public void InventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();

		if (e.getInventory().getTitle().contains("Trade Hidden Gems")) {
			tradeGems(e, p);
			return;
		} else if (e.getInventory().getTitle().contains("Delete Hidden Gem Trades")) {
			deleteTrade(e, p);
			return;
		} else if (e.getInventory().getTitle().contains("Confirm Delete Gem Trade?")) {
			confirmDeleteTrade(e, p);
			return;
		}
	}

	// TODO: implement confirmDelete
	private void confirmDeleteTrade(InventoryClickEvent e, Player p) {
		/* Prevent taking items */
		e.setCancelled(true);
		ItemStack item = e.getCurrentItem();
		if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null
				|| item.getItemMeta().getLore().size() < 2) {
			return;
		} else {
			String tradeName = item.getItemMeta().getLore().get(1);
			pluginConfig.removeTrade(tradeName);
			p.sendMessage("Trade removed.");

		}
	}

	// TODO: implement deleteTrade
	private void deleteTrade(InventoryClickEvent e, Player p) {
		/* Prevent taking items */
		e.setCancelled(true);
		ItemStack item = e.getCurrentItem();
		if (isClickableLoredItem(item)) {
			return;
		} else {
			p.sendMessage("Trade completed");
			Utils.displayConfirmDelete(pluginConfig, p, item);

		}
	}

	private boolean isClickableLoredItem(ItemStack item){
		return item == null || item.getType()==Material.AIR || item.getItemMeta() == null || item.getItemMeta().getLore() == null	
				|| item.getItemMeta().getLore().size() < 2;
	}

	private void tradeGems(InventoryClickEvent e, Player p) {
		/* Prevent taking items */
		e.setCancelled(true);
		ItemStack item = e.getCurrentItem();
		if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null
				|| item.getItemMeta().getLore().size() < 2) {
			return;
		} else {
			String tradeName = item.getItemMeta().getLore().get(1);
			Trade trade = pluginConfig.getTrade(tradeName);
			if (trade == null)
				return;
			int gems = Utils.gemCount(p);
			if (gems >= trade.getCost()) {
				if (Utils.executeTrade(p, trade)) {
					p.sendMessage("Trade completed");
				} else {
					p.closeInventory();
					p.sendMessage("Trade failed");
				}
			} else {
				p.sendMessage("You don't have enough Hidden Gems");
			}

		}
	}
}
