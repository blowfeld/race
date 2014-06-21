package thomasb.race.app.handlers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.clocking.ClockedRequestHandler;
import thomasb.web.clocking.ClockedRequestHandlerImp;
import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.RequestHandler;

public class RaceHandler implements RequestHandler {
	private final ClockedRequestHandler clockedRequestHandler;
	private final UUID id = UUID.randomUUID();
	
	public RaceHandler(List<String> participants, ScoreHandler scoreHandler) {
		StepProcessor stepProcessor = new StepProcessor(participants, scoreHandler);
		clockedRequestHandler = new ClockedRequestHandlerImp(participants, 50, 40, stepProcessor);
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
