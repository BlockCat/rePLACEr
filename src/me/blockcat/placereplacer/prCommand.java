package me.blockcat.placereplacer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class prCommand implements CommandExecutor {
	
	
	private PlaceReplacer plugin;
	private List<prRegion> regions = new ArrayList<prRegion>();
	private HashMap<Player, prRegion> selected = new HashMap<Player, prRegion>();

	public prCommand(PlaceReplacer plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;
		
		if (args.length == 0) {
			showHelp(player);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("select")) {			
			if (args.length > 1) {
				for (prRegion region : regions) {
					if (region.getName().equalsIgnoreCase(args[1])){
						selected.put(player, region);
						player.sendMessage(ChatColor.GREEN + "Selected region: " + region.getName() + "!");
						return true;
					}
				}
			}
		} else if (args[0].equalsIgnoreCase("create")) {
			prRegion pr = createNewRegion(player);
			pr.newBlocks();
			selected.put(player, pr);
			player.sendMessage(ChatColor.GOLD + "Now name your region.");
		} else if (args[0].equalsIgnoreCase("name")) {
			if (args.length > 1) {
				for (prRegion pr1 : regions) {
					if (pr1.getName().equalsIgnoreCase(args[1])){
						player.sendMessage(ChatColor.RED + "Name already in use!");
						return false;
					}						
				}
				prRegion pr = selected.get(player);				
				pr.setName(args[1]);	
				regions.add(pr);
				save(pr);
			} else {
				
			}
			
		} else if (args[0].equalsIgnoreCase("set")) {
			prRegion pr = selected.get(player);
			pr.place();
			player.sendMessage(ChatColor.GREEN + "Done");
		} else if (args[0].equalsIgnoreCase("list")) {
			String regs = ChatColor.GREEN + "";
			for (prRegion pr : regions) {
				regs += pr.getName() + "|";
			}
			player.sendMessage(regs);
		}
		
		return true;
	}

	private void save(prRegion pr) {
		File f = new File(plugin.getDataFolder(), "saves/" +pr.getWorld() + "/" + pr.getName()+".fr");
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
			f.createNewFile();
			} catch (Exception e) {}
		}
		try {
			GZIPOutputStream gzipo = new GZIPOutputStream(new FileOutputStream(f));
			DataOutputStream out = new DataOutputStream(gzipo);
			pr.save(out);
			out.close();
			gzipo.close();
		} catch (Exception e) {}
	}
	
	public void load() {
		for (World w : plugin.getServer().getWorlds()) {
			File f = new File(plugin.getDataFolder(), "saves/" +w.getName() + "/");
			for (File file : f.listFiles()) {
				try {
					
					GZIPInputStream gzipi = new GZIPInputStream(new FileInputStream(file));
					DataInputStream in = new DataInputStream(gzipi);
					prRegion pr = new prRegion();
					pr.load(in, file.getName().replace(".fr", ""), w);
					System.out.println(file.getName() + " loaded!");
					regions.add(pr);
					in.close();
					gzipi.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private prRegion createNewRegion(Player player) {
		int x1 = plugin.listener.block1.get(player).getBlockX();
		int y1 = plugin.listener.block1.get(player).getBlockY();
		int z1 = plugin.listener.block1.get(player).getBlockZ();
		int x2 = plugin.listener.block2.get(player).getBlockX();
		int y2 = plugin.listener.block2.get(player).getBlockY();
		int z2 = plugin.listener.block2.get(player).getBlockZ();;
		player.sendMessage("derp1");
		return new prRegion(player.getWorld(),x1,y1,z1,x2,y2,z2);
	}

	private void showHelp(Player player) {
		player.sendMessage("select");
		player.sendMessage("name");
		player.sendMessage("create");
		player.sendMessage("set");

	}

}
