package thomasb.web.clocking;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;

import net.jcip.annotations.GuardedBy;

final class ClockedSubmission<T> {
	@GuardedBy("submissionExecutor")
	private final RequestCollection<T> requests;
	private final ClockedRequestProcessor<T> requestProcessor;
	private final int submissionInterval;
	
	@GuardedBy("submissionExecutor")
	private volatile ClockedExecutorThread submissionExecutor;
	
	ClockedSubmission(Collection<String> participants,
			int submissionInterval,
			ClockedRequestProcessor<T> requestProcessor) {
		this.submissionInterval = submissionInterval;
		this.requestProcessor = requestProcessor;
		this.requests = new RequestCollection<>(requestProcessor, participants);
	}
	
	synchronized void init() {
		checkState(submissionExecutor == null, "clocked submission is already initialized");
		submissionExecutor = new ClockedExecutorThread(submissionInterval, new SubmitAction(requests));
		submissionExecutor.start();
	}
	
	synchronized void launch() {
		checkState(submissionExecutor != null, "Not initialized, call init first.");
		submissionExecutor.launch();
	}
	
	synchronized void finish() throws InterruptedException {
		checkState(submissionExecutor != null, "Not initialized, call init first.");
		submissionExecutor.finish();
		submissionExecutor.join();
	}
	
	void addRequest(AsyncContext context) throws IOException, ServletException {
		ClockedRequest<T> request = new ClockedRequest<>(context);
		checkArgument(submissionExecutor.getIntervalCount() >= request.getTime(), "Request with illegal timing was received: expected %s, was %s", submissionExecutor.getIntervalCount(), request.getTime());
		
		ClockedRequest<T> preprocessed = preprocess(request);
		
		synchronized(submissionExecutor) {
			scheduleRequest(preprocessed);
		}
	}
	
	private ClockedRequest<T> preprocess(ClockedRequest<T> request)
			throws IOException, ServletException {
		AsyncContext context = request.getContext();
		int requestTime = request.getTime();
		
		T preprocessData = requestProcessor.preprocess(context, requestTime);
		
		return request.withData(preprocessData);
	}
	
	private void scheduleRequest(ClockedRequest<T> request) throws ServletException, IOException {
		ClockInterval currentInterval = submissionExecutor.getCurrentInterval();
		System.err.println("T: " + submissionExecutor.getIntervalCount());
		boolean submit = requests.add(request, submissionExecutor.getIntervalCount());
		if (submit) {
			currentInterval.finish();
		}
	}
	
	int getIntervalCount() {
		return submissionExecutor.getIntervalCount();
	}
	
	private class SubmitAction implements Runnable {
		private RequestCollection<T> requests;
		
		SubmitAction(RequestCollection<T> requests) {
			this.requests = requests;
		}
		
		@Override
		public void run() {
			requests.submit();
		}
	}
}