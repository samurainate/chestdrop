package io.github.samurainate.chestdrop;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BasicGemModel implements GemModel {

	private ItemStack gem;
	private String name;
	
	public BasicGemModel(String name, List<String> lore) {
		this.name=name;
		ItemStack gem = new ItemStack(Material.EMERALD, 1);
		ItemMeta meta = gem.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
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
				&& emerald.getItemMeta().getDisplayName().equals(name);
	}
	@Override
	public String getName() {
		return name;
	}

}
