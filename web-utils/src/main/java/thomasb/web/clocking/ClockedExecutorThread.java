package thomasb.web.clocking;

import java.util.concurrent.CountDownLatch;
import thomasb.web.clocking.ClockInterval;

public final class ClockedExecutorThread extends Thread implements ClockedExecutor {
	private final Runnable action;
	private final CountDownLatch startLatch;

	private volatile boolean stop = false;
	private volatile ClockInterval clockInterval;
	
	public ClockedExecutorThread(int interval, Runnable action) {
		this.action = action;
		this.startLatch = new CountDownLatch(1);
		this.clockInterval = new ClockInterval(-1, interval);
	}
	
	public void launch() {
		startLatch.countDown();
	}
	
	public void finish() {
		stop = true;
	}
	
	public ClockInterval getCurrentInterval() {
		return clockInterval;
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
	
	public int getIntervalCount() {
		return clockInterval.getCount();
	}
}
