package io.github.samurainate.chestdrop;

import org.bukkit.inventory.ItemStack;

public class Trade {
	public Trade(ItemStack itemInHand, int cost) {
		this.items = itemInHand;
		this.cost = cost;
	}

	public Trade(String name, ItemStack itemInHand, int cost) {
		this.name = name;
		this.items = itemInHand;
		this.cost = cost;
	}

	String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	ItemStack items;
	int cost;

	public ItemStack getItems() {
		return items;
	}

	public int getCost() {
		return cost;
	}

	@Override
	public int hashCode() {
		return items.toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Trade))
			return false;
		Trade tobj = (Trade) obj;
		if (this.items.equals(tobj))
			return true;
		return false;
	}

}
