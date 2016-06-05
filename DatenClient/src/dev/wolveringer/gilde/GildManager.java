package dev.wolveringer.gilde;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import dev.wolveringer.arrays.CachedArrayList;
import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.LoadedPlayer;

public class GildManager {
	private ClientWrapper connection;
	private CachedArrayList<Gilde> gilden = new CachedArrayList<>(20, TimeUnit.MINUTES);
	
	public GildManager(ClientWrapper connection) {
		this.connection = connection;
	}
	
	private synchronized Gilde loadGilde(UUID gilde){
		for(Gilde g : gilden)
			if(g.getUuid().equals(gilde))
				return g;
		Gilde g = new Gilde(connection, gilde);
		g.load();
		gilden.add(g);
		return g;
	}
	
	public Gilde getGilde(UUID uuid){
		for(Gilde g : gilden)
			if(g.getUuid().equals(uuid))
				return g;
		return loadGilde(uuid);
	}
	
	public Gilde getGildeSync(String name){
		for(Gilde g : gilden)
			if(g.getName() != null && g.getName().equalsIgnoreCase(name))
				return g;
		return getGilde(connection.getGildeFromName(name).getSync());
	}
	
	public Gilde getGildeSync(LoadedPlayer player,GildeType type){
		for(Gilde g : gilden)
			for(GildSection s : g.getActiveSections())
				if(s.getType() == type && s.players.contains(new Integer(player.getPlayerId())))
					return g;
		return getGilde(connection.getGildePlayer(player, type).getSync());
	}
}
