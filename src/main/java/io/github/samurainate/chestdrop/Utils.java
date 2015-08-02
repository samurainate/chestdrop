package io.github.samurainate.chestdrop;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Utils {

	public static void worldBroadcast(Server server, String worldname, String format) {
		World world = server.getWorld(worldname);
		for (Player player : server.getOnlinePlayers()) {
			if (player.getWorld().equals(world))
				player.sendMessage(format);
		}

	}

	public static int getPlayerCount(Server server, String worldname) {
		int count = 0;
		World world = server.getWorld(worldname);
		for (Player player : server.getOnlinePlayers()) {
			if (player.getWorld().equals(world))
				count++;
		}
		return count;
	}

	/**
	 * @param config
	 *            the PluginConfig
	 * @param worldname
	 *            the world to get drop location in
	 * @return a random drop location NOTE: If WorldBorder integration is
	 *         enabled AND THERE IS A BORDER DEFINED, then drops are centered on
	 *         the center of the world's border. Otherwise, the drops are
	 *         centered on the world spawn location.
	 */
	public static double[] getDrop(PluginConfig config, String worldname) {
		double[] drop = null;
		WorldConfig worldConfig = config.getWorldConfig(worldname);
		int maxRange = worldConfig.getMaxRangeForDrops();
		// try world border method first
		if (config.isWorldBorderEnabled()) {
			// this returns null if no border enabled
			drop = config.getWb().randomCoordWithinBordersOf(worldname, maxRange);
			if (drop != null) return drop;
		}

		// either no world border integration or no border defined
		drop = new double[2];
		Location l = config.getServer().getWorld(worldname).getSpawnLocation();
		int minx = (int) (l.getX() - maxRange);
		int minz = (int) (l.getZ() - maxRange);
		drop[0] = config.getRandom().nextInt(maxRange) + minx;
		drop[1] = config.getRandom().nextInt(maxRange) + minz;

		return drop;
	}

	public static void scheduleTasks(PluginConfig config) {
		for (String worldname : config.getConfiguredWorlds()) {
			WorldConfig worldConfig = config.getWorldConfig(worldname);
			if (worldConfig.isEnabled()) {
			}
			config.getServer().getScheduler().scheduleSyncRepeatingTask(config.getPlugin(),
					new ChestDropTask(config, worldname), worldConfig.getDropInterval(), worldConfig.getDropInterval());
		}
	}
}
