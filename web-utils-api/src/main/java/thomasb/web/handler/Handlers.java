package thomasb.web.handler;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.clocking.ClockedRequestHandler;
import thomasb.web.clocking.ClockedRequestProcessor;
import thomasb.web.latch.TimeLatchHandler;

/**
 * Factory class for the interfaces in this module.
 */
public interface Handlers {

	/**
	 * Creates a new {@link TimeLatchHandler} instance with the specified
	 * duration and resolution.
	 * 
	 * @param duration duration of the count down interval in milliseconds.
	 * @param resolution step size resolution for the count down in milliseconds
	 * 
	 * @return a new {@link TimeLatchHandler} instance
	 */
	TimeLatchHandler timeLatchHandler(int duration, int resolution);
	
	/**
	 * Creates a new {@link ClockedRequestHandler} instance for the specified
	 * participants.
	 * 
	 * @param participants identifiers of the participants sending requests
	 * 			to the created {@code ClockedRequestHandler}
	 * @param interval the time interval length the code ClockedRequestHandler}
	 * 			counts with in milliseconds
	 * @param timeout the timeout interval of the code ClockedRequestHandler}
	 * 			in milliseconds
	 * @param requestProcessor the {@link ClockedRequestProcessor} used to
	 * 			process the received requests
	 * 
	 * @return a new {@link ClockedRequestHandler} instance
	 */
	ClockedRequestHandler clockedRequestHandler(Collection<String> participants,
			int interval,
			int timeout,
			ClockedRequestProcessor<?> requestProcessor);
	
	/**
	 * Creates a new {@link HandlerContext} instance based on the provided
	 * request and response.
	 * 
	 * @param request the request object
	 * @param response the response object
	 * 
	 * @return a new {@link HandlerContext} instance
	 */
	HandlerContext context(HttpServletRequest request, HttpServletResponse response);
	
}
