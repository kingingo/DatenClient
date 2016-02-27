package dev.wolveringer.client;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin{

	public void onEnable(){
		System.out.println("[DatenClient]: enabled!");
	}
	
	public void onDisable(){
		System.out.println("[DatenClient]: disabled!");
	}
	
}
