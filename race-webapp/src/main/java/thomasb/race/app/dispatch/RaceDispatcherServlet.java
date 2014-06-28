package thomasb.race.app.dispatch;

import thomasb.race.app.handlers.RaceContext;
import thomasb.race.app.handlers.RegistrationHandler;
import thomasb.race.app.json.RaceJsonConverter;
import thomasb.race.engine.RaceEngineImp;
import thomasb.web.dispatch.DispatchServlet;
import thomasb.web.dispatch.RegistrationListener;
import thomasb.web.handler.RequestHandler;


public class RaceDispatcherServlet extends DispatchServlet {
	private static final long serialVersionUID = 1L;
	
	private RegistrationHandler current;
	
	@Override
	protected synchronized RequestHandler assignHandler(String id) {
		if (current != null && current.closed() && current.contains(id)) {
			return current;
		}
		
		if (current == null || (current.closed() && !current.contains(id))) {
			RegistrationListener registrationListener = new RegistrationListener(getRegistry());
			
			RaceContext raceContext = new RaceContext(RaceTrackDefinition.INSTANCE,
					new RaceEngineImp(RaceTrackDefinition.INSTANCE),
					new RaceJsonConverter(),
					15 * 60 * 1000,
					1000,
					200);
			current = new RegistrationHandler(registrationListener, raceContext );
		}
		
		return current;
	}
}
