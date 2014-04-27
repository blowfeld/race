package thomasb.web.clocking;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public class ClockedServlet extends HttpServlet {
	private final ClockedSubmissionThread submissionThread;
	private final ClockedRequestProcessor requestProcessor;
	
	public ClockedServlet(int participants,
			ClockedRequestProcessor requestProcessor,
			int interval) {
		this.requestProcessor = requestProcessor;
		this.submissionThread = new ClockedSubmissionThread(participants, interval);
		this.submissionThread.start();
	}
	
	@Override
	public void service(ServletRequest request,
			ServletResponse response) throws ServletException, IOException {
		int requestTime = (int)request.getAttribute("time");
		if (submissionThread.getIntervalCount() > requestTime) {
			return;
		}
		
		requestProcessor.service(request, response);

		AsyncContext async = request.startAsync(request, response);
		submissionThread.addRequest(async, requestTime);
	}
}
