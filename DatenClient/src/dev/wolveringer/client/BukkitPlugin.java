package dev.wolveringer.client;

import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_8_R1.PacketDataSerializer;

public class BukkitPlugin extends JavaPlugin{

	public void onEnable(){ 
		System.out.println("[DatenClient]: enabled!");
	}
	
	public void onDisable(){
		System.out.println("[DatenClient]: disabled!");
	}
}
