package thomasb.web.dispatch;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A {@link RequestHandler} dispatches requests based on their session id.
 * <p>
 * Requests received by the {@link #handle(HttpServletRequest, HttpServletResponse)}
 * method will be dispatched to a handler registered with the session id of the
 * request. If no handler is registered for the id, the
 * {@link #assignHandler(String)} will be called and the returned
 * {@code RequestHandler} will be registered for this session id.
 * <p>
 * Registered {@code RequestHandler}s are supposed to use a
 * {@link RegistrationListener} to modify registry entries.
 */
public abstract class DispatchHandler implements RequestHandler {
	private final HandlerRegistry registry;
	
	protected DispatchHandler() {
		this(new HandlerRegistryMap());
	}
	
	DispatchHandler(HandlerRegistry registry) {
		this.registry = registry;
	}
	
	@Override
	public final void handle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = readId(request);

		RequestHandler handler = getOrRegister(id);
		handler.handle(request, response);
	}
	
	private RequestHandler getOrRegister(String id) {
		if (registry.containsKey(id)) {
			return registry.get(id);
		}

		RequestHandler newHandler = assignHandler(id);
		RequestHandler existing = registry.putIfAbsent(id, newHandler);
		
		return existing == null ? newHandler : existing;
	}
	
	/**
	 * Assign a new or existing {@link RequestHandler} to the given id.
	 * <p>
	 * This method is called if a request of a session that is not yet
	 * registered with this instance of the {@code DispatcheHandler} is
	 * received.
	 * <p>
	 * Implementations of this method must be prepared for concurrent calls
	 * with the same id, due to concurrent request to this
	 * {@code DispatcheHandler}. In such cases equivalent {@code RequestHandler}
	 * instances must be returned, as only one of them will be registered with
	 * this {@code DispatcheHandler}. Once a {@code RequestHandler} is
	 * registered it will handle all requests for the given id until explicitly
	 * replaced or removed from the {@link #registry}.
	 * 
	 * @param id the session id of a new session
	 * @return a new or existing instance of {@code RequestHandler}
	 */
	protected abstract RequestHandler assignHandler(String id);
	
	protected final HandlerRegistry getRegistry() {
		return registry;
	}
	
	protected final String readId(HttpServletRequest request) {
		return request.getSession().getId();
	}
}
