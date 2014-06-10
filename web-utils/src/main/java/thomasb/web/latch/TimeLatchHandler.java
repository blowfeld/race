package thomasb.web.latch;

import static java.lang.Math.max;

import java.io.IOException;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonValue;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.clocking.ClockedExecutorThread;
import thomasb.web.dispatch.HandlerContext;
import thomasb.web.dispatch.JsonHandlerContext;
import thomasb.web.dispatch.RequestHandler;

public final class TimeLatchHandler implements RequestHandler {
	public static final String REMAINING_PARAMETER = "remaining";
	
	private static final Runnable VOID_ACTION = new Runnable() {
		@Override
		public void run() {
			//do nothing
		}
	};
	
	private static final int DEFAULT_RESOLUTION = 0;
	
	private final UUID id = UUID.randomUUID();
	
	private final ClockedExecutorThread clock;
	private final int resolution;

	private volatile int count;
	private volatile boolean isExpired = false;
	
	public TimeLatchHandler(int time) {
		this(time, DEFAULT_RESOLUTION);
	}
	
	public TimeLatchHandler(int time, int resolution) {
		this.resolution = resolution;
		this.count = time / resolution;
		this.clock = new ClockedExecutorThread(resolution, VOID_ACTION);
		clock.start();
	}
	
	public void launch() {
		clock.launch();
	}
	
	public void resetClock() {
		count += clock.getIntervalCount();
	}
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handle(new JsonHandlerContext(request, response));
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		if (!isExpired && clock.getIntervalCount() > count) {
			isExpired = true;
		}
		
		if (isExpired) {
			respondWithExpired(context);
		} else {
			respondWithRemainingTime(context);
		}
		
		context.writeResponse();
	}
	
	private void respondWithExpired(HandlerContext context) throws IOException {
		context.setResponseParameter(REMAINING_PARAMETER, jsonNumberOf(-1));
	}
	
	private void respondWithRemainingTime(HandlerContext context) {
		int remainingTime = (count - clock.getIntervalCount()) * resolution;
		context.setResponseParameter(REMAINING_PARAMETER, jsonNumberOf(max(0, remainingTime)));
	}
	
	private JsonValue jsonNumberOf(int n) {
		return Json.createArrayBuilder().add(n).build().get(0);
	}
	
	public boolean isExpired() {
		return isExpired;
	}
	
	@Override
	public UUID getId() {
		return id;
	}
}