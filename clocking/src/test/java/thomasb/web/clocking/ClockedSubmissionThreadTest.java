package thomasb.web.clocking;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.Matchers;
import org.junit.Test;

public class ClockedSubmissionThreadTest {
	private static final ClockedRequestProcessor DUMMY_PROCESSOR = createProcessorMock();

	private ClockedSubmissionThread submissionThread;
	
	private TestAsyncContext request_0_0;
	private TestAsyncContext request_0_1;
	private TestAsyncContext request_1_0;
	private TestAsyncContext request_1_1;
	private TestAsyncContext request_2_0;
	private TestAsyncContext request_2_1;
	
	private void setupRequestsWithSchedule(int delay) {
		submissionThread = new ClockedSubmissionThread(2, 50, DUMMY_PROCESSOR);
		request_2_0 = new TestAsyncContext(2);
		request_2_1 = new TestAsyncContext(2);
		request_1_0 = new TestAsyncContext(submissionThread, 1, delay, request_2_0);
		request_1_1 = new TestAsyncContext(submissionThread, 1, delay, request_2_1);
		request_0_0 = new TestAsyncContext(submissionThread, 0, delay, request_1_0);
		request_0_1 = new TestAsyncContext(submissionThread, 0, delay, request_1_1);
	}
	
	@Test
	public void intervalLengthWithinBounds() throws InterruptedException {
		setupRequestsWithSchedule(0);
		
		submissionThread.start();
		
		submissionThread.launch();
		long start = System.currentTimeMillis();
		
		sleep(25);
		submissionThread.addRequest(request_0_0);

		int firstCount = submissionThread.getIntervalCount();
		sleep(50);
		int secondCount = submissionThread.getIntervalCount();
		sleep(50);
		int thirdCount = submissionThread.getIntervalCount();
		sleep(50);

		submissionThread.finish();
		submissionThread.join();

		long submissionTime_0_0 = request_0_0.getInvocationTime() - start;	
		long submissionTime_1_0 = request_1_0.getInvocationTime() - start;
		long submissionTime_2_0 = request_2_0.getInvocationTime() - start;
		int submissionDiff = (int)(submissionTime_1_0 - submissionTime_0_0);
		
		assertNotEquals(0, submissionTime_0_0);
		assertNotEquals(0, submissionTime_1_0);
		assertNotEquals(0, submissionTime_2_0);

		assertThat(50, Matchers.lessThanOrEqualTo((int)submissionTime_0_0));
		assertThat(100, Matchers.lessThanOrEqualTo((int)submissionTime_1_0));
		
		assertThat(45, Matchers.lessThan(submissionDiff));
		assertThat(55, Matchers.greaterThan(submissionDiff));
		
		assertEquals(0, firstCount);
		assertEquals(1, secondCount);
		assertEquals(2, thirdCount);
	}
	
	@Test
	public void clockIsIncreasedBeforeSubmission() throws InterruptedException {
		setupRequestsWithSchedule(0);

		submissionThread.start();
		
		submissionThread.launch();
		
		sleep(25);
		submissionThread.addRequest(request_0_0);
		submissionThread.addRequest(request_0_1);
		sleep(50);
		
		submissionThread.finish();
		submissionThread.join();
	}
	
	@Test
	public void intervalFinishesWhenAllParticipantsReached() throws InterruptedException {
		setupRequestsWithSchedule(25);

		submissionThread.start();
		
		long start = System.currentTimeMillis();
		submissionThread.launch();
		
		sleep(25);
		int firstCount = submissionThread.getIntervalCount();
		submissionThread.addRequest(request_0_0);
		submissionThread.addRequest(request_0_1);
		sleep(5);
		int secondCount = submissionThread.getIntervalCount();
		sleep(25);
		int thirdCount = submissionThread.getIntervalCount();
		
		submissionThread.finish();
		submissionThread.join();
		
		long submissionTime_0_0 = request_0_0.getInvocationTime() - start;	
		long submissionTime_1_0 = request_1_0.getInvocationTime() - start;
		int submissionDiff = (int)(submissionTime_1_0 - submissionTime_0_0);
		
		assertEquals(0, firstCount);
		assertEquals(1, secondCount);
		assertEquals(2, thirdCount);
		
		assertThat(50, Matchers.greaterThan((int)submissionTime_0_0));
		assertThat(100, Matchers.greaterThan((int)submissionTime_1_0));
		
		assertThat(30, Matchers.greaterThan(submissionDiff));
	}
	
	@Test
	public void pastRequestsIgnored() throws InterruptedException {
		setupRequestsWithSchedule(0);
		
		submissionThread.start();
		submissionThread.launch();
		
		sleep(25);
		submissionThread.addRequest(request_0_0);
		
		int firstCount = submissionThread.getIntervalCount();
		sleep(50);
		int secondCount = submissionThread.getIntervalCount();
		submissionThread.addRequest(request_0_1);
		sleep(50);
		
		submissionThread.finish();
		submissionThread.join();
		
		int responseStatus = ((HttpServletResponse)request_0_1.getResponse()).getStatus();
		assertEquals(408, responseStatus);
		
		assertEquals(0, firstCount);
		assertEquals(1, secondCount);
	}
	
	private static ClockedRequestProcessor createProcessorMock() {
		return new ClockedRequestProcessor() {
			@Override
			public void service(int requestTime, HttpServletRequest request,
					HttpServletResponse response) {
				// do nothing
			}
			
			@Override
			public void timeoutResponse(int requestTime,
					HttpServletResponse response) {
				response.setStatus(408);
			}
		};
	}
}
