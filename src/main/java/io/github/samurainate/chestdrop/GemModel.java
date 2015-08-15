package io.github.samurainate.chestdrop;

import org.bukkit.inventory.ItemStack;

public interface GemModel {

	ItemStack hiddenGem(int count);

	boolean isHiddenGem(ItemStack emerald);

	String getName();

}