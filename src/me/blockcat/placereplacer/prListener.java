package me.blockcat.placereplacer;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class prListener implements Listener {
	
	HashMap<Player, Location> block1 = new HashMap<Player, Location>();
	HashMap<Player, Location> block2 = new HashMap<Player, Location>();
	private PlaceReplacer plugin;
	
	public prListener(PlaceReplacer plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract (PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && player.getItemInHand().getType() == Material.BLAZE_ROD) {
			block1.put(player,block.getLocation());
			player.sendMessage(ChatColor.GOLD + "Selected first point.");
			event.setCancelled(true);
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getItemInHand().getType() == Material.BLAZE_ROD) {
			block2.put(player,block.getLocation());
			player.sendMessage(ChatColor.GOLD + "Selected second point.");
			event.setCancelled(true);
		}
	}

}
