package dev.wolveringer.client.futures;

import dev.wolveringer.client.Callback;
import dev.wolveringer.client.PacketHandleErrorException;
import dev.wolveringer.client.ProgressFuture;
import dev.wolveringer.client.threadfactory.ThreadFactory;

public abstract class BaseProgressFuture<T> implements ProgressFuture<T>{
	private int timeout = 5000;
	private int sleep = 25;
	private T response = null;
	private PacketHandleErrorException exception;
	
	
	protected void done(T response) {
		this.response = response;
	}
	protected void done(PacketHandleErrorException e) {
		this.exception = e;
	}

	public boolean haveResponse() {
		return response != null;
	}

	public T getSync() {
		return getSyncSave();
	}

	public T getSyncSave() throws PacketHandleErrorException {
		return getSyncSave(timeout);
	}

	public T getSync(int timeout) {
		return getSyncSave(timeout);
	}

	public T getSyncSave(int timeout) throws PacketHandleErrorException {
		long start = System.currentTimeMillis();
		while (response == null) {
			if(exception != null)
				throw exception;
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (start + timeout < System.currentTimeMillis()) {
				throw new RuntimeException("Timeout");
				//return null;
			}
		}
		return response;
	}

	public void getAsync(Callback<T> call) {
		getAsync(call, timeout);
	}

	public void getAsync(Callback<T> call, int timeout) {
		ThreadFactory.getFactory().createThread(new Runnable() {
			@Override
			public void run() {
				T out = null;
				try {
					out = getSyncSave(timeout);
				} catch (Exception e) {
					
				}
				call.call(out);
			}
		}).start();
	}
}
