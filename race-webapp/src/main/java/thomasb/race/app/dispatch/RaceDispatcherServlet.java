package thomasb.race.app.dispatch;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import thomasb.race.app.handlers.WebUtilHandlers;
import thomasb.race.app.json.RaceJsonConverter;
import thomasb.race.engine.RaceEngineImp;
import thomasb.race.web.handlers.RaceContext;
import thomasb.race.web.handlers.RegistrationHandler;
import thomasb.web.dispatch.DispatchServlet;
import thomasb.web.dispatch.RegistrationListener;
import thomasb.web.handler.RequestHandler;


public class RaceDispatcherServlet extends DispatchServlet {
	private static final long serialVersionUID = 1L;
	
	private RegistrationHandler current;
	
	private Path scoresFile;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		String scoresFileName = config.getInitParameter("scores_file");
		scoresFile = Paths.get(scoresFileName);
	}
	
	@Override
	protected synchronized RequestHandler assignHandler(String id) {
		if (current == null ||
				(current.closed() && !current.contains(id)) ||
				!getRegistry().containsKey(current.getId())) {
			RegistrationListener registrationListener = new RegistrationListener(getRegistry());
			
			RaceContext raceContext = new RaceContext(RaceTrackDefinition.INSTANCE,
					new RaceEngineImp(RaceTrackDefinition.INSTANCE),
					RaceJsonConverter.INSTANCE,
					WebUtilHandlers.INSTANCE,
					15 * 60 * 1000,
					1000,
					200);
			current = new RegistrationHandler(registrationListener,
					raceContext,
					scoresFile);
		}
		
		return current;
	}
}
