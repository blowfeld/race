package thomasb.web.latch;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.max;

import java.io.IOException;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonValue;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.clocking.ClockedExecutorThread;
import thomasb.web.dispatch.JsonHandlerContext;
import thomasb.web.handler.HandlerContext;

public final class TimeLatchHandlerImp implements TimeLatchHandler {
	private static final Runnable VOID_ACTION = new Runnable() {
		@Override
		public void run() {
			//do nothing
		}
	};
	
	private final UUID id = UUID.randomUUID();
	
	private final ClockedExecutorThread clock;
	private final int resolution;
	private final int duration;

	private volatile int count;
	private volatile boolean isExpired = false;
	
	public TimeLatchHandlerImp(int time, int resolution) {
		checkArgument(resolution > 0, "Resolution must be larger than zero: %s", resolution);
		checkArgument(time > 0, "Time must be larger than zero: %s", time);
		
		this.resolution = resolution;
		this.duration = time / resolution;
		this.clock = new ClockedExecutorThread(resolution, VOID_ACTION);
		this.count = this.duration;
		clock.start();
	}
	
	public void launch() {
		clock.launch();
	}
	
	public void resetClock() {
		count = clock.getIntervalCount() + duration;
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