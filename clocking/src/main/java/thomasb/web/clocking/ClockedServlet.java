package thomasb.web.clocking;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A {@code ClockedServlet} synchronizes between request of its participants.
 * 
 * The participants are supposed to send requests in fixed time intervals. For
 * each time interval the responses to the participants are delayed until the
 * request of the last participant arrives or a timeout limit is reached.
 * 
 * Participants must specify the count of the intended time interval in their
 * request and must not send a request to the succeeding interval before they
 * received the response from the server or a given timeout is reached.
 */
@SuppressWarnings("serial")
public class ClockedServlet extends HttpServlet {
	private final ClockedSubmissionThread submissionThread;
	private final CountDownLatch startLatch;
	private boolean init = true;
	
	public ClockedServlet(int participants,
			ClockedRequestProcessor requestProcessor,
			int interval) {
		this.submissionThread = new ClockedSubmissionThread(participants, interval, requestProcessor);
		this.submissionThread.start();
		this.startLatch = new CountDownLatch(participants);
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (init) {
			try {
				awaitParticipants();
			} catch (InterruptedException e) {
				return;
			} finally {
				Thread.currentThread().interrupt();
			}
			init = false;
		}
		
		AsyncContext async = request.startAsync(request, response);
		submissionThread.addRequest(async);
	}

	private void awaitParticipants() throws InterruptedException {
		startLatch.countDown();
		startLatch.await();
		submissionThread.launch();
	}
}