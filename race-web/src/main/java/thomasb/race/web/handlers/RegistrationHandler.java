package thomasb.race.web.handlers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.json.JsonValue;
import javax.servlet.ServletException;

import thomasb.race.web.dispatch.HandlerRegistry;
import thomasb.race.web.handlers.ScoreHandler.ExpirationListener;
import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.RequestHandler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class RegistrationHandler extends CountDownHandler {
	private static final String NAME_PARAMETER = "name";
	private static final String COLOR_PARAMETER = "color";
	
	private final HandlerRegistry registry;
	private final RaceContext raceContext;
	private final Map<String, String> names = Maps.newHashMap();
	private final Path scoresFile;
	
	private RequestHandler successor;
	
	public RegistrationHandler(HandlerRegistry registry,
			RaceContext raceContext,
			Path scoresFile) {
		super(new ArrayList<String>(), 5000, 1000, raceContext.getHandlers());
		this.registry = registry;
		this.raceContext = raceContext;
		this.scoresFile = scoresFile;
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		String participant = context.getRequest().getSession().getId();
		String name = context.getRequest().getParameter(NAME_PARAMETER);
		
		int rank = register(participant, name);
		if (rank > 0) {
			JsonValue color = raceContext.getConverter().serialize(PlayerColors.INSTANCE.get(rank));
			context.setResponseParameter(COLOR_PARAMETER, color);
		}
		
		super.handle(context);
	}

	@Override
	protected void onExpire() {
		if (allParticipantsClosed()) {
			registry.remove(getId());
		}
	}

	private synchronized int register(String sessionId, String name) {
		if (getParticipants().contains(sessionId)) {
			return -1;
		}
		
		addParticipant(sessionId);
		names.put(sessionId, name);
		
		if (getParticipants().size() == 2) {
			launch();
		}
		
		if (getParticipants().size() > 1) {
			reset();
		}
		
		return getParticipants().size();
	}
	
	protected synchronized RequestHandler getSuccessor() {
		if (successor == null) {
			initSuccessors();
		}
		
		return successor;
	}
	
	private void initSuccessors() {
		List<String> participants = ImmutableList.copyOf(getParticipants());
		
		ScoreHandler scoreHandler = new ScoreHandler(participants,
				names,
				raceContext.getConverter(),
				raceContext.getHandlers(),
				scoresFile);
		
		RaceHandler raceHandler = new RaceHandler(participants,
				raceContext,
				scoreHandler);
		
		LaunchHandler launchHandler = new LaunchHandler(participants,
				raceHandler,
				raceContext.getHandlers());
		
		scoreHandler.setExpirationListener(new UnregisterListener(
				ImmutableList.of(scoreHandler, raceHandler, launchHandler),
				registry));
		
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
