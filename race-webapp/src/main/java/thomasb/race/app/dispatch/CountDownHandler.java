package thomasb.race.app.dispatch;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonValue;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.dispatch.HandlerRegistry;
import thomasb.web.dispatch.JsonHandlerContext;
import thomasb.web.dispatch.RegistrationListener;
import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.RequestHandler;
import thomasb.web.latch.TimeLatchHandler;
import thomasb.web.latch.TimeLatchHandlerImp;

public class CountDownHandler implements RequestHandler {
	private final RegistrationListener listener;
	private final UUID id = UUID.randomUUID();
	
	private final Set<String> participants = new HashSet<>();
	private final TimeLatchHandler timeLatch;
	private volatile CountDownHandler successor;
	
	public CountDownHandler(HandlerRegistry registry) {
		this.listener = new RegistrationListener(registry);
		this.timeLatch = new TimeLatchHandlerImp(10000, 1000);
	}
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handle(new JsonHandlerContext(request, response));
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		if (successor == null) {
			successor = new CountDownHandler(listener.getRegistry());
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
				listener.remove(this);
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
}
