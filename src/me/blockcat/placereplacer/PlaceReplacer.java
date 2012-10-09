package me.blockcat.placereplacer;

import org.bukkit.plugin.java.JavaPlugin;

public class PlaceReplacer extends JavaPlugin {

	private prCommand commands;
	public prListener listener;
	
	public void onEnable() {
		listener = new prListener(this);
		commands = new prCommand(this);
		this.getServer().getPluginManager().registerEvents(listener, this);
		this.getCommand("pro").setExecutor(commands);
		commands.load();
	}

	

}
