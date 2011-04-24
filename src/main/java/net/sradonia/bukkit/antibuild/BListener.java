package net.sradonia.bukkit.antibuild;

import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BListener extends BlockListener {
	private final AntiBuild plugin;
	private final String message;

	private static final int MESSAGE_COOLDOWN = 3000; // 3 seconds
	private WeakHashMap<Player, Long> lastMessageTimes = new WeakHashMap<Player, Long>();

	public BListener(AntiBuild instance, String message) {
		this.plugin = instance;
		this.message = message;
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		if (!plugin.canBuild(player)) {
			event.setCancelled(true);
			sendMessage(player);
		}
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!plugin.canBuild(player)) {
			event.setCancelled(true);
			sendMessage(player);
		}
	}

	private void sendMessage(Player player) {
		if (message == null)
			return;

		Long lastTime = lastMessageTimes.get(player);
		if (lastTime != null && System.currentTimeMillis() - lastTime <= MESSAGE_COOLDOWN)
			return;

		player.sendMessage(message);
		lastMessageTimes.put(player, System.currentTimeMillis());
	}
}
