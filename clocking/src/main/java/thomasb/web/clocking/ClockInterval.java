package thomasb.web.clocking;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class ClockInterval {
	private final int count;
	private final int duration;
	private final CountDownLatch latch;
	private final ScheduledFuture<?> termination;
	private final ScheduledExecutorService executor;
	
	ClockInterval(int count, int duration) {
		this(count, duration, Executors.newSingleThreadScheduledExecutor());
	}
	
	private ClockInterval(int count, int duration, ScheduledExecutorService executor) {
		this(count, duration, new CountDownLatch(1), executor);
	}
	
	private ClockInterval(int count, int duration, CountDownLatch latch, ScheduledExecutorService executor) {
		checkArgument(duration > 0, "duration must be positive");
		this.count = count;
		this.duration = duration;
		this.latch = latch;
		this.executor = executor;
		this.termination = executor.schedule(new TerminationTask(latch), duration, TimeUnit.MILLISECONDS);
	}

	void await() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	void finish() {
		termination.cancel(false);
		latch.countDown();
	}
	
	ClockInterval next() {
		return new ClockInterval(count + 1, duration, executor);
	}
	
	int getCount() {
		return count;
	}
	
	private static class TerminationTask implements Callable<Void> {
		private final CountDownLatch latch;

		TerminationTask(CountDownLatch latch) {
			this.latch = latch;
		}
		
		@Override
		public Void call() throws Exception {
			latch.countDown();
			
			return null;
		}
	}
}
