package thomasb.web.latch;

import java.util.UUID;

import thomasb.web.handler.RequestHandler;

public interface TimeLatchHandler extends RequestHandler {
	void launch();
	
	void resetClock();
	
	boolean isExpired();
	
	UUID getId();
}