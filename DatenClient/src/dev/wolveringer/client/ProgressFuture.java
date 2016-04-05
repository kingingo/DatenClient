package dev.wolveringer.client;

public interface ProgressFuture<T> {
	public boolean haveResponse();

	public T getSync();

	public T getSyncSave() throws PacketHandleErrorException;

	public T getSync(int timeout);

	public T getSyncSave(int timeout) throws PacketHandleErrorException;

	public void getAsync(Callback<T> call);

	public void getAsync(Callback<T> call, int timeout);
}
