package dev.wolveringer.client.debug;

import java.util.function.Predicate;

import lombok.Getter;
import lombok.Setter;

public class Debugger {
	@Getter
	@Setter
	private static boolean enabled = true;
	@Getter
	@Setter
	private static Predicate<String> filter;

	public static void debug(String message) {
		if (enabled && (filter == null || filter.test(message)))
			System.out.println(message);
	}
}
