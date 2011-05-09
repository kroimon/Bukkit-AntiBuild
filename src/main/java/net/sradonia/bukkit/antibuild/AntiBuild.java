package net.sradonia.bukkit.antibuild;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class AntiBuild extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");

	private PermissionHandler permissions;
	private boolean multiworldSupport;

	public void onEnable() {
		PluginDescriptionFile pdf = getDescription();

		if (!setupPermissions()) {
			log.severe("[" + pdf.getName() + "] version " + pdf.getVersion() + " not enabled! Permission plugin not detected!");
			return;
		}

		// Load configuration
		Configuration config = getConfiguration();
		boolean configChanged = false;
		if (config.getProperty("build.message") == null) {
			config.setProperty("build.message", config.getString("message", "You don't have permission to build!"));
			config.removeProperty("message");
			configChanged = true;
		}
		if (config.getProperty("build.messageCooldown") == null) {
			config.setProperty("build.messageCooldown", 3);
			configChanged = true;
		}
		if (config.getProperty("interaction.check") == null) {
			config.setProperty("interaction.check", config.getBoolean("interactCheck", false));
			config.removeProperty("interactCheck");
			configChanged = true;
		}
		if (config.getProperty("interaction.message") == null) {
			config.setProperty("interaction.message", config.getString("interactMessage", "You don't have permission to interact with the world!"));
			config.removeProperty("interactMessage");
			configChanged = true;
		}
		if (config.getProperty("interaction.messageCooldown") == null) {
			config.setProperty("interaction.messageCooldown", 3);
			configChanged = true;
		}
		if (configChanged)
			config.save();

		// Register listeners
		PluginManager pluginManager = getServer().getPluginManager();

		String message = getConfigString("build.message");
		MessageSender messageSender = (message == null) ? null : new MessageSender(message, config.getInt("build.messageCooldown", 3));

		final BListener bl = new BListener(this, messageSender);
		pluginManager.registerEvent(Type.BLOCK_DAMAGE, bl, Priority.Normal, this);
		pluginManager.registerEvent(Type.BLOCK_PLACE, bl, Priority.Normal, this);
		
		final EListener el = new EListener(this, messageSender);
		pluginManager.registerEvent(Type.PAINTING_BREAK, el, Priority.Normal, this);
		pluginManager.registerEvent(Type.PAINTING_PLACE, el, Priority.Normal, this);

		if (config.getBoolean("interaction.check", false)) {
			message = getConfigString("interaction.message");
			messageSender = (message == null) ? null : new MessageSender(message, config.getInt("interaction.messageCooldown", 3));

			final PListener pl = new PListener(this, messageSender);
			pluginManager.registerEvent(Type.PLAYER_INTERACT, pl, Priority.Normal, this);

			log.info("[" + pdf.getName() + "] registered interaction listener");
		}

		log.info("[" + pdf.getName() + "] version " + pdf.getVersion() + " enabled " + (multiworldSupport ? "with" : "without") + " multiworld support");
	}

	private String getConfigString(String path) {
		String s = getConfiguration().getString(path);
		if (s != null) {
			s = s.trim();
			if (s.length() == 0)
				s = null;
		}
		return s;
	}

	public void onDisable() {
		permissions = null;

		PluginDescriptionFile pdf = getDescription();
		log.info("[" + pdf.getName() + "] version " + pdf.getVersion() + " disabled");
	}

	private boolean setupPermissions() {
		Plugin plugin = getServer().getPluginManager().getPlugin("Permissions");
		if (plugin != null) {
			// if we got it, it should already be enabled due to Bukkits dependency resolver.
			permissions = ((Permissions) plugin).getHandler();

			String pluginVersion = plugin.getDescription().getVersion();
			multiworldSupport = pluginVersion.compareTo("2.1") >= 0;
		} else {
			setEnabled(false);
			return false;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean canBuild(Player player) {
		if (multiworldSupport) {
			String worldName = player.getWorld().getName();
			String group = permissions.getGroup(worldName, player.getName());
			if (group != null)
				return permissions.canGroupBuild(worldName, group);
		} else {
			String group = permissions.getGroup(player.getName());
			if (group != null)
				return permissions.canGroupBuild(group);
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean canInteract(Player player) {
		if (multiworldSupport) {
			String worldName = player.getWorld().getName();
			String group = permissions.getGroup(worldName, player.getName());
			if (group != null)
				return permissions.getGroupPermissionBoolean(worldName, group, "interact"); 
		} else {
			String group = permissions.getGroup(player.getName());
			if (group != null)
				return permissions.getGroupPermissionBoolean(group, "interact"); 
		}
		return true;
	}

}
