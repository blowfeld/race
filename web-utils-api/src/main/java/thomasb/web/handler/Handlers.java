package thomasb.web.handler;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.clocking.ClockedRequestHandler;
import thomasb.web.clocking.ClockedRequestProcessor;
import thomasb.web.latch.TimeLatchHandler;

public interface Handlers {
	
	TimeLatchHandler timeLatchHandler(int duration, int resolution);
	
	ClockedRequestHandler clockedRequestHandler(Collection<String> participants,
			int interval,
			int timeout,
			ClockedRequestProcessor<?> requestProcessor);
	
	HandlerContext context(HttpServletRequest request, HttpServletResponse response);
	
}
