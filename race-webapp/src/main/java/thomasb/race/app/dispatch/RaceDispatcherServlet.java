package thomasb.race.app.dispatch;

import thomasb.web.dispatch.DispatchServlet;
import thomasb.web.handler.RequestHandler;


public class RaceDispatcherServlet extends DispatchServlet {
	private static final long serialVersionUID = 1L;

	private CountDownHandler current = new CountDownHandler(getRegistry());

	@Override
	protected synchronized RequestHandler assignHandler(String id) {
		if (current.closed() && current.contains(id)) {
			return current;
		}
		
		if (current.closed() && !current.contains(id)) {
			current = new CountDownHandler(getRegistry());
		}
		
		current.register(id);
		
		return current;
	}
}
