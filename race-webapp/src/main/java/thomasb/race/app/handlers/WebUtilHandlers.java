package thomasb.race.app.handlers;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.clocking.ClockedRequestHandler;
import thomasb.web.clocking.ClockedRequestHandlerImp;
import thomasb.web.clocking.ClockedRequestProcessor;
import thomasb.web.dispatch.JsonHandlerContext;
import thomasb.web.handler.HandlerContext;
import thomasb.web.handler.Handlers;
import thomasb.web.latch.TimeLatchHandler;
import thomasb.web.latch.TimeLatchHandlerImp;

public enum WebUtilHandlers implements Handlers {
	INSTANCE;
	
	@Override
	public TimeLatchHandler timeLatchHandler(int duration, int resolution) {
		return new TimeLatchHandlerImp(duration, resolution);
	}

	@Override
	public ClockedRequestHandler clockedRequestHandler(
			Collection<String> participants, int interval, int timeout,
			ClockedRequestProcessor<?> requestProcessor) {
		return new ClockedRequestHandlerImp(participants,
				interval,
				timeout,
				requestProcessor);
	}

	@Override
	public HandlerContext context(HttpServletRequest request,
			HttpServletResponse response) {
		return new JsonHandlerContext(request, response);
	}
}
