package io.github.samurainate.chestdrop;

import java.util.Random;

import org.bukkit.plugin.Plugin;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;

public class WorldBorderIntegration {

	private WorldBorder wbplugin;
	private Random rand;

	public WorldBorderIntegration(PluginConfig config, Plugin test) {
		this.wbplugin = (WorldBorder) test;
		this.rand = config.getRandom();
	}

	public double[] randomCoordWithinBordersOf(String worldname, int maxRange) {
		BorderData borderData = wbplugin.getWorldBorder(worldname);
		if (borderData == null) {
			return null;
		}
		
		/* enforce max range */
		int xrad = borderData.getRadiusX();
		xrad = xrad>maxRange?maxRange:xrad;
		int zrad = borderData.getRadiusZ();
		zrad = zrad>maxRange?maxRange:zrad;
		
		/* get a uniform random coordinate inside the world border */
		double xmin = borderData.getX() - xrad;
		double zmin = borderData.getZ() - zrad;
		double[] coord = new double[2];
		do {
			coord[0] = rand.nextDouble() * 2f * xrad + xmin;
			coord[1] = rand.nextDouble() * 2f * zrad + zmin;
		} while (!borderData.insideBorder(coord[0], coord[1]));

		return coord;
	}

}
