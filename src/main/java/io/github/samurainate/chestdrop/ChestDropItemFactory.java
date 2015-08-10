package io.github.samurainate.chestdrop;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChestDropItemFactory {

	@SuppressWarnings("unused")
	private PluginConfig config;
	private ItemStack gem;

	public ChestDropItemFactory(PluginConfig config) {
		this.config = config;
		ItemStack gem = new ItemStack(Material.EMERALD, 1);
		ItemMeta meta = gem.getItemMeta();
		meta.setDisplayName("Hidden Gem");
		meta.setLore(Arrays.asList("Found in special chests around the world."));
		gem.setItemMeta(meta);
		this.gem = gem;
	}

	public ItemStack hiddenGem(int count) {
		if (count < 1)
			return null;
		if (count > Material.EMERALD.getMaxStackSize())
			count = Material.EMERALD.getMaxStackSize();
		ItemStack gems = gem.clone();
		gems.setAmount(count);
		return gems;
	}
}
