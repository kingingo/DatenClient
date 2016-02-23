package dev.wolveringer.client.threadfactory;

public class ThreadFactory {
	private static ThreadFactory factory;
	
	public static void setFactory(ThreadFactory factory) {
		ThreadFactory.factory = factory;
	}
	public static ThreadFactory getFactory() {
		return factory;
	}
	
	public ThreadRunner createThread(Runnable run){
		return new ThreadRunner() {
			Thread t = new Thread(run);
			@Override
			public void start() {
				t.start();
			}
			public void stop() {
				t.interrupt();
			};
		};
	}
}
