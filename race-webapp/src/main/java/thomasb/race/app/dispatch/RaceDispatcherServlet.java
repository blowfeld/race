package thomasb.race.app.dispatch;

import thomasb.race.app.handlers.RegistrationHandler;
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
			current = new RegistrationHandler(registrationListener);
		}
		
		return current;
	}
}
