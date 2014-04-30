package thomasb.web.clocking;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jcip.annotations.GuardedBy;

class ClockedSubmissionThread extends Thread {
	private static final Dispatcher DISPATCHER = new Dispatcher();
	
	public static final String TIME_PARAMETER = "time_count";
	
	@GuardedBy("requests")
	private final List<AsyncContext> requests = new ArrayList<>();
	private final int participants;
	private final ClockedRequestProcessor requestProcessor;
	private final CountDownLatch startLatch;
	
	@GuardedBy("requests")
	private volatile ClockInterval clockInterval;
	private volatile boolean stop = false;
	
	ClockedSubmissionThread(int participants,
			int submissionInterval,
			ClockedRequestProcessor requestProcessor) {
		this.participants = participants;
		this.requestProcessor = requestProcessor;
		this.startLatch = new CountDownLatch(1);
		this.clockInterval = new ClockInterval(-1, submissionInterval);
	}
	
	void launch() {
		startLatch.countDown();
	}
	
	void finish() {
		stop = true;
	}
	
	@Override
	public void run() {
		clockInterval.finish();
		
		awaitStart();
		
		while (!stop) {
			awaitClockInterval();
			
			synchronized (requests) {
				submitInterval();
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
	
	private void submitInterval() {
		clockInterval = clockInterval.next();
		DISPATCHER.submit(requests);
		requests.clear();
	}
	
	void addRequest(AsyncContext request) throws IOException {
		int intervalCount = readTime(request);
		serviceRequest(request, intervalCount);
		
		synchronized (requests) {
			scheduleRequest(request, intervalCount);
		}
	}
	
	private int readTime(AsyncContext request) {
		HttpServletRequest httpRequest = (HttpServletRequest)request.getRequest();
		
		return Integer.valueOf(httpRequest.getParameter(TIME_PARAMETER));
	}
	
	private void serviceRequest(AsyncContext request, int intervalCount) throws IOException {
		if (clockInterval.getCount() > intervalCount) {
			return;
		}
		
		HttpServletRequest httpRequest = (HttpServletRequest)request.getRequest();
		HttpServletResponse httpResponse = (HttpServletResponse)request.getResponse();
		requestProcessor.service(intervalCount, httpRequest, httpResponse);
	}
	
	private void timeout(AsyncContext request) {
		HttpServletResponse httpResponse = (HttpServletResponse)request.getResponse();
		httpResponse.reset();
		requestProcessor.timeoutResponse(clockInterval.getCount(), httpResponse);
	}
	
	private void scheduleRequest(AsyncContext request, int intervalCount) {
		checkArgument(clockInterval.getCount() >= intervalCount, "Request with illegal timing was received: expected %s, was %s", clockInterval.getCount(), intervalCount);
		if (clockInterval.getCount() > intervalCount) {
			timeout(request);
			DISPATCHER.submit(request);
			
			return;
		}
		
		requests.add(request);
		
		if (requests.size() == this.participants) {
			// This is save as clients are only allowed to send a request for
			// the next time interval after they received the current submission
			clockInterval.finish();
		}
	}
	
	int getIntervalCount() {
		return clockInterval.getCount();
	}
}