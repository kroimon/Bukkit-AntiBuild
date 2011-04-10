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

	private static final String CONFIG_MESSAGE = "message";
	private static final String CONFIG_MESSAGE_DEFAULT = "You don't have permission to build!";
	private static final String CONFIG_INTERACT_CHECK = "interactCheck";
	private static final boolean CONFIG_INTERACT_CHECK_DEFAULT = false;
	private static final String CONFIG_INTERACT_MESSAGE = "interactMessage";
	private static final String CONFIG_INTERACT_MESSAGE_DEFAULT = "You don't have permission to interact with the world!";
	

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
		if (config.getProperty(CONFIG_MESSAGE) == null) {
			config.setProperty(CONFIG_MESSAGE, CONFIG_MESSAGE_DEFAULT);
			configChanged = true;
		}
		if (config.getProperty(CONFIG_INTERACT_CHECK) == null) {
			config.setProperty(CONFIG_INTERACT_CHECK, CONFIG_INTERACT_CHECK_DEFAULT);
			configChanged = true;
		}
		if (config.getProperty(CONFIG_INTERACT_MESSAGE) == null) {
			config.setProperty(CONFIG_INTERACT_MESSAGE, CONFIG_INTERACT_MESSAGE_DEFAULT);
			configChanged = true;
		}
		if (configChanged)
			config.save();

		// Register listeners
		PluginManager pluginManager = getServer().getPluginManager();
		BListener bl = new BListener(this, getConfigString(CONFIG_MESSAGE));
		pluginManager.registerEvent(Type.BLOCK_DAMAGE, bl, Priority.Normal, this);
		pluginManager.registerEvent(Type.BLOCK_PLACE, bl, Priority.Normal, this);

		if (config.getBoolean(CONFIG_INTERACT_CHECK, CONFIG_INTERACT_CHECK_DEFAULT)) {
			PListener pl = new PListener(this, getConfigString(CONFIG_INTERACT_MESSAGE));
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
			getServer().getPluginManager().enablePlugin(plugin);
			permissions = ((Permissions) plugin).getHandler();

			String pluginVersion = plugin.getDescription().getVersion();
			multiworldSupport = pluginVersion.compareTo("2.1") >= 0;
		} else {
			getServer().getPluginManager().disablePlugin(this);
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
