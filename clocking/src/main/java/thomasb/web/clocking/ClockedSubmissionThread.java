package thomasb.web.clocking;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;

import net.jcip.annotations.GuardedBy;

class ClockedSubmissionThread<T> extends Thread {
	@GuardedBy("requests")
	private final RequestCollection<T> requests;
	private final ClockedRequestProcessor<T> requestProcessor;
	private final CountDownLatch startLatch;
	
	@GuardedBy("requests")
	private volatile ClockInterval clockInterval;
	private volatile boolean stop = false;
	
	ClockedSubmissionThread(int participants,
			int submissionInterval,
			ClockedRequestProcessor<T> requestProcessor) {
		this.requestProcessor = requestProcessor;
		this.startLatch = new CountDownLatch(1);
		this.clockInterval = new ClockInterval(-1, submissionInterval);
		this.requests = new RequestCollection<>(requestProcessor, participants);
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
		requests.submit();
	}

	void addRequest(AsyncContext context) throws IOException, ServletException {
		ClockedRequest<T> request = new ClockedRequest<>(context);
		checkArgument(clockInterval.getCount() >= request.getTime(), "Request with illegal timing was received: expected %s, was %s", clockInterval.getCount(), request.getTime());

		ClockedRequest<T> preprocessed = preprocess(request);
		
		synchronized(requests) {
			scheduleRequest(preprocessed);
		}
	}
	
	private ClockedRequest<T> preprocess(ClockedRequest<T> request)
			throws IOException, ServletException {
		AsyncContext context = request.getContext();
		int requestTime = request.getTime();
		
		T preprocessData = requestProcessor.preprocess(context, requestTime);
		
		return new ClockedRequest<T>(context, preprocessData, requestTime);
	}
	
	private void scheduleRequest(ClockedRequest<T> request)
			throws ServletException, IOException {
		boolean submit = requests.add(request, clockInterval.getCount());
		
		if (submit) {
			clockInterval.finish();
		}
	}
	
	int getIntervalCount() {
		return clockInterval.getCount();
	}
}