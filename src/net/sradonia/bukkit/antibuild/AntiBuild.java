package net.sradonia.bukkit.antibuild;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiBuild extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");

	private PermissionHandler permissions;
	private boolean multiworldSupport;

	private PListener pl = new PListener(this);
	private BListener bl = new BListener(this);

	public void onEnable() {
		PluginDescriptionFile pdf = getDescription();

		if (!setupPermissions()) {
			log.severe("[" + pdf.getName() + "] version " + pdf.getVersion() + " not enabled! Permission plugin not detected!");
			return;
		}

		getServer().getPluginManager().registerEvent(Type.BLOCK_DAMAGED, bl, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.BLOCK_PLACED, bl, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_ITEM, pl, Priority.Normal, this);

		log.info("[" + pdf.getName() + "] version " + pdf.getVersion() + " enabled " + (multiworldSupport ? "with" : "without") + " multiworld support");
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

}
