package thomasb.web.clocking;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;

import net.jcip.annotations.GuardedBy;

class ClockedSubmissionThread extends Thread {
	private static final ExecutorService DISPATCHER = Executors
			.newCachedThreadPool();

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
			synchronized (requests) {
				clockInterval = clockInterval.next();
				submit(requests);
				requests.clear();
			}
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

	void finish() {
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
			DISPATCHER.execute(createSubmission(asyncRequest));
		}
	}
	
	private static Runnable createSubmission(final AsyncContext request) {
		return new Runnable() {
			@Override
			public void run() {
				request.complete();
			}
		};
	}

	int getIntervalCount() {
		return clockInterval.getCount();
	}
}