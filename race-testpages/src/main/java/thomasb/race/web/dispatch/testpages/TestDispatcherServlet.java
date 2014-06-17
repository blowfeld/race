package thomasb.race.web.dispatch.testpages;

import thomasb.web.dispatch.DispatchServlet;
import thomasb.web.handler.RequestHandler;


public class TestDispatcherServlet extends DispatchServlet {
	private static final long serialVersionUID = 1L;

	private CountDownHandler current = CountDownHandler.create(getRegistry());

	@Override
	protected synchronized RequestHandler assignHandler(String id) {
		if (current.closed() && current.contains(id)) {
			return current;
		}
		
		if (current.closed() && !current.contains(id)) {
			current = CountDownHandler.create(getRegistry());
		}
		
		current.register(id);
		
		return current;
	}
}
