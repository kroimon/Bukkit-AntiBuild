package net.sradonia.bukkit.antibuild;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;

public class PListener extends PlayerListener {
	private final AntiBuild plugin;
	private final String message;

	public PListener(AntiBuild instance, String message) {
		this.plugin = instance;
		this.message = message;
	}

	public void onPlayerItem(PlayerItemEvent event) {
		Player player = event.getPlayer();
		if (!plugin.canBuild(player)) {
			event.setCancelled(true);
			if (message != null)
				player.sendMessage(message);
		}
	}

}
