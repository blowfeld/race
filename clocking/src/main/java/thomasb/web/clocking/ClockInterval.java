package thomasb.web.clocking;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class ClockInterval {
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	private final int count;
	private final int duration;
	private final CountDownLatch latch;
	private final ScheduledFuture<?> termination;
	
	ClockInterval(int count, int duration) {
		this(count, duration, new CountDownLatch(1));
	}
	
	ClockInterval(int count, int duration, CountDownLatch latch) {
		checkArgument(count >= -1, "count must be non-negative");
		checkArgument(duration > 0, "duration must be positive");
		this.count = count;
		this.duration = duration;
		this.latch = latch;
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
		return new ClockInterval(count + 1, duration);
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
