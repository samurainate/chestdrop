package io.github.samurainate.chestdrop;

import java.util.Random;

import org.bukkit.plugin.Plugin;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;

public class WorldBorderIntegration {

	private WorldBorder wbplugin;
	private Random rand;

	public WorldBorderIntegration(ChestDropConfig config, Plugin test) {
		this.wbplugin = (WorldBorder) test;
		this.rand = config.getRandom();
	}

	public double[] randomCoordWithinBordersOf(String worldname) {
		BorderData borderData = wbplugin.getWorldBorder(worldname);
		if (borderData == null) {
			return null;
		}
		double xmin = borderData.getX() - borderData.getRadiusX();
		double xrng = 2f * borderData.getRadiusX();
		double zmin = borderData.getZ() - borderData.getRadiusZ();
		double zrng = 2f * borderData.getRadiusZ();
		double[] coord = new double[2];
		do {
			coord[0] = rand.nextDouble() * xrng + xmin;
			coord[1] = rand.nextDouble() * zrng + zmin;
		} while (!borderData.insideBorder(coord[0], coord[1]));

		return coord;
	}

}
