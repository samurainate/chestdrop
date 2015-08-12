package io.github.samurainate.chestdrop;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreBasedGemModel implements GemModel {

	private ItemStack gem;

	public LoreBasedGemModel() {
		ItemStack gem = new ItemStack(Material.EMERALD, 1);
		ItemMeta meta = gem.getItemMeta();
		meta.setDisplayName("Hidden Gem");
		meta.setLore(Arrays.asList("Found in special chests around the world."));
		gem.setItemMeta(meta);
		this.gem = gem;
	}

	/* (non-Javadoc)
	 * @see io.github.samurainate.chestdrop.GemModel#hiddenGem(int)
	 */
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
	
	/* (non-Javadoc)
	 * @see io.github.samurainate.chestdrop.GemModel#isHiddenGem(org.bukkit.inventory.ItemStack)
	 */
	@Override
	public boolean isHiddenGem(ItemStack emerald) {
		return emerald.hasItemMeta() && emerald.getItemMeta().hasDisplayName()
				&& emerald.getItemMeta().getDisplayName().equals("Hidden Gem")
				&& emerald.getItemMeta().hasLore()
				&& emerald.getItemMeta().getLore().size()>0
				&& emerald.getItemMeta().getLore().get(0).equals("Found in special chests around the world.");
	}
}
