import java.net.InetSocketAddress;
import java.util.UUID;

import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.LoadedPlayer;
import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.connection.ServerInformations;
import dev.wolveringer.client.external.BungeeCordActionListener;
import dev.wolveringer.client.threadfactory.ThreadFactory;
import dev.wolveringer.dataserver.gamestats.GameState;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import dev.wolveringer.dataserver.player.Setting;
import dev.wolveringer.dataserver.protocoll.DataBuffer;
import dev.wolveringer.dataserver.protocoll.packets.PacketInServerStatus;
import dev.wolveringer.dataserver.protocoll.packets.PacketInStatsEdit;
import dev.wolveringer.dataserver.protocoll.packets.PacketInStatsEdit.Action;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutServerStatus;
import dev.wolveringer.gamestats.Statistic;

public class Teast {
	public static void main(String[] args) {
		String name = "a04";
		ThreadFactory.setFactory(new ThreadFactory());
		for(int i = 0;i<5;i++){
			new TClient("acarde_"+i,GameType.BedWars).start();;
		}
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	private static void createClient(Game game,String name){
		
		Client client = Client.createServerClient(ClientType.ACARDE,name, new InetSocketAddress("localhost", 1111), new ServerActionListener() {
			@Override
			public void sendMessage(UUID player, String message) {
				System.out.println("["+name+"] Sendmessage: "+player +" Message: "+message);
			}
			
			@Override
			public void kickPlayer(UUID player, String message) {
				System.out.println("["+name+"] Kickplayer: "+player+" Message: "+message);
			}
			
			@Override
			public void brotcast(String permission, String message) {
				System.out.println("["+name+"] Brotcast: "+message+" Permission: "+permission);
			}
			
			@Override
			public void setGamemode(Game game) {
				System.out.println("Set gamemode");
				System.out.println("Reconnecting");
				//client.disconnect("Gamemode change");
				//createClient(game, name);
			}
			@Override
			public void disconnected() {
				System.out.println("Disconnected ["+name+"]");
				System.out.println("Stopping ["+name+"]");
				//System.exit(-1);
			}
			@Override
			public void connected() {}
		},new ServerInformations() {
			
			@Override
			public boolean isIngame() {
				return false;
			}
			
			@Override
			public Game getType() {
				return game;
			}
			
			@Override
			public int getPlayers() {
				return 0;
			}
			
			@Override
			public int getMaxPlayers() {
				return 1;
			}
			
			@Override
			public String getMOTS() {
				return "Client["+name+"]";
			}
		});
		Runnable disconnect = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		};
		client.connect("HelloWorld".getBytes());
		System.out.println(">> Connected");
		ClientWrapper wclient = new ClientWrapper(client);
		System.out.println(">> Create Player");
		/*
		LoadedPlayer player = wclient.getPlayer(UUID.fromString("57091d6f-839f-48b7-a4b1-4474222d4ad1"));
		if(player != null)
			player.load();
		else
			System.out.println("X");
		System.out.println("Player: "+player.getName()+"/"+player.getUUID());
		*/
		//LoadedPlayer player = wclient.getPlayer("WolverinDEV");
		//player.load();
		//player.setServerSync("lobby0");
		//wclient.sendMessage(player.getUUID(), "Hello world");
		//testPremiumSystem(player);
		//testServerSwitch(player);
		
		//testGetRequestStats(player);
		/*
		testStatsEdit(player);
		testGetRequestStats(player);
		*/
		//testBan(player);
		
		//TEST GEMS/COINS
		/*
		try{
			System.out.println("Gems: "+player.getGemsSync());
			player.changeGems(Action.ADD, 100).getSync();
			System.out.println("Gems: "+player.getGemsSync());
			
			System.out.println("Coins: "+player.getCoinsSync());
			player.changeCoins(Action.ADD, 10).getSync();
			System.out.println("Coins: "+player.getCoinsSync());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//player.unload();
		
		//System.exit(-1);
	}
*/
	
	public static void testBan(LoadedPlayer player){
		System.out.println("banned: "+ player.getBanStats(null).getSync());
		player.banPlayer(null, "System", null, null, 5, System.currentTimeMillis()+100*1000, "Testing!").getSync();
		System.out.println("banned: "+ player.getBanStats(null).getSync());
		player.banPlayer(null, "System", null, null, 5, System.currentTimeMillis()-10, "Testing!").getSync();
		System.out.println("banned: "+ player.getBanStats(null).getSync());
	}
	
	private static void testStatsEdit(LoadedPlayer player){
		player.setStats(new PacketInStatsEdit.EditStats[]{new PacketInStatsEdit.EditStats(GameType.SheepWars, Action.REMOVE, StatsKey.KILLS, 10)}).getSync();
	}
	
	private static void testGetRequestStats(LoadedPlayer player){
		long start = System.currentTimeMillis();
		Statistic[] stats = player.getStats(GameType.SheepWars).getSync();
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
class TClient {
	Client client;
	String name;
	public TClient(String name,GameType game) {
		this.name = name;
		client = Client.createBungeecordClient(name, new InetSocketAddress("localhost", 1111), new BungeeCordActionListener() {
			@Override
			public void sendMessage(UUID player, String message) {
				System.out.println("["+name+"] Sendmessage: "+player +" Message: "+message);
			}
			
			@Override
			public void kickPlayer(UUID player, String message) {
				System.out.println("["+name+"] Kickplayer: "+player+" Message: "+message);
			}
			
			@Override
			public void brotcast(String permission, String message) {
				System.out.println("["+name+"] Brotcast: "+message+" Permission: "+permission);
			}
			
			public void setGamemode(GameType game) {
				System.out.println("Set gamemode");
				System.out.println("Reconnecting");
				client.disconnect("Gamemode change");
				new TClient(name, game).start();
			}
			@Override
			public void disconnected() {
				System.out.println("Disconnected ["+name+"]");
				System.out.println("Stopping ["+name+"]");
				//System.exit(-1);
			}
			@Override
			public void connected() {}
			
			@Override
			public void serverMessage(String channel, DataBuffer buffer) {
				System.out.println("Message in Channel: "+channel);
				if(channel.equalsIgnoreCase("log"))
					System.out.println("message: "+buffer.readString());
			}

			@Override
			public void settingUpdate(UUID player, Setting setting, String value) {
				
			}

			@Override
			public void sendPlayer(UUID player, String server) {
				
			}
			
		},new ServerInformations() {
			@Override
			public PacketInServerStatus getStatus() {
				return new PacketInServerStatus(0x00, -1, -2, "", GameType.NONE,GameState.NONE,"NONE",false,"bungee000");
			}
		});
	}
	
	public void start(){
		try {
			client.connect("HelloWorld".getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(">> Connected");
		DataBuffer buffer = new DataBuffer();
		buffer.writeString("Hello world from "+name);
		new ClientWrapper(client).brotcastMessage(null, "Hello world");
	}
	public static void testBan(LoadedPlayer player){
		System.out.println("banned: "+ player.getBanStats(null).getSync());
		player.banPlayer(null, "System", null, null, 5, System.currentTimeMillis()+100*1000, "Testing!").getSync();
		System.out.println("banned: "+ player.getBanStats(null).getSync());
		player.banPlayer(null, "System", null, null, 5, System.currentTimeMillis()-10, "Testing!").getSync();
		System.out.println("banned: "+ player.getBanStats(null).getSync());
	}
	
	private static void testStatsEdit(LoadedPlayer player){
		player.setStats(new PacketInStatsEdit.EditStats[]{new PacketInStatsEdit.EditStats(GameType.SheepWars, Action.REMOVE, StatsKey.KILLS, 10)}).getSync();
	}
	
	private static void testGetRequestStats(LoadedPlayer player){
		long start = System.currentTimeMillis();
		Statistic[] stats = player.getStats(GameType.SheepWars).getSync();
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
/*
- Acarde Server verwatlten
- ChatAPI [-]
- Server Action [Kicken,Senden] [+]
- Coins und gems in stats implimentieren.


Later:

*/