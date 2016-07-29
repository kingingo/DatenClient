package dev.wolveringer.client;

import dev.wolveringer.thread.ThreadFactory;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {
	public void onEnable() {
		System.out.println("§aDatenclient classen wurden geladen.");
		ThreadFactory.class.getName();
	}

	public void onDisable() {
	}

}
