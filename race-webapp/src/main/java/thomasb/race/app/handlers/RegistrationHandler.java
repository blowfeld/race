package thomasb.race.app.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

import thomasb.race.app.handlers.ScoreHandler.ExpirationListener;
import thomasb.web.dispatch.HandlerRegistry;
import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.RequestHandler;

import com.google.common.collect.ImmutableList;

public class RegistrationHandler extends CountDownHandler {
	private final HandlerRegistry registry;
	
	private final RaceContext raceContext;
	
	private RequestHandler successor;
	
	public RegistrationHandler(HandlerRegistry registry, RaceContext raceContext) {
		super(new ArrayList<String>(), 5000, 1000);
		this.registry = registry;
		this.raceContext = raceContext;
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		String participant = context.getRequest().getSession().getId();
		register(participant);
		super.handle(context);
	}

	@Override
	protected void onExpire() {
		if (getParticipants().isEmpty()) {
			registry.remove(this);
		}
	}

	private synchronized void register(String sessionId) {
		if (getParticipants().contains(sessionId)) {
			return;
		}
		
		boolean added = getParticipants().add(sessionId);
		if (added && getParticipants().size() == 2) {
			launch();
		}
		
		if (added && getParticipants().size() > 1) {
			reset();
		}
	}
	
	protected synchronized RequestHandler getSuccessor() {
		if (successor == null) {
			initSuccessors();
		}
		
		return successor;
	}
	
	private void initSuccessors() {
		List<String> participants = ImmutableList.copyOf(getParticipants());
		ScoreHandler scoreHandler = new ScoreHandler(participants);
		RaceHandler raceHandler = new RaceHandler(participants, raceContext, scoreHandler);
		LaunchHandler launchHandler = new LaunchHandler(participants, raceHandler);
		scoreHandler.setExpirationListener(new UnregisterListener(
				ImmutableList.of(scoreHandler, raceHandler, launchHandler), registry));
		
		successor = launchHandler;
		
		registry.putIfAbsent(launchHandler.getId(), launchHandler);
		registry.putIfAbsent(raceHandler.getId(), raceHandler);
		registry.putIfAbsent(scoreHandler.getId(), scoreHandler);
	}
	
	private static class UnregisterListener implements ExpirationListener {
		private final HandlerRegistry registry;
		private final Collection<RequestHandler> handlers;

		UnregisterListener(Collection<RequestHandler> handlers,
				HandlerRegistry registry){
			this.handlers = handlers;
			this.registry = registry;
		}
		
		@Override
		public void expire() {
			for (RequestHandler handler : handlers) {
				registry.remove(handler.getId());
			}
		}
	}
}
