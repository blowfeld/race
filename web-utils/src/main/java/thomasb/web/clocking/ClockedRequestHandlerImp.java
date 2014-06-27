package thomasb.web.clocking;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.handler.HandlerContext;

/**
 * A {@code ClockedRequestHandler} synchronizes between request of its participants.
 * 
 * The participants are supposed to send requests in fixed time intervals. For
 * each time interval the responses to the participants are delayed until the
 * request of the last participant arrives or a timeout limit is reached.
 * 
 * Participants must specify the count of the intended time interval in their
 * request and must not send a request to the succeeding interval before they
 * received the response from the server or a given timeout is reached.
 */
public final class ClockedRequestHandlerImp implements ClockedRequestHandler {
	public static final String TIME_PARAMETER = ClockedRequestImp.TIME_PARAMETER;
	
	private final UUID id = UUID.randomUUID();
	
	private final ClockedSubmission<?> clockedSubmission;
	private final CountDownLatch startLatch;
	
	private final ClockedRequestProcessor<?> requestProcessor;
	private final int interval;
	private final int timeout;
	
	private volatile boolean init = true;

	
	public ClockedRequestHandlerImp(Collection<String> participants,
			int interval,
			int timeout,
			ClockedRequestProcessor<?> requestProcessor) {
		this.interval = interval;
		this.timeout = timeout;
		this.requestProcessor = requestProcessor;
		
		this.clockedSubmission = new ClockedSubmission<>(participants, interval + timeout, requestProcessor);
		this.startLatch = new CountDownLatch(participants.size());
		this.clockedSubmission.init();
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException,
	IOException {
		handle(context.getRequest(), context.getResponse());
	}
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (init) {
			try {
				awaitParticipants();
			} catch (InterruptedException e) {
				return;
			}
			init = false;
			clockedSubmission.launch();

			writeInitialData(request, response);
			
			return;
		}
		
		AsyncContext async = request.startAsync(request, response);
		clockedSubmission.addRequest(async);
	}
	
	private void awaitParticipants() throws InterruptedException {
		startLatch.countDown();
		startLatch.await();
	}
	
	private void writeInitialData(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JsonGenerator responseGenerator = Json.createGenerator(response.getWriter());
		responseGenerator.writeStartObject()
				.write("interval", interval)
				.write("timeout", timeout)
				.write("data", requestProcessor.initalData(request))
			.writeEnd()
		.close();
	}
	
	@Override
	public UUID getId() {
		return id;
	}
}