package dev.wolveringer.client;

public interface Callback<T> {
	public void call(T obj,Throwable exception);
}
