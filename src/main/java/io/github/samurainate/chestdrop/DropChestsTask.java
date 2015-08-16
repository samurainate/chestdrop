package io.github.samurainate.chestdrop;

public class DropChestsTask implements Runnable {

	private static final int CPS = 100;
	private PluginConfig pluginConfig;
	private String worldname;
	private int chestsToGo;
	private int taskNum;

	public DropChestsTask(PluginConfig pluginConfig, String name, int count) {
		this.pluginConfig = pluginConfig;
		this.worldname = name;
		this.taskNum = -1;
		this.chestsToGo=count;
		pluginConfig.getServer().getLogger().info("Create task to drop "+count+" chests");
	}

	@Override
	public void run() {
		if(chestsToGo<=0) pluginConfig.getServer().getScheduler().cancelTask(taskNum);
		
		for(int i = Math.min(CPS, chestsToGo);i>0&&chestsToGo>0;i--) {
			chestsToGo--;
			Utils.dropChest(pluginConfig, worldname);
		}
	}

	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}

}
