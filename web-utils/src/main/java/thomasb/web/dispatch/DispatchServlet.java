package thomasb.web.dispatch;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A {@code DispatchHandler} dispatches requests based on their handler id.
 * <p>
 * Requests received by the {@link #handle(HttpServletRequest, HttpServletResponse)}
 * method will be dispatched to a handler registered with the handler id of the
 * request. If no handler is registered for the id, the
 * {@link #assignHandler(String)} will be called with the current session id
 * and the returned {@code IdRequestHandler} will be registered for this handler
 * id.
 * <p>
 * Registered {@code IdRequestHandler}s are supposed to use a
 * {@link RegistrationListener} to modify registry entries.
 */
public abstract class DispatchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Pattern ID_PATTERN = Pattern.compile("^/([0-9a-fA-F\\-]+)(/|$)");
	
	private final HandlerRegistry registry;
	
	protected DispatchServlet() {
		this(new HandlerRegistryMap());
	}
	
	DispatchServlet(HandlerRegistry registry) {
		this.registry = registry;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException ,IOException {
		handle(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException ,IOException {
		handle(request, response);
	}
	
	private final void handle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		UUID id = null;
		try {
			id = readHandlerId(request);
		} catch (HandlerIdException e) {
			response.sendError(400, "url path is invalid: " + request.getPathInfo());
			return;
		}
		
		RequestHandler handler = getOrRegister(id, request.getSession().getId());
		if (handler == null) {
			response.sendError(404, "url path is not present: " + request.getPathInfo());
			return;
		}
		
		handler.handle(request, response);
	}
	
	private RequestHandler getOrRegister(UUID id, String sessionId) {
		if (id != null) {
			return registry.get(id);
		}

		RequestHandler newHandler = assignHandler(sessionId);
		RequestHandler existing = registry.putIfAbsent(newHandler.getId(), newHandler);
		
		return existing == null ? newHandler : existing;
	}
	
	/**
	 * Assign a new or existing {@link RequestHandler} to the given id.
	 * <p>
	 * This method is called if a request is received that is not yet
	 * registered with this instance of the {@code DispatcheHandler}.
	 * <p>
	 * Implementations of this method must be prepared for concurrent calls
	 * with the same id, due to concurrent request to this
	 * {@code DispatcheHandler}. In such cases equivalent {@code IdRequestHandler}
	 * instances must be returned, as only one of them will be registered with
	 * this {@code DispatcheHandler}. Once a {@code IdRequestHandler} is
	 * registered it will handle all requests for the given id until explicitly
	 * replaced or removed from the {@link #registry}.
	 * 
	 * @param id the session id of the received request
	 * @return a new or existing instance of {@code IdRequestHandler}
	 */
	protected abstract RequestHandler assignHandler(String id);
	
	protected final HandlerRegistry getRegistry() {
		return registry;
	}
	
	protected final UUID readHandlerId(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			return null;
		}
		
		Matcher matcher = ID_PATTERN.matcher(pathInfo);
		if (matcher.matches()) {
			return UUID.fromString(matcher.group(1));
		}
		
		throw new HandlerIdException();
	}
	
	private static class HandlerIdException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
}
