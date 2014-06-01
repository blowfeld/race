package thomasb.race.web.dispatch.testpages;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonValue;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.dispatch.HandlerContext;
import thomasb.web.dispatch.HandlerRegistry;
import thomasb.web.dispatch.JsonHandlerContext;
import thomasb.web.dispatch.RegistrationListener;
import thomasb.web.dispatch.RequestHandler;
import thomasb.web.latch.TimeLatchHandler;

public class CountDownHandler implements RequestHandler {
	private final DispatchListener listener;
	private final UUID id;
	
	private final Set<String> participants = new HashSet<>();
	private final TimeLatchHandler timeLatch;
	private volatile CountDownHandler successor;
	
	public static CountDownHandler create(HandlerRegistry registry) {
		UUID id = UUID.randomUUID();
		DispatchListener listener = new DispatchListener(id, registry);
		
		return new CountDownHandler(id, listener);
	}
	
	private CountDownHandler(UUID id, DispatchListener listener) {
		this.id = id;
		this.listener = listener;
		this.timeLatch = new TimeLatchHandler(10000, 1000);
	}
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handle(new JsonHandlerContext(request, response));
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		if (successor == null) {
			successor = create(listener.getRegistry());
			listener.putIfAbsent(successor.getId(), successor);
		}
		
		String participant = context.getRequest().getSession().getId();
		
		if (participants.size() == 1) {
			timeLatch.launch();
		}
		
		if (participants.size() < 1 || !participants.contains(participant)) {
			timeLatch.resetClock();
		}
		
		setHandler(context);
		timeLatch.handle(context);
		
		if (timeLatch.isExpired()) {
			participants.remove(participant);
			if (participants.isEmpty()) {
				listener.remove();
			}
		}
	}
	
	private void setHandler(HandlerContext context) {
		JsonValue currentId = Json.createArrayBuilder().add(getId().toString()).build().get(0);
		JsonValue redirectId = Json.createArrayBuilder().add(successor.getId().toString()).build().get(0);
		context.setResponseParameter("handler", currentId);
		context.setResponseParameter("redirect", redirectId);
	}
	
	boolean contains(String id) {
		return participants.contains(id);
	}
	
	boolean register(String id) {
		return participants.add(id);
	}
	
	boolean closed() {
		return timeLatch.isExpired();
	}
	
	@Override
	public UUID getId() {
		return id;
	}
	
	private static class DispatchListener extends RegistrationListener {
		private final HandlerRegistry handlerRegistry;
		
		DispatchListener(UUID id, HandlerRegistry handlerRegistry) {
			super(id, handlerRegistry);
			this.handlerRegistry = handlerRegistry;
		}
		
		public HandlerRegistry getRegistry() {
			return handlerRegistry;
		}
		
		@Override
		public boolean replace(RequestHandler oldHandler) {
			CountDownHandler newHandler = CountDownHandler.create(handlerRegistry);
			
			return replaceHandler(oldHandler, newHandler);
		}
	}
}
