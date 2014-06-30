package thomasb.race.web.handlers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.clocking.ClockedRequestHandler;
import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.RequestHandler;

public class RaceHandler implements RequestHandler {
	private final ClockedRequestHandler clockedRequestHandler;
	private final UUID id = UUID.randomUUID();
	
	public RaceHandler(List<String> participants, RaceContext raceContext, ScoreHandler scoreHandler) {
		RaceProcessor stepProcessor = new RaceProcessor(participants,
				raceContext,
				scoreHandler);
		
		this.clockedRequestHandler = raceContext.getHandlers().clockedRequestHandler(participants,
				raceContext.getUpdateInterval(),
				raceContext.getTimeout(),
				stepProcessor);
	}
	
	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		handle(context.getRequest(), context.getResponse());
	}
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		clockedRequestHandler.handle(request, response);
	}
	
	@Override
	public UUID getId() {
		return id;
	}
}
