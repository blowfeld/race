package thomasb.race.app.handlers;

import static thomasb.race.engine.PlayerStatus.ACTIVE;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
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

import thomasb.race.app.json.JsonConverter;
import thomasb.race.engine.ControlEvent;
import thomasb.race.engine.PathSegment;
import thomasb.race.engine.PlayerState;
import thomasb.race.engine.PointDouble;
import thomasb.race.engine.RaceEngine;
import thomasb.race.engine.RacePath;
import thomasb.race.engine.RaceTrack;
import thomasb.web.clocking.ClockedRequest;
import thomasb.web.clocking.ClockedRequestProcessor;
import thomasb.web.handler.RequestHandler;

final class RaceProcessor implements ClockedRequestProcessor<RaceData> {
	private static final String PARTICIPANTS_PARAMETER = "participants";
	private static final String GRID_PARAMETER = "grid";
	private static final String ID_PARAMETER = "id";
	private static final String CONTROL_PARAMETER = "command";
	private static final String STATE_PARAMETER = "state";
	private static final String SERVER_TIME = "serverTime";
	private static final String REDIRECT_PARAMETER = "redirect";
	private static final String EVENT_DATA_PARAMETER = "eventData";
	
	private static final JsonObject INIT_LAPS = Json.createObjectBuilder()
			.add(JsonConverter.LAP_COUNT, -1)
			.add(JsonConverter.LAP_TIME, 0.0)
		.build();;
	private static final JsonObject INIT_CONTROL = Json.createObjectBuilder()
			.add(JsonConverter.SPEED, 0)
			.add(JsonConverter.STEERING, 0)
		.build();
	private static final JsonString INIT_STATUS = Json.createArrayBuilder()
			.add(ACTIVE.name())
		.build().getJsonString(0);
	
	private final RaceEngine engine;
	private final JsonConverter converter;
	private final RaceRedirect redirect;
	private final JsonArray jsonParticipants;
	private final JsonObject grid;
	private final Map<String, JsonValue> startPositions;
	
	RaceProcessor(List<String> participants,
			RaceContext raceContext,
			RequestHandler scoreHandler) {
		this(participants,
				raceContext.getTrack(),
				raceContext.getEngine(),
				raceContext.getConverter(),
				scoreHandler,
				raceContext.getMaxTime());
	}
	
	RaceProcessor(List<String> participants,
			RaceTrack track,
			RaceEngine engine,
			JsonConverter converter,
			RequestHandler scoreHandler,
			int maxTime) {
		this.engine = engine;
		this.converter = converter;
		this.redirect = new RaceRedirect(scoreHandler, maxTime);
		
		JsonArrayBuilder participantArray = Json.createArrayBuilder();
		for (String participant : participants) {
			participantArray.add(participant);
		}
		this.jsonParticipants = participantArray.build();
		
		Map<String, JsonValue> startPositions = new HashMap<>();
		Iterator<? extends PointDouble> startGrid = track.getStartGrid().iterator();
		JsonObjectBuilder gridBuilder = Json.createObjectBuilder();
		for (String participant : participants) {
			PointDouble startPosition = startGrid.next();
			startPositions.put(participant, createInitialState(startPosition, converter));
			gridBuilder.add(participant, converter.serialize(startPosition));
		}
		this.startPositions = startPositions;
		this.grid = gridBuilder.build();
	}
	
	private static JsonValue createInitialState(PointDouble position, JsonConverter converter) {
		return Json.createObjectBuilder()
				.add(JsonConverter.POSITION, converter.serialize(position))
				.add(JsonConverter.CONTROL, INIT_CONTROL)
				.add(JsonConverter.LAPS, INIT_LAPS)
				.add(JsonConverter.STATUS, INIT_STATUS)
			.build();
	}
	
	@Override
	public JsonStructure initalData(HttpServletRequest request) {
		JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
		
		String sessionId = request.getSession().getId();
		responseBuilder.add(ID_PARAMETER, sessionId)
				.add(STATE_PARAMETER, startPositions.get(sessionId))
				.add(PARTICIPANTS_PARAMETER, jsonParticipants)
				.add(GRID_PARAMETER, grid);
		
		return responseBuilder.build();
	}

	@Override
	public RaceData preprocess(AsyncContext request, int requestTime)
			throws ServletException, IOException {
		String data = request.getRequest().getParameter(ClockedRequest.DATA_PARAMETER);

		JsonReader dataParser = Json.createReader(new StringReader(data));
		JsonObject dataObject = dataParser.readObject();
		
		JsonString id = dataObject.getJsonString(ID_PARAMETER);
		JsonObject jsonState = dataObject.getJsonObject(STATE_PARAMETER);
		
		PlayerState playerState = converter.deserializePlayerState(jsonState);
		ControlEvent event = converter.deserializeControlEvent(dataObject.getJsonNumber(CONTROL_PARAMETER));
		
		RacePath path = engine.calculatePath(playerState.adjust(event), requestTime, 1.0);
		
		return new RaceData(id, path);
	}
	
	@Override
	public JsonStructure timeoutResponse(AsyncContext request, int requestTime, int currentTime)
			throws ServletException, IOException {
		return Json.createObjectBuilder()
				.add(SERVER_TIME, currentTime)
			.build();
	}

	@Override
	public List<JsonStructure> process(List<? extends ClockedRequest<RaceData>> requests) {
		JsonObjectBuilder eventDataBuilder = Json.createObjectBuilder();
		for (ClockedRequest<RaceData> request : requests) {
			RaceData responseData = request.getData();
			
			List<? extends PathSegment> segments = responseData.getPath().getSegments();
			eventDataBuilder.add(responseData.getId(), converter.serialize(segments));
		}
		
		JsonObject eventData = eventDataBuilder.build();
		
		List<JsonStructure> result = new ArrayList<>();
		for (ClockedRequest<RaceData> request : requests) {
			JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
			
			RaceData responseData = request.getData();
			responseBuilder.add(ID_PARAMETER, responseData.getJsonId());
			String redirectUrl = redirect.url(requests);
			if (redirectUrl != null) {
				responseBuilder.add(REDIRECT_PARAMETER, redirectUrl);
			}
			PlayerState endState = responseData.getPath().getEndState();
			responseBuilder.add(STATE_PARAMETER, converter.serialize(endState));
			responseBuilder.add(EVENT_DATA_PARAMETER, eventData);
			
			result.add(responseBuilder.build());
		}
		
		return result;
	}
}