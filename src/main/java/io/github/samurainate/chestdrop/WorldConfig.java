package io.github.samurainate.chestdrop;

public class WorldConfig {
	private static final int MIN_DROP_INTERVAL = 20;
	private String worldname;
	private boolean enabled = false;
	private boolean dropWhenEmpty = false;
	private int maxRangeForDrops = 1000;
	private int dropInterval = 1200; // 60 seconds in ticks
	private double dropChance = 0.1;

	public String getWorldname() {
		return worldname;
	}

	protected void setWorldname(String worldname) {
		this.worldname = worldname;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.rationalize();
	}

	public boolean isDropWhenEmpty() {
		return dropWhenEmpty;
	}

	public void setDropWhenEmpty(boolean dropWhenEmpty) {
		this.dropWhenEmpty = dropWhenEmpty;
	}

	public WorldConfig(String worldname) {
		this.setWorldname(worldname);
	}

	public int getDropInterval() {
		return dropInterval;
	}

	public void setDropInterval(int dropInterval) {
		this.dropInterval = dropInterval;
		this.rationalize();
	}

	public double getDropChance() {
		return dropChance;
	}

	public void setDropChance(double dropChance) {
		this.dropChance = dropChance;
		this.rationalize();
	}

	public int getMaxRangeForDrops() {
		return maxRangeForDrops;
	}

	public void setMaxRangeForDrops(int maxRangeForDrops) {
		this.maxRangeForDrops = maxRangeForDrops;
		this.rationalize();
	}

	/**
	 * Brings parameters in rational ranges. Enforces MIN_DROP_INTERVAL.
	 * Disables the world if dropChance is zero, or
	 * if maxRangeForDrops is zero.
	 */
	public void rationalize() {
		if (this.dropInterval < MIN_DROP_INTERVAL) {
			this.dropInterval = MIN_DROP_INTERVAL;
		}
		if (this.maxRangeForDrops < 0) {
			this.maxRangeForDrops = 0;
		}
		if (this.dropChance < 0d) {
			this.dropChance = 0d;
		}
		if (this.dropChance > 1d) {
			this.dropChance = 1d;
		}
		if (this.maxRangeForDrops <= 0 || this.dropChance <= 0d)
			this.enabled = false;
	}

}
