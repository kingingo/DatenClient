package dev.wolveringer.dataserver.gamestats;

import java.util.HashMap;

public class Statistic {
	private static HashMap<Class<?>, Integer> types = new HashMap<>();

	static {
		types.put(int.class, 0);
		types.put(double.class, 1);
		types.put(String.class, 2);
	}

	private StatsKey stat;
	private Object output;

	public Statistic(StatsKey stat, Object output) {
		this.stat = stat;
		this.output = output;
	}

	public int asInt() {
		return (int) output;
	}

	public double asDouble() {
		return (double) output;
	}

	public String asString() {
		return (String) output;
	}

	public int getTypeId() {
		return types.get(stat.getType());
	}

	public StatsKey getStatsKey() {
		return stat;
	}

	public Object getValue() {
		return output;
	}
}
