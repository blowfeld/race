package thomasb.race.app.handlers;

import static java.util.Collections.newSetFromMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.dispatch.JsonHandlerContext;
import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.RequestHandler;
import thomasb.web.latch.TimeLatchHandler;
import thomasb.web.latch.TimeLatchHandlerImp;

public abstract class CountDownHandler implements RequestHandler {
	private final UUID id = UUID.randomUUID();
	private final List<String> participants;
	private final TimeLatchHandler timeLatch;
	private final Set<String> expirationSent = newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	
	public CountDownHandler(List<String> participants, int duration, int interval) {
		this.participants = new ArrayList<>(participants);
		this.timeLatch = new TimeLatchHandlerImp(duration, interval);
	}
	
	@Override
	public final void handle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		handle(new JsonHandlerContext(request, response));
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		timeLatch.handle(context);
		
		String participant = context.getRequest().getSession().getId();
		if (timeLatch.isExpired()) {
			RedirectUtil.setHandler(context, id, getSuccessor());
			expirationSent.add(participant);
			onExpire();
		} else {
			RedirectUtil.setHandler(context, id, null);
		}
		
		context.writeResponse();
	}
	
	protected abstract RequestHandler getSuccessor();
	
	protected void onExpire() {
		//do nothing by default
	}
	
	public final boolean contains(String id) {
		return participants.contains(id);
	}
	
	public final boolean closed() {
		return timeLatch.isExpired();
	}
	
	public final void launch() {
		timeLatch.launch();
	}
	
	public final void reset() {
		timeLatch.resetClock();
	}
	
	protected final boolean addParticipant(String participant) {
		return participants.add(participant);
	}
	
	protected final List<String> getParticipants() {
		return Collections.unmodifiableList(participants);
	}
	
	protected final boolean allParticipantsClosed() {
		return expirationSent.size() == participants.size();
	}
	
	@Override
	public UUID getId() {
		return id;
	}
}

