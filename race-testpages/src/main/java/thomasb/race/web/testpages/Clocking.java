package thomasb.race.web.testpages;

import static java.util.Collections.nCopies;

import java.io.IOException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonStructure;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.clocking.ClockedRequest;
import thomasb.web.clocking.ClockedRequestHandler;
import thomasb.web.clocking.ClockedRequestProcessor;

@SuppressWarnings("serial")
public class Clocking extends HttpServlet {
	private static final JsonArray EMPTY_ARRAY = Json.createArrayBuilder().build();

	private static final ClockedRequestProcessor<Void> REQUEST_PROCESSOR = new ClockedRequestProcessor<Void>() {
			@Override
			public Void preprocess(AsyncContext request, int requestTime)
					throws IOException {
				return null;
			}
			
			@Override
			public JsonStructure timeoutResponse(AsyncContext request, int requestTime) {
				return null;
			}
			
			@Override
			public List<? extends JsonStructure> process(List<ClockedRequest<Void>> requests) {
				return nCopies(requests.size(), EMPTY_ARRAY);
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
