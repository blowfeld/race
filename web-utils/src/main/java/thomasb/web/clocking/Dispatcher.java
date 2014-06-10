package thomasb.web.clocking;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;

final class Dispatcher {
	private static final ExecutorService DISPATCHER = Executors.newCachedThreadPool();
	
	void submit(List<AsyncContext> requests) {
		for (AsyncContext asyncRequest : requests) {
			submit(asyncRequest);
		}
	}
	
	void submit(AsyncContext request) {
		DISPATCHER.execute(new Submission(request));
	}
	
	private static class Submission implements Runnable {
		private final AsyncContext request;
		
		Submission(AsyncContext request) {
			this.request = request;
		}
		
		@Override
		public void run() {
			request.complete();
		}
	}
}
