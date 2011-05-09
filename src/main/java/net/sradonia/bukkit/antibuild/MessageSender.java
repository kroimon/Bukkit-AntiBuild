package net.sradonia.bukkit.antibuild;

import java.util.WeakHashMap;

import org.bukkit.entity.Player;

public class MessageSender {
	private final String message;
	private final int coolDown;

	private final WeakHashMap<Player, Long> lastMessageTimes = new WeakHashMap<Player, Long>();

	/**
	 * @param message the text to send
	 * @param coolDown in seconds
	 */
	public MessageSender(String message, int coolDown) {
		this.message = message;
		this.coolDown = coolDown * 1000;
	}

	public void sendMessage(Player player) {
		Long lastTime = lastMessageTimes.get(player);
		if (lastTime != null && System.currentTimeMillis() - lastTime <= coolDown)
			return;

		player.sendMessage(message);
		lastMessageTimes.put(player, System.currentTimeMillis());
	}
}
