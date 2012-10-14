package me.blockcat.replace;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Lever;
import org.bukkit.metadata.FixedMetadataValue;

public class prListener implements Listener {


	private RePlace plugin;

	public prListener(RePlace plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract (PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if (event.getAction() == Action.LEFT_CLICK_BLOCK && player.getItemInHand().getType() == Material.BLAZE_ROD) {
			player.setMetadata("x1", new FixedMetadataValue(plugin, block.getX()));
			player.setMetadata("y1", new FixedMetadataValue(plugin, block.getY()));
			player.setMetadata("z1", new FixedMetadataValue(plugin, block.getZ()));
			player.sendMessage(ChatColor.GOLD + "Selected first point.");
			event.setCancelled(true);
			return;
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getItemInHand().getType() == Material.BLAZE_ROD) {
			player.setMetadata("x2", new FixedMetadataValue(plugin, block.getX()));
			player.setMetadata("y2", new FixedMetadataValue(plugin, block.getY()));
			player.setMetadata("z2", new FixedMetadataValue(plugin, block.getZ()));
			player.sendMessage(ChatColor.GOLD + "Selected second point.");
			event.setCancelled(true);
			return;
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (block.getTypeId() == 63 || block.getTypeId() == 68)) {
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();

			if (lines.length > 2) {
				if (!sign.getLine(0).equalsIgnoreCase("[replace]")) return;

				String name = sign.getLine(1);

				prRegion pr = new prRegion();
				pr.place(name, block.getWorld());
				event.setCancelled(true);
				player.sendMessage(ChatColor.GREEN + "Done!");
				return;
			}
		}
	}

	@EventHandler
	public void onRedstoneChange(BlockRedstoneEvent e) {
		Block block = e.getBlock();

		int startX = block.getX()-1;
		int startY = block.getY()-1;
		int startZ = block.getZ()-1;
		int endX = block.getX()+1;
		int endY = block.getY()+1;
		int endZ = block.getZ()+1;
		for(int x = startX; x <= endX; x++)
			for(int y = startY; y <= endY; y++)
				for(int z = startZ; z <= endZ; z++)
					if(block.getWorld().getBlockAt(x,y,z).getTypeId() == 63 || block.getWorld().getBlockAt(x,y,z).getTypeId() == 68)
					{
						Block b = block.getWorld().getBlockAt(x,y,z);
						Sign sign = (Sign) b.getState();

						if (b.isBlockPowered()){
							if(sign.getLine(0).equalsIgnoreCase("[replace]")) {
								if (!sign.getLine(1).equalsIgnoreCase("")) {
									prRegion pr = new prRegion();
									pr.place(sign.getLine(1), sign.getWorld());
								}
							}		
						}
					}
	}
}
