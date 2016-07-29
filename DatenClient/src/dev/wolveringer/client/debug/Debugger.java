package dev.wolveringer.client.debug;

import lombok.Getter;
import lombok.Setter;

public class Debugger {
	@Getter
	@Setter
	private static boolean enabled = true;

	public static void debug(String message) {
		if (enabled)
			System.out.println(message);
	}
}
