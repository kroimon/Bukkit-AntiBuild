package com.nijikokun.bukkit.AB;

import com.nijikokun.bukkit.AB.AntiBuild;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BListener extends BlockListener {
	public static AntiBuild plugin;

	public BListener(AntiBuild instance) {
		plugin = instance;
	}

	public void onBlockDamage(BlockDamageEvent event) {
		if (!plugin.canBuild(event.getPlayer()))
			event.setCancelled(true);
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		if (!plugin.canBuild(event.getPlayer()))
			event.setCancelled(true);
	}

	public void onBlockInteract(BlockInteractEvent event) {
		if (event.isPlayer() && !plugin.canBuild((Player) event.getEntity()))
			event.setCancelled(true);
	}

}
