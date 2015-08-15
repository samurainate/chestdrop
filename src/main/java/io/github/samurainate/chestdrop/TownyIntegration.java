package io.github.samurainate.chestdrop;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.WorldCoord;

public class TownyIntegration {
	

	@SuppressWarnings("unused")
	private Towny plugin;

	public TownyIntegration(PluginConfig config, Plugin test) {
		this.plugin = (Towny) test;
	}
	
	public boolean isWild(Location loc) {
		WorldCoord wc = WorldCoord.parseWorldCoord(loc);
		try {
			wc.getTownBlock().getTown();
		}catch (NotRegisteredException e) {
			return true;
		}
		return false;
	}
}
