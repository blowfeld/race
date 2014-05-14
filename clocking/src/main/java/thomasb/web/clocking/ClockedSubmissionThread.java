package thomasb.web.clocking;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.jcip.annotations.GuardedBy;

class ClockedSubmissionThread<T> extends Thread {
	private static final Dispatcher DISPATCHER = new Dispatcher();
	
	public static final String TIME_PARAMETER = "time_count";
	
	@GuardedBy("requests")
	private final List<ClockedRequest<T>> requests = new ArrayList<>();
	private final int participants;
	private final ClockedRequestProcessor<T> requestProcessor;
	private final CountDownLatch startLatch;
	
	@GuardedBy("requests")
	private volatile ClockInterval clockInterval;
	private volatile boolean stop = false;
	
	ClockedSubmissionThread(int participants,
			int submissionInterval,
			ClockedRequestProcessor<T> requestProcessor) {
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
		DISPATCHER.submit(requestProcessor.process(requests));
		requests.clear();
	}
	
	void addRequest(AsyncContext request) throws IOException, ServletException {
		int intervalCount = readTime(request);
		ClockedRequest<T> preprocessed = preprocess(request, intervalCount);
		
		synchronized(requests) {
			scheduleRequest(preprocessed, intervalCount);
		}
	}
	
	private int readTime(AsyncContext request) {
		HttpServletRequest httpRequest = (HttpServletRequest)request.getRequest();
		
		return Integer.valueOf(httpRequest.getParameter(TIME_PARAMETER));
	}
	
	private ClockedRequest<T> preprocess(AsyncContext request, int intervalCount)
			throws IOException, ServletException {
		if (clockInterval.getCount() > intervalCount) {
			return timeout(request, intervalCount);
		}
		
		return requestProcessor.preprocess(request, intervalCount);
	}
	
	private void scheduleRequest(ClockedRequest<T> request, int intervalCount)
			throws ServletException, IOException {
		checkArgument(clockInterval.getCount() >= intervalCount, "Request with illegal timing was received: expected %s, was %s", clockInterval.getCount(), intervalCount);
		if (clockInterval.getCount() > intervalCount) {
			DISPATCHER.submit(timeout(request).getContext());
			
			return;
		}
		
		requests.add(request);
		
		if (requests.size() == this.participants) {
			// This is save as clients are only allowed to send a request for
			// the next time interval after they received the current submission
			clockInterval.finish();
		}
	}
	
	private ClockedRequest<T> timeout(AsyncContext request, int intervalCount)
			throws ServletException, IOException {
		return timeout(new ClockedRequest<T>(request, null, intervalCount));
	}
	
	private ClockedRequest<T> timeout(ClockedRequest<T> request)
			throws ServletException, IOException {
		return requestProcessor.timeoutResponse(request.getContext(), clockInterval.getCount());
	}
	
	int getIntervalCount() {
		return clockInterval.getCount();
	}
}