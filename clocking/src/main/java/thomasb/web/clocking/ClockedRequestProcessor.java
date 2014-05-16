package thomasb.web.clocking;

import java.io.IOException;
import java.util.List;

import javax.json.JsonStructure;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;

public interface ClockedRequestProcessor<T> {
	
	/**
	 * Preprocesses the request for the given time interval count and creates
	 * a new {@link ClockedRequest} from the preprocessed request and
	 * intermediate data.
	 * <p>
	 * This method may be called from different threads.
	 * @param request the client request to be processed
	 * @param requestTime the time interval count the request belongs to
	 * 
	 * @return data object with data from the preprocessing step
	 * 
	 * @throws ServletException if an exception occurs that interferes
     *					with the servlet's normal operation 
     *
     * @throws IOException if an input or output exception occurs
     */

	T preprocess(AsyncContext request, int requestTime)
    		throws ServletException, IOException;
	
	/**
	 * Processes the response in case it is not received in time.
	 * @param request the client request to be processed
	 * @param requestTime the time interval count the request was intended for
	 * 
	 * @return data object with data from the preprocessing step
	 * 
	 * @throws ServletException if an exception occurs that interferes
	 *					with the servlet's normal operation 
	 *
	 * @throws IOException if an input or output exception occurs
	 */
	JsonStructure timeoutResponse(AsyncContext request, int requestTime)
			throws ServletException, IOException;
	
	/**
	 * Processes the provided {@link ClockedRequest}s.
	 * <p>
	 * This method performs the final processing of the preprocessed client
	 * request, The responses can incorporate dependencies between the requests.
	 * <p>
	 * Exceptions occurring during processing must be handled by this method by
	 * creating an appropriate response.
	 * 
	 * @param requestTime the time interval count the request was intended for
	 * @param request the client request to be processed
	 * 
	 * @return a list of {@link JsonStructure}s to be sent with the responses
	 */
	List<? extends JsonStructure> process(List<ClockedRequest<T>> requests);
}
