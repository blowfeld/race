package thomasb.web.clocking;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.Lists;

//TODO Rewrite this unit test, as the timings are not really deterministic
public class ClockedSubmissionThreadTest {
	private static final ClockedRequestProcessor<?> DUMMY_PROCESSOR = createProcessorMock();

	private ClockedSubmissionThread<?> submissionThread;
	
	private TestAsyncContext request_0_0;
	private TestAsyncContext request_0_1;
	private TestAsyncContext request_1_0;
	private TestAsyncContext request_1_1;
	private TestAsyncContext request_2_0;
	private TestAsyncContext request_2_1;
	
	// The first execution of the thread is much slower than consecutive ones.
	// Thus somee tests fail if executed individually.
	@BeforeClass
	public static void initThread() throws InterruptedException, IOException, ServletException {
		ClockedSubmissionThread<?> submissionThread = new ClockedSubmissionThread<>(2, 50, DUMMY_PROCESSOR);
		submissionThread.start();
		submissionThread.launch();
		submissionThread.addRequest(new TestAsyncContext(0));
		
		sleep(10);
		
		submissionThread.finish();
		submissionThread.join();
	}
	
	private void setupRequestsWithSchedule(int delay) throws IOException {
		submissionThread = new ClockedSubmissionThread<>(2, 50, DUMMY_PROCESSOR);
		request_2_0 = new TestAsyncContext(2);
		request_2_1 = new TestAsyncContext(2);
		request_1_0 = new TestAsyncContext(submissionThread, 1, delay, request_2_0);
		request_1_1 = new TestAsyncContext(submissionThread, 1, delay, request_2_1);
		request_0_0 = new TestAsyncContext(submissionThread, 0, delay, request_1_0);
		request_0_1 = new TestAsyncContext(submissionThread, 0, delay, request_1_1);
	}
	
	@Test
	public void intervalLengthWithinBounds()
			throws InterruptedException, IOException, ServletException {
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
	public void clockIsIncreasedBeforeSubmission()
			throws InterruptedException, IOException, ServletException {
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
	public void requestsProcessedBeforeSubmission()
			throws InterruptedException, IOException, ServletException {
		setupRequestsWithSchedule(0);
		
		submissionThread.start();
		
		submissionThread.launch();
		
		sleep(25);
		submissionThread.addRequest(request_0_0);
		submissionThread.addRequest(request_0_1);
		sleep(50);
		
		submissionThread.finish();
		submissionThread.join();
		
		ArgumentCaptor<String> responseCaptor_0 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> responseCaptor_1 = ArgumentCaptor.forClass(String.class);
		verify(request_0_0.getResponse().getWriter(), times(1)).write(responseCaptor_0.capture());
		verify(request_0_0.getResponse().getWriter(), times(1)).write(responseCaptor_1.capture());
		
		assertEquals("2", responseCaptor_0.getValue());
		assertEquals("2", responseCaptor_1.getValue());
	}
	
	@Test
	public void intervalFinishesWhenAllParticipantsReached()
			throws InterruptedException, IOException, ServletException {
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
	public void pastRequestsIgnored()
			throws InterruptedException, IOException, ServletException {
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
		
		HttpServletResponse response = (HttpServletResponse)request_0_1.getResponse();
		ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
		verify(response, atLeastOnce()).setStatus(statusCaptor.capture());

		assertEquals(Integer.valueOf(408), statusCaptor.getValue());
		
		assertEquals(0, firstCount);
		assertEquals(1, secondCount);
	}
	
	private static ClockedRequestProcessor<?> createProcessorMock() {
		return new ClockedRequestProcessor<Void>() {
			@Override
			public ClockedRequest<Void> preprocess(AsyncContext request, int requestTime)
					throws IOException {
				return new ClockedRequest<Void>(request, null, requestTime);
			}
			
			@Override
			public ClockedRequest<Void> timeoutResponse(AsyncContext request, int requestTime) {
				HttpServletResponse response = (HttpServletResponse) request.getResponse();
				response.setStatus(408);
				
				return new ClockedRequest<>(request, null, requestTime);
			}
			
			@Override
			public List<AsyncContext> process(List<ClockedRequest<Void>> requests) {
				List<AsyncContext> result = Lists.newArrayList();
				for (ClockedRequest<Void> request : requests) {
					AsyncContext asyncContext = request.getContext();
					setCount(asyncContext, requests.size());
					result.add(asyncContext);
				}
				
				return result;
			}
			
			private void setCount(AsyncContext context, int count) {
				HttpServletResponse response = (HttpServletResponse) context.getResponse();
				try {
					response.getWriter().write(String.valueOf(count));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}
