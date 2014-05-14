package thomasb.web.clocking;

import java.io.IOException;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;

public interface ClockedRequestProcessor<T> {
	
	/**
	 * Pre-processes the request for the given time interval count and creates
	 * a new {@link ClockedRequest} from the pre-processed request and
	 * intermediate data.
	 * <p>
	 * This method may be called from different threads.
	 * 
	 * @param requestTime the time interval count the request belongs to
	 * @param request the client request to be processed
	 * 
	 * @return a {@link ClockedRequest} with the processed request
	 * 
	 * @throws ServletException if an exception occurs that interferes
     *					with the servlet's normal operation 
     *
     * @throws IOException if an input or output exception occurs
     */

	ClockedRequest<T> service(int requestTime, AsyncContext request)
    		throws ServletException, IOException;
	
	/**
	 * Processes the response in case it is not received in time.
	 * 
	 * @param requestTime the time interval count the request was intended for
	 * @param request the client request to be processed
	 * @return a {@link ClockedRequest} with the processed request
	 * 
	 * @throws ServletException if an exception occurs that interferes
	 *					with the servlet's normal operation 
	 *
	 * @throws IOException if an input or output exception occurs
	 */
	ClockedRequest<T> timeoutResponse(int requestTime, AsyncContext request)
			throws ServletException, IOException;
	
	/**
	 * Processes the provided {@link ClockedRequest}s.
	 * <p>
	 * This method performs the final processing of the pre-processed client
	 * request, The responses can incorporate dependencies between the requests.
	 * <p>
	 * Exceptions occurring during processing must be handled by this method by
	 * creating an appropriate response.
	 * 
	 * @param requestTime the time interval count the request was intended for
	 * @param request the client request to be processed
	 * @return a list of {@link AsyncContext} ready for sending to the client
	 */
	List<AsyncContext> process(List<ClockedRequest<T>> requests);
}
