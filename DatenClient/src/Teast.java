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
		System.out.println("UUID: "+player.getUUID());
	}
}
