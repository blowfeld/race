package thomasb.race.app.handlers;

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
import javax.json.JsonValue;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import thomasb.web.clocking.ClockedRequest;
import thomasb.web.clocking.ClockedRequestProcessor;

public class StepProcessor implements ClockedRequestProcessor<JsonObject> {
	private static final String REDIRECT_PARAMETER = "redirect";
	private static final String EVENT_DATA_PARAMETER = "eventData";
	private static final String DATA_PARAMETER = "data";
	private static final String ID_PARAMETER = "id";
	private static final String POSITION_PARAMETER = "pos";
	private static final String CONTROL_PARAMETER = "command";
	
	private final ScoreHandler scoreHandler;
	private final JsonStructure initialData;
	
	public StepProcessor(List<String> participants, ScoreHandler scoreHandler) {
		this.scoreHandler = scoreHandler;
		
		JsonArrayBuilder participantArray = Json.createArrayBuilder();
		for (String participant : participants) {
			participantArray.add(participant);
		}
		
		this.initialData = Json.createObjectBuilder()
				.add("participants", participantArray)
			.build();
	}
	
	@Override
	public JsonStructure initalData(HttpServletRequest request) {
		return initialData;
	}
	
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
		JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
		if (!requests.isEmpty() && requests.get(0).getTime() > 10) {
			responseBuilder.add(REDIRECT_PARAMETER, scoreHandler.getId().toString());
		} else {
			responseBuilder.add(REDIRECT_PARAMETER, JsonValue.NULL);
		}
		
		JsonArrayBuilder positionArrayBuilder = Json.createArrayBuilder();
		for (ClockedRequest<JsonObject> request : requests) {
			JsonObject responseData = request.getData();
			positionArrayBuilder.add(responseData);
		}
		
		responseBuilder.add(EVENT_DATA_PARAMETER, positionArrayBuilder);
		
		JsonStructure response = responseBuilder.build();
		List<JsonStructure> result = nCopies(requests.size(), response);
		
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
}