package thomasb.web.clocking;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A {@code ClockedRequest} is a container object that allows to attach
 * addition data to a request that can be accessed at the time the response is
 * written.
 * 
 * @param <T> the type of the data attached to a {@code ClockedRequest}
 */
public interface ClockedRequest<T> {
	
	/**
	 * Returns the original request.
	 * 
	 * @return the original request
	 */
	HttpServletRequest getRequest();
	
	/**
	 * Returns the response object associated with the request.
	 * 
	 * @return the response object associated with the request
	 */
	HttpServletResponse getResponse();
	
	/**
	 * Returns additional data attached to the request.
	 * 
	 * @return additional data attached to the request
	 */
	T getData();
	
	/**
	 * Returns the time interval in which the request was received.
	 * 
	 * @return the time interval in which the request was received
	 */
	int getTime();
	
}
