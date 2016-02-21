import java.net.InetSocketAddress;

import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.LoadedPlayer;
import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.connection.ClientType;

public class Teast {
	public static void main(String[] args) {
		Client client = new Client(new InetSocketAddress("localhost", 1111), ClientType.SERVER, "01");
		client.connect("HelloWorld".getBytes());
		System.out.println(">> Connected");
		ClientWrapper wclient = new ClientWrapper(client);
		System.out.println(">> Create Player");
		LoadedPlayer player = new LoadedPlayer(wclient,"WolverinDEV") {};
		player.load();
		//testPremiumSystem(player);
		testServerSwitch(player);
		System.exit(-1);
	}
	
	public static void testStatsRequest(LoadedPlayer player){
		
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
