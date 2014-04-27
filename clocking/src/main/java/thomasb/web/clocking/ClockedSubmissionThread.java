package thomasb.web.clocking;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.copyOf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.AsyncContext;

import net.jcip.annotations.GuardedBy;

import com.google.common.collect.ImmutableList;

class ClockedSubmissionThread extends Thread {
	@GuardedBy("requests")
	private final List<AsyncContext> requests = new ArrayList<>();
	private final int participants;
	private final int submissionInterval;
	private final CountDownLatch startLatch;

	@GuardedBy("requests")
	private volatile ClockInterval clockInterval; 
	private volatile boolean stop = false;

	ClockedSubmissionThread(int participants, int submissionInterval) {
		this.participants = participants;
		this.submissionInterval = submissionInterval;
		this.startLatch = new CountDownLatch(1);
	}
	
	@Override
	public void run() {
		clockInterval = new ClockInterval(-1, submissionInterval);
		clockInterval.finish();
		
		awaitStart();
		
		while (!stop) {
			clockInterval.await();
			ImmutableList<AsyncContext> submission;
			synchronized (requests) {
				clockInterval = clockInterval.next();
				submission = copyOf(requests);
				requests.clear();
			}
			
			submit(submission);
		}
	}

	private void awaitStart() {
		try {
			startLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	void launch() {
		startLatch.countDown();
	}
	
	void terminate() {
		stop = true;
	}
	
	boolean addRequest(AsyncContext request, int intervalCount) {
		synchronized (requests) {
			checkArgument(clockInterval.getCount() >= intervalCount, "Request with illegal timing was received: expected %s, was %s", clockInterval.getCount(), intervalCount);
			if (clockInterval.getCount() > intervalCount) {
				return false;
			}
			
			requests.add(request);

			if (requests.size() == this.participants) {
				clockInterval.finish();
			}
		}
		
		return true;
	}
	
	private void submit(List<AsyncContext> requests) {
		for (AsyncContext asyncRequest : requests) {
			asyncRequest.complete();
		}
	}
	
	int getIntervalCount() {
		return clockInterval.getCount();
	}
}