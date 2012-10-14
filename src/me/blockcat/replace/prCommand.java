package me.blockcat.replace;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class prCommand implements CommandExecutor {


	private RePlace plugin;
	private List<String> regions = new ArrayList<String>();
	private HashMap<Player, String> selected = new HashMap<Player, String>();

	public prCommand(RePlace plugin) {
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
				for (String region : regions) {

					if (region.equalsIgnoreCase(args[1])){
						selected.put(player, region);
						player.sendMessage(ChatColor.GREEN + "Selected region: " + region + "!");
						return true;
					}
				}
			}
			
		} else if (args[0].equalsIgnoreCase("create")) {

			if (args.length > 1) {
				if (regions.contains(args[1])) {
					player.sendMessage(ChatColor.RED + "Name already in use!");
					return true;
				}
				prRegion pr = createNewRegion(player);
				pr.setName(args[1]);
				pr.newBlocks();
				selected.put(player, pr.getName());
				regions.add(pr.getName());
				save(pr);
			} else {
				player.sendMessage(ChatColor.AQUA + "Please provide a name.");
			}
			
		} else if (args[0].equalsIgnoreCase("delete")) {
			prRegion pr = new prRegion();
			pr.delete(selected.get(player), player.getWorld());
			regions.remove(selected.get(player));
			selected.remove(player);
			player.sendMessage(ChatColor.DARK_RED + "Region deleted.");
			
		} else if (args[0].equalsIgnoreCase("replace")) {
			prRegion pr = new prRegion();
			if (!selected.containsKey(player)) {
				player.sendMessage(ChatColor.RED + "You did not select a region.");
				return true;
			}
			pr.place(selected.get(player), player.getWorld());
			player.sendMessage(ChatColor.GREEN + "Done");

		} else if (args[0].equalsIgnoreCase("list")) {
			String regs = ChatColor.GREEN + "";
			for  (String region : regions) {
				regs += region + "|";
			}
			player.sendMessage(regs);
		}

		return true;
	}

	public void load() {
		for (World w : Bukkit.getWorlds()) {
			File f = new File(plugin.getDataFolder() , "saves/" + w.getName() + "/");
			for (File file : f.listFiles()) {
				regions.add(file.getName().replace(".fr", ""));
			}
		}
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


	private prRegion createNewRegion(Player player) {
		int x1,y1,z1;
		try {
			x1 = player.getMetadata("x1").get(0).asInt();
			y1 = player.getMetadata("y1").get(0).asInt();
			z1 = player.getMetadata("z1").get(0).asInt();
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Select point 1 first.");
			return null;
		}

		player.removeMetadata("x1", plugin);
		player.removeMetadata("y1", plugin);
		player.removeMetadata("z1", plugin);
		int x2,y2,z2;
		try{
			x2 = player.getMetadata("x2").get(0).asInt();
			y2 = player.getMetadata("y2").get(0).asInt();
			z2 = player.getMetadata("z2").get(0).asInt();

		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Select point 2 first.");
			return null;
		}

		player.removeMetadata("x2", plugin);
		player.removeMetadata("y2", plugin);
		player.removeMetadata("z2", plugin);
		player.sendMessage(ChatColor.GREEN + "Region created!");
		return new prRegion(player.getWorld(),x1,y1,z1,x2,y2,z2);
	}

	private void showHelp(Player player) {
		player.sendMessage(ChatColor.GOLD + "Select " 		+ ChatColor.AQUA	+ "<Name> : " + ChatColor.GREEN + "Select region.");
		player.sendMessage(ChatColor.GOLD + "Create " 		+ ChatColor.AQUA	+ "<Name> : " + ChatColor.GREEN + "Create region.");
		player.sendMessage(ChatColor.GOLD + "Delete : " 	+ ChatColor.GREEN	+ "Delete selected region.");
		player.sendMessage(ChatColor.GOLD + "Replace : "	+ ChatColor.GREEN	+ "Replace selected region.");
		player.sendMessage(ChatColor.GOLD + "List : " 		+ ChatColor.GREEN	+ "Show a list of available regions.");
		

	}

}
