package thomasb.web.clocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class ClockedSubmissionThreadTest {
	private ClockedSubmissionThread submissionThread;
	
	TestAsyncContext request_0_0;
	TestAsyncContext request_0_1;
	TestAsyncContext request_1_0;
	TestAsyncContext request_1_1;

	@Before
	public void setupRequests() {
		request_0_0 = new TestAsyncContext();
		request_0_1 = new TestAsyncContext();
		request_1_0 = new TestAsyncContext();
		request_1_1 = new TestAsyncContext();
	}
	
	@Before
	public void setupThread() {
		submissionThread = new ClockedSubmissionThread(2, 50);
	}
	
	@Test
	public void intervalLengthWithinBounds() throws InterruptedException {
		submissionThread.start();
		
		submissionThread.launch();
		long start = System.currentTimeMillis();
		
		Thread.sleep(25);
		submissionThread.addRequest(request_0_0, 0);
		int firstCount = submissionThread.getIntervalCount();
		Thread.sleep(50);
		int secondCount = submissionThread.getIntervalCount();
		submissionThread.addRequest(request_1_0, 1);
		Thread.sleep(50);
		int thirdCount = submissionThread.getIntervalCount();

		submissionThread.terminate();
		submissionThread.join();

		long submissionTime_0_0 = request_0_0.getInvocationTime() - start;	
		long submissionTime_1_0 = request_1_0.getInvocationTime() - start;
		int submissionDiff = (int)(submissionTime_1_0 - submissionTime_0_0);
		
		assertThat(50, Matchers.lessThan((int)submissionTime_0_0));
		assertThat(100, Matchers.lessThan((int)submissionTime_1_0));
		
		assertThat(45, Matchers.lessThan(submissionDiff));
		assertThat(55, Matchers.greaterThan(submissionDiff));
		
		assertEquals(0, firstCount);
		assertEquals(1, secondCount);
		assertEquals(2, thirdCount);
	}
	
	@Test
	public void partialIntervalLengthWithinBounds() throws InterruptedException {
		submissionThread.start();
		
		long start = System.currentTimeMillis();
		submissionThread.launch();
		
		Thread.sleep(25);
		submissionThread.addRequest(request_0_0, 0);
		submissionThread.addRequest(request_0_1, 0);
		int firstCount = submissionThread.getIntervalCount();
		Thread.sleep(25);
		submissionThread.addRequest(request_1_0, 1);
		submissionThread.addRequest(request_1_1, 1);
		int secondCount = submissionThread.getIntervalCount();
		Thread.sleep(50);
		int thirdCount = submissionThread.getIntervalCount();
		
		submissionThread.terminate();
		submissionThread.join();
		
		long submissionTime_0_0 = request_0_0.getInvocationTime() - start;	
		long submissionTime_1_0 = request_1_0.getInvocationTime() - start;
		int submissionDiff = (int)(submissionTime_1_0 - submissionTime_0_0);
		
		assertThat(50, Matchers.greaterThan((int)submissionTime_0_0));
		assertThat(100, Matchers.greaterThan((int)submissionTime_0_0));
		
		assertThat(30, Matchers.greaterThan(submissionDiff));
		
		assertEquals(0, firstCount);
		assertEquals(1, secondCount);
		assertEquals(2, thirdCount);
	}
}
