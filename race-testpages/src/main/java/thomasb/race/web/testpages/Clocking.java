package thomasb.race.web.testpages;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.clocking.ClockedRequestHandler;
import thomasb.web.clocking.ClockedRequestProcessor;

@SuppressWarnings("serial")
public class Clocking extends HttpServlet {
	private static final ClockedRequestProcessor REQUEST_PROCESSOR = new ClockedRequestProcessor() {
			@Override
			public void service(int requestTime, HttpServletRequest request,
					HttpServletResponse response) throws IOException {
				response.setContentType("application/json");
				
				JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
				jsonBuilder.add(ClockedRequestHandler.TIME_PARAMETER, requestTime);
				JsonObject jsonObject = jsonBuilder.build();
				
				Json.createWriter(response.getWriter()).writeObject(jsonObject);
			}
			
			@Override
			public void timeoutResponse(int requestTime, HttpServletResponse response) {
				//do nothing
			}
		};
	
	private ClockedRequestHandler clockedRequestHandler;
	
	public Clocking() {
		clockedRequestHandler = new ClockedRequestHandler(2, 1000, REQUEST_PROCESSOR);
	}
	
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		clockedRequestHandler.handle(request, response);
	}
}
