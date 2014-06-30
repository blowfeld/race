package thomasb.race.web.handlers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.RequestHandler;

public class LaunchHandler extends CountDownHandler {
	private final RaceHandler raceHandler;
	
	private volatile boolean launched = false;
	
	public LaunchHandler(List<String> participants, RaceHandler raceHandler, RaceContext context) {
		super(participants, context.getLaunchInterval(), context.getCountdownResolution(), context.getHandlers());
		this.raceHandler = raceHandler;
	}

	@Override
	public void handle(HandlerContext context) throws ServletException, IOException {
		if (!launched) {
			launched = true;
			launch();
		}
		
		super.handle(context);
	}
	
	@Override
	protected RequestHandler getSuccessor() {
		return raceHandler;
	}
}
