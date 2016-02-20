package dev.wolveringer.client;

public abstract class ProgressFuture<T> {
	private int timeout = 5000;
	private int sleep = 50;
	private T response = null;

	protected void done(T response) {
		this.response = response;
	}

	public boolean haveResponse() {
		return haveResponse();
	}

	public T getSync() {
		return getSyncSave();
	}

	public T getSyncSave() throws RuntimeException {
		return getSyncSave(timeout);
	}

	public T getSync(int timeout) {
		return getSyncSave(timeout);
	}

	public T getSyncSave(int timeout) throws RuntimeException {
		long start = System.currentTimeMillis();
		while (response == null) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (start + timeout < System.currentTimeMillis()) {
				//throw new RuntimeException("Timeout");
				return null;
			}
		}
		return response;
	}

	public void getAsync(Callback<T> call) {
		getAsync(call, timeout);
	}

	public void getAsync(Callback<T> call, int timeout) {
		new Thread() {
			@Override
			public void run() {
				T out = null;
				try {
					out = getSyncSave(timeout);
				} catch (Exception e) {
					
				}
				call.call(out);
			}
		}.start();
	}
}
