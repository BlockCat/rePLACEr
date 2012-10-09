package me.blockcat.placereplacer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.blockcat.placereplacer.blocks.Blocks;
import me.blockcat.placereplacer.blocks.BlocksChest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class prRegion {

	private int x1;
	private int y1;
	private int z1;
	private int x2;
	private int y2;
	private int z2;
	private String name;

	List<Blocks> blocks = new ArrayList<Blocks>();
	private World world;

	public prRegion(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.world = world;
	}

	public prRegion() {

	}

	public void load(DataInputStream in, String name, World w) {
		this.name = name;
		try {
			world = w;
			System.out.println(world.getName());
			try {
				while (true) {
					int d = in.readInt();
					if (d == 54) {
						System.out.println("chest added.");
						blocks.add(new BlocksChest(in, world));
					} else {
						blocks.add(new Blocks(in, world));
					}
				}
			}
			catch (EOFException d) {
				System.out.println("done!");

			}			
		} catch(IOException e) {
			e.printStackTrace();
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
		if (x1 > x2) {
			x3 = x2;
			x4 = x1;
		} else {
			x3 = x1;
			x4 = x2;
		}
		if (y1 > y2) {
			y3 = y2;
			y4 = y1;
		} else {
			y3 = y1;
			y4 = y2;
		}
		if (z1 > z2) {
			z3 = z2;
			z4 = z1;
		} else {
			z3 = z1;
			z4 = z2;
		}

		for (int x = x3; x <= x4 ; x++) {
			for (int y = y3; y <= y4; y++) {
				for (int z = z3; z <= z4; z++) {

					Block block = world.getBlockAt(x, y, z);
					int id = block.getTypeId();
					if (id == 54) {
						blocks.add(new BlocksChest(world ,x,y,z,54, block.getData(), block.getState()));
						System.out.println("added chest");
					} else {
						blocks.add(new Blocks(world, x, y, z, id, block.getData()));
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

	public void place() {
		for (Blocks bl : blocks) {
			bl.set();
		}
	}

}
