package thomasb.web.clocking;

import java.util.concurrent.CountDownLatch;

public class ClockedExecutorThread extends Thread {
	private final Runnable action;
	private final CountDownLatch startLatch;

	private volatile boolean stop = false;
	private volatile ClockInterval clockInterval;
	
	ClockedExecutorThread(int interval, Runnable action) {
		this.action = action;
		this.startLatch = new CountDownLatch(1);
		this.clockInterval = new ClockInterval(-1, interval);
	}
	
	void launch() {
		startLatch.countDown();
	}
	
	void finish() {
		stop = true;
	}
	
	void step() {
		clockInterval.finish();
	}
	
	@Override
	public void run() {
		clockInterval.finish();
		
		awaitStart();
		
		while (!stop) {
			awaitClockInterval();
			synchronized (this) {
				clockInterval = clockInterval.next();
				action.run();
			}
		}
	}
	
	private void awaitStart() {
		try {
			startLatch.await();
		} catch (InterruptedException e) {
			finish();
			launch();
		}
	}
	
	private void awaitClockInterval() {
		try {
			clockInterval.await();
		} catch (InterruptedException e) {
			finish();
		}
	}
	
	int getIntervalCount() {
		return clockInterval.getCount();
	}
}
