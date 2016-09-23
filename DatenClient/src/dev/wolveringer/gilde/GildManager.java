package dev.wolveringer.gilde;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import dev.wolveringer.arrays.CachedArrayList;
import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.LoadedPlayer;
import dev.wolveringer.client.ProgressFuture;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildActionResponse;
import dev.wolveringer.events.EventType;
import dev.wolveringer.gilde.GildeType;

public class GildManager {
	private ClientWrapper connection;
	private CachedArrayList<Gilde> gilden = new CachedArrayList<>(20, TimeUnit.MINUTES);
	private GildeUpdateListener listener = new GildeUpdateListener(this);
	
	public GildManager(ClientWrapper connection) {
		this.connection = connection;
		connection.getHandle().getEventManager().getEventManager(EventType.GILDE_PERMISSION_UPDATE).setEventEnabled(true);
		connection.getHandle().getEventManager().getEventManager(EventType.GILDE_PLAYER_UPDATE).setEventEnabled(true);
		connection.getHandle().getEventManager().getEventManager(EventType.GILDE_PROPERTIES_UPDATE).setEventEnabled(true);
		connection.getHandle().getEventManager().registerListener(listener, false);
	}
	
	public void clear(){
		gilden.clear();
	}
	
	private synchronized Gilde loadGilde(UUID gilde) {
		if(gilde == null)
			return null;
		for (Gilde g : gilden)
			if (g.getUuid().equals(gilde))
				return g;
		Gilde g = new Gilde(connection, gilde);
		g.load();
		gilden.add(g);
		return g;
	}

	public Gilde getLoadedGilde(UUID uuid) {
		if(uuid == null)
			return null;
		for (Gilde g : new ArrayList<>(gilden))
			if (g != null && g.getUuid() != null)
				if (g.getUuid().equals(uuid))
					return g;
		return null;
	}

	public Gilde getGilde(UUID uuid) {
		if(uuid == null)
			return null;
		for (Gilde g : new ArrayList<>(gilden))
			if (g != null && g.getUuid() != null)
				if (g.getUuid().equals(uuid))
					return g;
		return loadGilde(uuid);
	}

	public Gilde getGildeSync(String name) {
		for (Gilde g : gilden)
			if (g != null && g.getName() != null)
				if (g.getName() != null && g.getName().equalsIgnoreCase(name))
					return g;
		return getGilde(connection.getGildeFromName(name).getSync());
	}

	public Gilde getGildeSync(LoadedPlayer player, GildeType type) {
		for (Gilde g : gilden)
			if (g != null && g.getName() != null)
				for (GildSection s : g.getActiveSections())
					if (s.getType() == type && s.players.contains(new Integer(player.getPlayerId())))
						return g;
		return getGilde(connection.getGildePlayer(player, type).getSync());
	}

	public ProgressFuture<PacketGildActionResponse> deleteGilde(Gilde gilde,boolean DatenServersync) { //TODO update all players in the gilde (Tab etc.)
		gilden.remove(gilde);
		if(DatenServersync)
			return connection.deleteGilde(gilde.getUuid());
		return null;
	}
}
