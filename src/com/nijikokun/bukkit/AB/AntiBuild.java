package com.nijikokun.bukkit.AB;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiBuild extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");

	public static final String name = "AntiBuild";
	public static final String codename = "Really Reborn";
	public static final String version = "1.1";

	public PListener pl = new PListener(this);
	public BListener bl = new BListener(this);
	public PermissionHandler permissions;

	public void onEnable() {
		getConfiguration().load();

		if (!setupPermissions())
			return;
		registerEvents();

		log.info("[" + name + "] version [" + version + " / " + codename + "] enabled");
	}

	public void onDisable() {
		log.info("[" + name + "] version [" + version + " / " + codename + "] disabled");
	}

	public boolean setupPermissions() {
		Plugin plugin = getServer().getPluginManager().getPlugin("Permissions");
		if (permissions == null) {
			if (plugin != null) {
				getServer().getPluginManager().enablePlugin(plugin);
				permissions = ((Permissions) plugin).getHandler();
			} else {
				log.info("[" + name + "] version [" + version + "] not enabled. Permissions not detected.");
				getServer().getPluginManager().disablePlugin(this);
				return false;
			}
		}
		return true;
	}

	private void registerEvents() {
		getServer().getPluginManager().registerEvent(Type.BLOCK_DAMAGED, bl, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.BLOCK_PLACED, bl, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_ITEM, pl, Priority.Normal, this);
	}

	public boolean canBuild(Player player) {
		String worldName = player.getWorld().getName();
		String group = permissions.getGroup(worldName, player.getName());
		if (group != null)
			return permissions.canGroupBuild(worldName, group);
		return true;
	}

}
