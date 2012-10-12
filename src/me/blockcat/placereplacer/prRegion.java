package me.blockcat.placereplacer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import me.blockcat.placereplacer.blocks.Blocks;
import me.blockcat.placereplacer.blocks.BlocksChest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class prRegion {

	private int x1;
	private int y1;
	private int z1;
	private int x2;
	private int y2;
	private int z2;

	private String name;
	private Vector v1;
	private Vector v2;

	List<Blocks> blocks = new ArrayList<Blocks>();
	private World world;

	public prRegion(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		v1 = new Vector(x1,y1,z1);
		v2 = new Vector(x2,y2,z2);
		this.world = world;

	}

	public prRegion() {

	}

	public void load(DataInputStream in, String name, World w) {
		this.name = name;
		try {
			world = w;
			try {
				while (true) {
					int d = in.readInt();
					if (d == Material.CHEST.getId()) {
						blocks.add(new BlocksChest(in, world));
					} else {
						blocks.add(new Blocks(in, world));
					}
				}
			} catch (EOFException d) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadFromFile(String file, World world) {
		File f = new File("plugins/placereplacer/saves/" +world.getName() + "/" +file + ".fr");
		try {
		GZIPInputStream gzipi = new GZIPInputStream(new FileInputStream(f));
		DataInputStream in = new DataInputStream(gzipi);
		
		this.load(in, f.getName().replace(".fr", ""), world);
		in.close();
		gzipi.close();
		} catch (IOException e) {
			
		}
	}

	public void save(DataOutputStream out) {
		try {
			for (Blocks b : blocks) {
				b.save(out);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void newBlocks() {
		int x3;
		int x4;
		int y3;
		int y4;
		int z3;
		int z4;
		Vector vMax = Vector.getMaximum(v1, v2);
		Vector vMin = Vector.getMinimum(v1, v2);
		
		x3 = vMin.getBlockX();
		x4 = vMax.getBlockX();
		
		
		y3 = vMin.getBlockY();
		y4 = vMax.getBlockY();
		
		z3 = vMin.getBlockZ();
		z4 = vMax.getBlockZ();
		

		for (int x = x3; x <= x4; x++) {
			
			for (int y = y3; y <= y4; y++) {
				
				for (int z = z3; z <= z4; z++) {
					Block block = world.getBlockAt(x, y, z);
					int id = block.getTypeId();
					if (id == Material.CHEST.getId()) {
						blocks.add(new BlocksChest(world, x, y, z, 54, block
								.getData(), block.getState()));
					} else {
						blocks.add(new Blocks(world, x, y, z, id, block
								.getData()));
					}
				}
			}
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getWorld() {
		return world.getName();
	}

	public void place(String string, World world) {
		this.loadFromFile(string, world);
		for (Blocks bl : blocks) {
			bl.set();
		}
	}

}
