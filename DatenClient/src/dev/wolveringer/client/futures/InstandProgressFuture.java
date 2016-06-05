package dev.wolveringer.client.futures;

import dev.wolveringer.client.Callback;
import dev.wolveringer.client.PacketHandleErrorException;
import dev.wolveringer.client.ProgressFuture;

public abstract class InstandProgressFuture<T> implements ProgressFuture<T> {
	@Override
	public boolean haveResponse() {
		return true;
	}

	@Override
	public T getSync() {
		return get();
	}

	@Override
	public T getSyncSave() throws PacketHandleErrorException {
		return get();
	}

	@Override
	public T getSync(int timeout) {
		return get();
	}

	@Override
	public T getSyncSave(int timeout) throws PacketHandleErrorException {
		return get();
	}

	@Override
	public void getAsync(Callback<T> call) {
		call.call(get(),null);
	}

	@Override
	public void getAsync(Callback<T> call, int timeout) {
		call.call(get(),null);
	}
	
	public abstract T get();

}
