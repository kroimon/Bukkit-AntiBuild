package net.sradonia.bukkit.antibuild;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;

public class EListener extends EntityListener {
	private final AntiBuild plugin;
	private final MessageSender message;

	public EListener(AntiBuild instance, MessageSender message) {
		this.plugin = instance;
		this.message = message;
	}

	@Override
	public void onPaintingPlace(PaintingPlaceEvent event) {
		final Player player = event.getPlayer();
		if (!plugin.canBuild(player)) {
			event.setCancelled(true);
			if (message != null)
				message.sendMessage(player);
		}
	}

	@Override
	public void onPaintingBreak(PaintingBreakEvent event) {
		if (event instanceof PaintingBreakByEntityEvent) {
			final Entity remover = ((PaintingBreakByEntityEvent) event).getRemover();
			if (remover instanceof Player) {
				if (!plugin.canBuild((Player) remover)) {
					event.setCancelled(true);
					if (message != null)
						message.sendMessage((Player) remover);
				}
			}
		}
	}
}
