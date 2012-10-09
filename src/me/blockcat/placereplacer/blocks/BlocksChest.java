package me.blockcat.placereplacer.blocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class BlocksChest extends Blocks{
	
	ItemStack[] content;

	public BlocksChest(World world, int x, int y, int z, int id, byte data) {
		super(world, x, y, z, id, data);
	}
	
	public BlocksChest(World world, int x, int y, int z, int id, byte data, BlockState state) {
		super(world,x, y, z, id, data, state);
		content = ((Chest) state).getBlockInventory().getContents();
	}
	public BlocksChest(DataInputStream in, World world) throws IOException {
		super(in, world);
		int index = in.read();
		System.out.println(index);
		for (int x = 0; x < index; x++) {
			int id = in.readInt();
			int a = in.readInt();
			byte data = in.readByte();
			System.out.println(index + " "+id + " " + a);
			content[x] = new ItemStack(id,a);
		}
	}
	
	public void save(DataOutputStream out) throws IOException {
		super.save(out);
		out.write(content.length);
		for (ItemStack c : content) {
			out.writeInt(c.getTypeId());
			out.writeInt(c.getAmount());
			out.writeByte(c.getData().getData());
		}
	}
	
	public void set() {
		Block b = world.getBlockAt(x, y, z);
		b.setTypeIdAndData(id, data, true);
		Chest chestNew = (Chest) b.getState();
		chestNew.getBlockInventory().setContents(content);
	
	}
}
