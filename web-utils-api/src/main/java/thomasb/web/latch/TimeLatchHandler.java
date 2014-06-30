package thomasb.web.latch;

import thomasb.web.handler.RequestHandler;

/**
 * A count down timer for HTTP requests.
 * 
 * The {@code TimeLatchHandler} responds to a request by sending a JSON object
 * containing the remaining time count in the 'remaining' property.
 * 
 * If the count down period is over, the sent remaining time is -1.
 */
public interface TimeLatchHandler extends RequestHandler {
	static final String REMAINING_PARAMETER = "remaining";
	
	/**
	 * Launches the count down.
	 * <p>
	 * For repeated invocations of this method only the first one has an effect.
	 */
	void launch();
	
	/**
	 * Resets the count down to the initial time period.
	 * <p>
	 * This method has no effect if {@code #isExpired()} returns true.
	 */
	void resetClock();
	
	/**
	 * Returns true after the count down is period elapsed.
	 * @return true after the count down is period elapsed, false otherwise
	 */
	boolean isExpired();
	
}