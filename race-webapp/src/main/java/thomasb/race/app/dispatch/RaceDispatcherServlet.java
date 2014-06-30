package thomasb.race.app.dispatch;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import thomasb.race.app.handlers.WebUtilHandlers;
import thomasb.race.app.json.RaceJsonConverter;
import thomasb.race.engine.RaceEngineImp;
import thomasb.race.web.dispatch.DispatchServlet;
import thomasb.race.web.dispatch.RegistrationListener;
import thomasb.race.web.handlers.RaceContext;
import thomasb.race.web.handlers.RegistrationHandler;
import thomasb.web.handler.RequestHandler;


public class RaceDispatcherServlet extends DispatchServlet {
	private static final long serialVersionUID = 1L;
	
	private static final int LAUNCH_INTERVAL = 3000;
	
	private RegistrationHandler current;
	
	private Path scoresFile;
	private int maxTime;
	private int updateInterval;
	private int timeoutInterval;
	private int registrationInterval;
	private int scoresInterval;
	private int countdownResolution;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String scoresFileName = config.getInitParameter("scores_file");
		scoresFile = Paths.get(scoresFileName);
		maxTime = Integer.valueOf(config.getInitParameter("max_time_sec"));
		updateInterval = Integer.valueOf(config.getInitParameter("update_interval_ms"));
		timeoutInterval = Integer.valueOf(config.getInitParameter("timeout_interval_ms"));
		registrationInterval = Integer.valueOf(config.getInitParameter("registration_interval_ms"));
		scoresInterval = Integer.valueOf(config.getInitParameter("scores_interval_ms"));
		countdownResolution = Integer.valueOf(config.getInitParameter("countdown_resolution_ms"));
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
					scoresFile,
					maxTime,
					updateInterval,
					timeoutInterval,
					registrationInterval,
					LAUNCH_INTERVAL,
					scoresInterval,
					countdownResolution);
			current = new RegistrationHandler(registrationListener, raceContext);
		}
		
		return current;
	}
}
