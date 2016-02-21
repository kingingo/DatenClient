import java.net.InetSocketAddress;

import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.LoadedPlayer;
import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.connection.ClientType;
import dev.wolveringer.dataclient.gamestats.Game;
import dev.wolveringer.dataclient.gamestats.Statistic;
import dev.wolveringer.dataclient.gamestats.StatsKey;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutStatsEdit;
import dev.wolveringer.dataclient.protocoll.packets.PacketInBanStats.BanEntity;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutStatsEdit.Action;

public class Teast {
	public static void main(String[] args) {
		Client client = new Client(new InetSocketAddress("localhost", 1111), ClientType.SERVER, "01");
		client.connect("HelloWorld".getBytes());
		System.out.println(">> Connected");
		ClientWrapper wclient = new ClientWrapper(client);
		System.out.println(">> Create Player");
		LoadedPlayer player = wclient.getPlayer("WolverinDEV");
		player.load();
		
		//testPremiumSystem(player);
		//testServerSwitch(player);
		
		/*
		testGetRequestStats(player);
		testStatsEdit(player);
		testGetRequestStats(player);
		*/
		//testBan(player);
		
		
		
		System.exit(-1);
	}
	
	public static void testBan(LoadedPlayer player){
		System.out.println("banned: "+ player.getBanStats(null).getSync());
		player.banPlayer(null, "System", null, null, 5, System.currentTimeMillis()+100*1000, "Testing!").getSync();
		System.out.println("banned: "+ player.getBanStats(null).getSync());
		player.banPlayer(null, "System", null, null, 5, System.currentTimeMillis()-10, "Testing!").getSync();
		System.out.println("banned: "+ player.getBanStats(null).getSync());
	}
	
	private static void testStatsEdit(LoadedPlayer player){
		player.setStats(new PacketOutStatsEdit.EditStats[]{new PacketOutStatsEdit.EditStats(Game.SheepWars, Action.REMOVE, StatsKey.KILLS, 10)}).getSync();
	}
	
	private static void testGetRequestStats(LoadedPlayer player){
		long start = System.currentTimeMillis();
		Statistic[] stats = player.getStats(Game.SheepWars).getSync();
		if(stats == null)
			System.out.println("No stats found");
		else
			for(Statistic s : stats){
				System.out.println("Stats: "+s.getStatsKey()+"="+s.getValue());
			}
		System.out.println("Needed time: "+(System.currentTimeMillis()-start));
	}
	
	private static void testServerSwitch(LoadedPlayer player){
		System.out.println("Currunt-Server: "+player.getServer().getSync());
		System.out.println("Switch to lobby");
		player.setServerSync("lobby");
		System.out.println("Currunt-Server: "+player.getServer().getSync());
		System.out.println("Switch to hub1");
		player.setServerSync("hub1");
		System.out.println("Currunt-Server: "+player.getServer().getSync());
	}
	
	private static void testPremiumSystem(LoadedPlayer player){
		long start = System.currentTimeMillis();
		System.out.println("UUID: "+player.getUUID());
		System.out.println("Password: "+player.getPasswordSync());
		boolean premium = player.isPremiumSync();
		System.out.println("Premium: "+premium);

		player.setPremiumSync(!premium);
		System.out.println("----- Setting Premium "+!premium+" ----");
		
		System.out.println("UUID: "+player.getUUID());
		System.out.println("Password: "+player.getPasswordSync());
		System.out.println("Premium: "+player.isPremiumSync());
		System.out.println("Needed time: "+(System.currentTimeMillis()-start));
	}
}
