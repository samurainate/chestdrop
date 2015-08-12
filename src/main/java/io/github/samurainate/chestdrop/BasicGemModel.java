package io.github.samurainate.chestdrop;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BasicGemModel implements GemModel {

	private ItemStack gem;
	
	public BasicGemModel() {
		ItemStack gem = new ItemStack(Material.EMERALD, 1);
		ItemMeta meta = gem.getItemMeta();
		meta.setDisplayName("Hidden Gem");
		meta.setLore(Arrays.asList("Found in special chests around the world."));
		gem.setItemMeta(meta);
		this.gem = gem;
	}
	@Override
	public ItemStack hiddenGem(int count) {
		if (count < 1)
			return null;
		if (count > Material.EMERALD.getMaxStackSize())
			count = Material.EMERALD.getMaxStackSize();
		ItemStack gems = gem.clone();
		gems.setAmount(count);
		return gems;
	}

	@Override
	public boolean isHiddenGem(ItemStack emerald) {
		return emerald.hasItemMeta() && emerald.getItemMeta().hasDisplayName()
				&& emerald.getItemMeta().getDisplayName().equals("Hidden Gem");
	}

}
