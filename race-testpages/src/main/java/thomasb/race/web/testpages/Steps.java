package thomasb.race.web.testpages;

import static java.util.Collections.nCopies;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import thomasb.web.clocking.ClockedRequest;
import thomasb.web.clocking.ClockedRequestHandler;
import thomasb.web.clocking.ClockedRequestHandlerImp;
import thomasb.web.clocking.ClockedRequestProcessor;

@SuppressWarnings("serial")
public class Steps extends HttpServlet {
	private static final ClockedRequestProcessor<JsonObject> REQUEST_PROCESSOR = new ClockedRequestProcessor<JsonObject>() {
			private static final String DATA_PARAMETER = "data";
			private static final String ID_PARAMETER = "id";
			private static final String POSITION_PARAMETER = "pos";
			private static final String CONTROL_PARAMETER = "command";
			
			@Override
			public JsonObject preprocess(AsyncContext request, int requestTime)
					throws ServletException, IOException {
				String data = request.getRequest().getParameter(DATA_PARAMETER);
				JsonReader dataParser = Json.createReader(new StringReader(data));
				
				JsonObject dataObject = dataParser.readObject();
				JsonString id = dataObject.getJsonString(ID_PARAMETER);
				JsonObject position = dataObject.getJsonObject(POSITION_PARAMETER);
				int command = dataObject.getInt(CONTROL_PARAMETER, 0);
				
				JsonObjectBuilder resultBuilder = Json.createObjectBuilder();
				resultBuilder.add(ID_PARAMETER, id);
				resultBuilder.add(POSITION_PARAMETER, incrementPosition(command, position));
				
				return resultBuilder.build();
			}
			
			@Override
			public JsonStructure timeoutResponse(AsyncContext request, int requestTime)
					throws ServletException, IOException {
				return Json.createArrayBuilder().build();
			}
			
			@Override
			public List<JsonStructure> process(List<ClockedRequest<JsonObject>> requests) {
				JsonArrayBuilder responseArrayBuilder = Json.createArrayBuilder();
				for (ClockedRequest<JsonObject> request : requests) {
					JsonObject responseData = request.getData();
					responseArrayBuilder.add(responseData);
				}
				
				JsonStructure positionArray = responseArrayBuilder.build();
				List<JsonStructure> result = nCopies(requests.size(), positionArray);
				
				return result;
			}
			
			private JsonObject incrementPosition(int control, JsonObject position) {
				if (control < 37 || 40 < control) {
					return position;
				}
				
				JsonObjectBuilder positionBuilder = Json.createObjectBuilder();
				switch(control) {
					case 37:
						positionBuilder.add("x", getX(position) - 1);
						positionBuilder.add("y", position.getJsonNumber("y"));
						break;
					case 38:
						positionBuilder.add("x", position.getJsonNumber("x"));
						positionBuilder.add("y", getY(position) + 1);
						break;
					case 39:
						positionBuilder.add("x", getX(position) + 1);
						positionBuilder.add("y", position.getJsonNumber("y"));
						break;
					case 40:
						positionBuilder.add("x", position.getJsonNumber("x"));
						positionBuilder.add("y", getY(position) - 1);
						break;
				}
				
				return positionBuilder.build();
			}
			
			private int getX(JsonObject position) {
				return position.getJsonNumber("x").intValue();
			};
			
			private int getY(JsonObject position) {
				return position.getJsonNumber("y").intValue();
			};
		};
	
	private ClockedRequestHandler clockedRequestHandler;
	
	public Steps() {
		clockedRequestHandler = new ClockedRequestHandlerImp(2, 1000, REQUEST_PROCESSOR);
	}
	
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		clockedRequestHandler.handle(request, response);
	}
}
