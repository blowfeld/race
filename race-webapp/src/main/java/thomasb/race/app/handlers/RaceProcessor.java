package thomasb.race.app.handlers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
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
	private static final String GRID_PARAMETER = "grid";
	private static final String ID_PARAMETER = "id";
	private static final String CONTROL_PARAMETER = "command";
	private static final String STATE_PARAMETER = "state";
	private static final String SERVER_TIME = "serverTime";
	private static final String REDIRECT_PARAMETER = "redirect";
	private static final String EVENT_DATA_PARAMETER = "eventData";
	
	private final RaceEngine engine;
	private final RaceTrack track;
	private final JsonConverter converter;
	private final List<String> participants;
	private final RaceRedirect redirect;
	
	RaceProcessor(List<String> participants,
			RaceTrack track,
			RaceEngine engine,
			JsonConverter converter,
			RequestHandler scoreHandler,
			int maxTime) {
		this.participants = participants;
		this.track = track;
		this.engine = engine;
		this.converter = converter;
		this.redirect = new RaceRedirect(scoreHandler, maxTime);
	}
	
	@Override
	public JsonStructure initalData(HttpServletRequest request) {
		Iterator<PointDouble> startGrid = track.getStartGrid().iterator();
		JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
		
		JsonObjectBuilder gridBuilder = Json.createObjectBuilder();
		for (String participant : participants) {
			gridBuilder.add(participant, converter.serialize(startGrid.next()));
		}
		
		responseBuilder.add(GRID_PARAMETER, gridBuilder);
		
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