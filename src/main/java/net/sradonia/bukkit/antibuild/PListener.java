package net.sradonia.bukkit.antibuild;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class PListener extends PlayerListener {
	private final AntiBuild plugin;
	private final MessageSender message;

	public PListener(AntiBuild instance, MessageSender message) {
		this.plugin = instance;
		this.message = message;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!plugin.canInteract(player)) {
			event.setCancelled(true);
			if (message != null)
				message.sendMessage(player);
		}
	}
}
