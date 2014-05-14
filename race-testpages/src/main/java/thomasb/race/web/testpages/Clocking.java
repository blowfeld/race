package thomasb.race.web.testpages;

import java.io.IOException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.clocking.ClockedRequest;
import thomasb.web.clocking.ClockedRequestHandler;
import thomasb.web.clocking.ClockedRequestProcessor;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class Clocking extends HttpServlet {
	private static final ClockedRequestProcessor<Void> REQUEST_PROCESSOR = new ClockedRequestProcessor<Void>() {
			@Override
			public ClockedRequest<Void> service(int requestTime, AsyncContext request)
					throws IOException {
				HttpServletResponse response = (HttpServletResponse) request.getResponse();
				response.setContentType("application/json");
				
				JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
				jsonBuilder.add(ClockedRequestHandler.TIME_PARAMETER, requestTime);
				JsonObject jsonObject = jsonBuilder.build();
				
				Json.createWriter(response.getWriter()).writeObject(jsonObject);
				
				return new ClockedRequest<Void>(request, null, requestTime);
			}
			
			@Override
			public ClockedRequest<Void> timeoutResponse(int requestTime, AsyncContext request) {
				return new ClockedRequest<Void>(request, null, requestTime);
			}
			
			@Override
			public List<AsyncContext> process(List<ClockedRequest<Void>> requests) {
				List<AsyncContext> result = Lists.newArrayList();
				for (ClockedRequest<Void> request : requests) {
					result.add(request.getContext());
				}
				
				return result;
			}
		};
	
	private ClockedRequestHandler clockedRequestHandler;
	
	public Clocking() {
		clockedRequestHandler = new ClockedRequestHandler(2, 1000, REQUEST_PROCESSOR);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		clockedRequestHandler.handle(request, response);
	}
}
