package net.sradonia.bukkit.antibuild;

import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class PListener extends PlayerListener {
	private final AntiBuild plugin;
	private final String message;
	private int messageCooldown;

	private WeakHashMap<Player, Long> lastMessageTimes = new WeakHashMap<Player, Long>();

	public PListener(AntiBuild instance, String message, int messageCooldown) {
		this.plugin = instance;
		this.message = message;
		this.messageCooldown = messageCooldown;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!plugin.canInteract(player)) {
			event.setCancelled(true);
			sendMessage(player);
		}
	}

	private void sendMessage(Player player) {
		if (message == null)
			return;

		Long lastTime = lastMessageTimes.get(player);
		if (lastTime != null && System.currentTimeMillis() - lastTime <= messageCooldown * 1000)
			return;

		player.sendMessage(message);
		lastMessageTimes.put(player, System.currentTimeMillis());
	}
}
