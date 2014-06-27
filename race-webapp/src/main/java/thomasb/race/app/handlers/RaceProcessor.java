package thomasb.race.app.handlers;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import thomasb.race.app.json.JsonConverter;
import thomasb.race.engine.PointDouble;
import thomasb.race.engine.RaceEngine;
import thomasb.race.engine.RaceTrack;
import thomasb.web.clocking.ClockedRequest;
import thomasb.web.clocking.ClockedRequestProcessor;
import thomasb.web.handler.RequestHandler;

final class RaceProcessor implements ClockedRequestProcessor<JsonObject> {
	private static final String GRID_PARAMETER = "grid";
	
	private final RaceEngine engine;
	private final RaceTrack track;
	private final JsonConverter converter;
	private final List<String> participants;
	private final RequestHandler scoreHandler;
	
	RaceProcessor(List<String> participants,
			RaceTrack track,
			RaceEngine engine,
			JsonConverter converter,
			RequestHandler scoreHandler) {
		this.participants = participants;
		this.track = track;
		this.engine = engine;
		this.converter = converter;
		this.scoreHandler = scoreHandler;
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
	public JsonObject preprocess(AsyncContext request, int requestTime)
			throws ServletException, IOException {
		return null;
	}
	
	@Override
	public JsonStructure timeoutResponse(AsyncContext request, int requestTime, int currentTime)
			throws ServletException, IOException {
		return null;
	}

	@Override
	public List<JsonStructure> process(List<ClockedRequest<JsonObject>> requests) {
		return Collections.emptyList();
	}

}
