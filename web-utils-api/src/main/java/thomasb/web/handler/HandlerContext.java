package thomasb.web.handler;

import java.io.IOException;

import javax.json.JsonValue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* A {@code HandlerContext} is a container object that allows handlers to add
* additional response parameter that should be added to the responses without
* actually writing the response. If handlers are chained, it needs to be ensured
* that the {@code #writeResponse()} method is called in the appropriate place.
* 
* @param <T> the type of the data attached to a {@code ClockedRequest}
*/
public interface HandlerContext {

	/** Returns the original request.
	 * 
	 * @return the original request
	 */
	HttpServletRequest getRequest();
	
	/** Returns the original response object.
	 * 
	 * @return the original object
	 */
	HttpServletResponse getResponse();
	
	/**
	 * Adds the specified parameter value with the specified name.
	 * <p>
	 * The parameter is written to the response as a property of a JSON object
	 * with the specified name when the {@code #writeResponse()} method is called.
	 *
	 * @param name the name of the property in the JSON object sent with the response
	 * @param value the {@link JsonValue} of the parameter
	 */
	void setResponseParameter(String name, JsonValue value);
	
	/**
	 * Writes a JSON object with the set parameters as properties to the response.
	 * 
	 * @throws IOException if an i/o error occurs
	 */
	void writeResponse() throws IOException;
}
