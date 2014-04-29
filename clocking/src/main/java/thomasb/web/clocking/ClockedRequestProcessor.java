package thomasb.web.clocking;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ClockedRequestProcessor {

	/**
	 * Processes the request for the given time interval count.
	 * 
	 * @param requestTime the time interval count the request belongs to
	 * @param request the client request to be processed
	 * @param response the response object to be written
	 */
	void service(int requestTime, HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * Processes the response in case it is not received in time.
	 * 
	 * @param requestTime the time interval count the request was intended for
	 * @param response the response object to be written to
	 */
	void timeoutResponse(int requestTime, HttpServletResponse response);
}
