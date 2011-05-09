package net.sradonia.bukkit.antibuild;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BListener extends BlockListener {
	private final AntiBuild plugin;
	private final MessageSender message;

	public BListener(AntiBuild instance, MessageSender message) {
		this.plugin = instance;
		this.message = message;
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		if (!plugin.canBuild(player)) {
			event.setCancelled(true);
			if (message != null)
				message.sendMessage(player);
		}
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!plugin.canBuild(player)) {
			event.setCancelled(true);
			if (message != null)
				message.sendMessage(player);
		}
	}

}
