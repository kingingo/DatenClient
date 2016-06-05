package dev.wolveringer.client.futures;

import dev.wolveringer.client.Callback;
import dev.wolveringer.client.PacketHandleErrorException;
import dev.wolveringer.client.ProgressFuture;

public abstract class FutureResponseTransformer<IN,OUT> implements ProgressFuture<OUT>{
	private ProgressFuture<IN> future;

	public FutureResponseTransformer(ProgressFuture<IN> future) {
		this.future = future;
	}
	
	@Override
	public boolean haveResponse() {
		return future.haveResponse();
	}

	@Override
	public OUT getSync() {
		return transform(future.getSync());
	}

	@Override
	public OUT getSyncSave() throws PacketHandleErrorException {
		return transform(future.getSync());
	}

	@Override
	public OUT getSync(int timeout) {
		return transform(future.getSync(timeout));
	}

	@Override
	public OUT getSyncSave(int timeout) throws PacketHandleErrorException {
		return transform(future.getSyncSave(timeout));
	}

	@Override
	public void getAsync(Callback<OUT> call) {
		future.getAsync(new Callback<IN>() {
			@Override
			public void call(IN obj, Throwable e) {
				call.call(obj != null ? transform(obj) : null,e);
			}
			
		});
	}

	@Override
	public void getAsync(Callback<OUT> call, int timeout) {
		future.getAsync(new Callback<IN>() {
			@Override
			public void call(IN obj, Throwable e) {
				call.call(obj != null ? transform(obj) : null,e);
			}
			
		}, timeout);
	}
	
	public abstract OUT transform(IN obj);
}
