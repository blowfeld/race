package thomasb.web.latch;

import thomasb.web.handler.RequestHandler;

public interface TimeLatchHandler extends RequestHandler {
	static final String REMAINING_PARAMETER = "remaining";
	
	void launch();
	
	void resetClock();
	
	boolean isExpired();
	
}