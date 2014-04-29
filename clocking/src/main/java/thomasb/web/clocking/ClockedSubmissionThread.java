package thomasb.web.clocking;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jcip.annotations.GuardedBy;

class ClockedSubmissionThread extends Thread {
	private static final Dispatcher DISPATCHER = new Dispatcher();

	@GuardedBy("requests")
	private final List<AsyncContext> requests = new ArrayList<>();
	private final int participants;
	private final int submissionInterval;
	private final ClockedRequestProcessor requestProcessor;
	private final CountDownLatch startLatch;

	@GuardedBy("requests")
	private volatile ClockInterval clockInterval;
	private volatile boolean stop = false;


	ClockedSubmissionThread(int participants,
			int submissionInterval,
			ClockedRequestProcessor requestProcessor) {
		this.participants = participants;
		this.submissionInterval = submissionInterval;
		this.requestProcessor = requestProcessor;
		this.startLatch = new CountDownLatch(1);
	}
	
	void launch() {
		startLatch.countDown();
	}
	
	private void awaitStart() {
		try {
			startLatch.await();
		} catch (InterruptedException e) {
			finish();
			launch();
		}
	}
	
	void finish() {
		stop = true;
	}

	@Override
	public void run() {
		clockInterval = new ClockInterval(-1, submissionInterval);
		clockInterval.finish();

		awaitStart();

		while (!stop) {
			try {
				clockInterval.await();
			} catch (InterruptedException e) {
				finish();
			}
			
			synchronized (requests) {
				submitInterval();
			}
		}
	}
	
	private void submitInterval() {
		clockInterval = clockInterval.next();
		DISPATCHER.submit(requests);
		requests.clear();
	}

	void addRequest(AsyncContext request) {
		int intervalCount = readTime(request);
		serviceRequest(request, intervalCount);
		
		synchronized (requests) {
			scheduleRequest(request, intervalCount);
		}
	}
	
	private int readTime(AsyncContext request) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void serviceRequest(AsyncContext request, int intervalCount) {
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
	
	private boolean scheduleRequest(AsyncContext request, int intervalCount) {
		checkArgument(clockInterval.getCount() >= intervalCount, "Request with illegal timing was received: expected %s, was %s", clockInterval.getCount(), intervalCount);
		if (clockInterval.getCount() > intervalCount) {
			timeout(request);
			DISPATCHER.submit(request);
			
			return false;
		}
		
		requests.add(request);
		
		if (requests.size() == this.participants) {
			// This is save as clients are only allowed to send a request for
			// the next time interval after they received the current submission
			clockInterval.finish();
		}
		
		return true;
	}

	int getIntervalCount() {
		return clockInterval.getCount();
	}
}