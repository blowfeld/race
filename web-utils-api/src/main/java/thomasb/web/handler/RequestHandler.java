package thomasb.web.handler;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A {@link RequestHandler} handles an HTTP request.
 */
public interface RequestHandler {
	
	/**
	 * Handles the specified request
	 * 
	 * @param request the request object associated with the request
	 * @param response the response object associated with the request
	 * 
	 * @throws ServletException if the request could not be handled
	 * @throws IOException if an input or output error is detected when the handler handles the request
	 */
	void handle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;
	
	/**
	 * Handles the specified request
	 * 
	 * @param context the {@link HandlerContext} based on the request
	 * 
	 * @throws ServletException if the request could not be handled
	 * @throws IOException if an input or output error is detected when the handler handles the request
	 */
	void handle(HandlerContext context)
			throws ServletException, IOException;
	/**
	 * Returns a unique id that can be used to identify the handler instance.
	 * 
	 * @return a unique {@link UUID}
	 */
	UUID getId();
	
}
