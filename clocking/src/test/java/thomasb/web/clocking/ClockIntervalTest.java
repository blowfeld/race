package thomasb.web.clocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class ClockIntervalTest {
	private ClockInterval interval;
	private int actualDuration;
	private Thread thread;

	@Before
	public void setupThread() {
		interval = new ClockInterval(0, 50);
		
		thread = new Thread() {
			@Override
			public void run() {
				long start = System.currentTimeMillis();
				interval.await();
				long duration = System.currentTimeMillis() - start;
				actualDuration = (int)duration;
			};
		};
		
	}
	
	@Test
	public void intervalLengthWithinBounds() throws InterruptedException {
		thread.start();

		thread.join();
		assertThat(55, Matchers.greaterThan(actualDuration));
		assertThat(45, Matchers.lessThan(actualDuration));
	}

	@Test
	public void terminationBeforeDurationWithinBounds() throws InterruptedException {
		thread.start();
		
		Thread.sleep(25);
		interval.finish();
		
		thread.join();
		assertThat(30, Matchers.greaterThan(actualDuration));
		assertThat(20, Matchers.lessThan(actualDuration));
	}
	
	@Test
	public void nextIntevalIncrementsCount() throws InterruptedException {
		ClockInterval nextInterval = interval.next();
		assertEquals(1, nextInterval.getCount());
	}
}
