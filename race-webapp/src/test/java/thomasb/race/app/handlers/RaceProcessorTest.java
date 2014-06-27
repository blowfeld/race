package thomasb.race.app.handlers;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import thomasb.race.app.json.RaceJsonConverter;
import thomasb.race.engine.ControlEvent;
import thomasb.race.engine.ControlState;
import thomasb.race.engine.Lap;
import thomasb.race.engine.PathSegment;
import thomasb.race.engine.PlayerState;
import thomasb.race.engine.PlayerStatus;
import thomasb.race.engine.PointDouble;
import thomasb.race.engine.RaceEngine;
import thomasb.race.engine.RacePath;
import thomasb.race.engine.RaceTrack;
import thomasb.web.clocking.ClockedRequest;
import thomasb.web.handler.RequestHandler;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class RaceProcessorTest {
	private static final UUID HANDLER_ID = UUID.randomUUID();
	
	@Mock RaceTrack track;
	@Mock RaceEngine engine;
	
	@Mock PointDouble point_0_0;
	@Mock PointDouble point_1_0;
	@Mock PointDouble point_2_0;
	
	@Mock PathSegment segment_1;
	@Mock PathSegment segment_2;

	@Mock RacePath path;
	
	@Mock ControlEvent event;
	@Mock ControlState controlState;
	@Mock Lap laps;
	@Mock PlayerState endState;
	
	@Mock RequestHandler scoreHandler;
	
	@Mock HttpServletRequest request;
	@Mock HttpServletResponse response;
	@Mock AsyncContext asyncRequest;
	
	ClockedRequest<JsonObject> clockedRequest_1;
	ClockedRequest<JsonObject> clockedRequest_2;
	
	private RaceProcessor processor;
	
	@Before
	public void setupPoints() {
		when(point_0_0.getX()).thenReturn(0.0);
		when(point_0_0.getY()).thenReturn(0.0);
		
		when(point_1_0.getX()).thenReturn(1.0);
		when(point_1_0.getY()).thenReturn(0.0);
		
		when(point_2_0.getX()).thenReturn(2.0);
		when(point_2_0.getY()).thenReturn(0.0);
	}
	
	@Before
	public void setupSegments() {
		when(segment_1.getStart()).thenReturn(point_0_0);
		when(segment_1.getEnd()).thenReturn(point_1_0);
		when(segment_2.getStart()).thenReturn(point_1_0);
		when(segment_2.getEnd()).thenReturn(point_2_0);
		
		when(segment_1.getStartTime()).thenReturn(1.0);
		when(segment_1.getEndTime()).thenReturn(1.5);
		when(segment_2.getStartTime()).thenReturn(1.5);
		when(segment_2.getEndTime()).thenReturn(2.0);
	}
	
	@Before
	public void setupStates() {
		when(controlState.getSpeed()).thenReturn(1);
		when(controlState.getSteering()).thenReturn(90);

		when(laps.getCount()).thenReturn(1);
		when(laps.getLapTime()).thenReturn(1.0);
		
		when(endState.getControlState()).thenReturn(controlState);
		when(endState.getPosition()).thenReturn(point_2_0);
		when(endState.getLaps()).thenReturn(laps);
		when(endState.getPlayerStatus()).thenReturn(PlayerStatus.ACTIVE);
	}
	
	@Before
	public void setupTrack() {
		when(track.getStartGrid()).thenReturn(ImmutableList.of(point_0_0, point_1_0, point_2_0));
	}
	
	@Before
	public void setupEngine() {
		when(path.getEndState()).thenReturn(endState);
		when(path.getEndState()).thenReturn(endState);
		Mockito.<List<? extends PathSegment>>when(path.getSegments())
				.thenReturn(ImmutableList.of(segment_1, segment_2));
		
		when(engine.calculatePath(any(PlayerState.class),
				doubleThat(comparesEqualTo(1.0)), doubleThat(comparesEqualTo(1.0))))
				.thenReturn(path);
	}
	
	@SuppressWarnings("unchecked")
	@Before
	public void setupRequest() {
		String baseRequestDataString =
				"{"
						+ "\"id\" : \"%s\","
						+ "\"state\" : {"
							+ "\"position\" : {"
								+ "\"x\" : 0.0,"
								+ "\"y\" : 0.0"
							+ "},"
							+ "\"status\" : \"ACTIVE\","
							+ "\"laps\" : {"
								+ "\"count\" : 1,"
								+ "\"lapTime\" : 1.0"
							+ "},"
							+ "\"control\" : {"
								+ "\"speed\" : 1,"
								+ "\"steering\" : 90"
							+ "}"
						+ "},"
						+ "%s"
				+ "}";
		
		String requestDataJson = String.format(baseRequestDataString, "1", "\"command\" : 37");
		when(request.getParameter(ClockedRequest.DATA_PARAMETER))
				.thenReturn(requestDataJson);
		
		when(asyncRequest.getRequest()).thenReturn(request);
		
		String eventsString = 
				"\"events\" : ["
						+ "{"
							+ "\"start\" : {\"x\" : 0.0, \"y\" : 0.0},"
							+ "\"end\" : {\"x\" : 1.0, \"y\" : 0.0},"
							+ "\"start_time\" : 1.0,"
							+ "\"end_time\" : 1.5"
						+ "},"
						+ "{"
							+ "\"start\" : {\"x\" : 1.0, \"y\" : 0.0},"
							+ "\"end\" : {\"x\" : 2.0, \"y\" : 0.0},"
							+ "\"start_time\" : 1.5,"
							+ "\"end_time\" : 2.0"
						+ "}"
					+ "]";
		
		String clockedDataJson_1 = String.format(baseRequestDataString, "1", eventsString);
		String clockedDataJson_2 = String.format(baseRequestDataString, "2", eventsString);
	
		clockedRequest_1 = mock(ClockedRequest.class);
		clockedRequest_2 = mock(ClockedRequest.class);
		
		when(clockedRequest_1.getData()).thenReturn((JsonObject) jsonFrom(clockedDataJson_1));
		when(clockedRequest_2.getData()).thenReturn((JsonObject) jsonFrom(clockedDataJson_2));
	}
	
	@Before
	public void setupScoreHandler() {
		when(scoreHandler.getId()).thenReturn(HANDLER_ID);
	}
	
	@Before
	public void setupProcessor() {
		List<String> participants = ImmutableList.of("1", "2");
		
		processor = new RaceProcessor(participants, track , engine, new RaceJsonConverter(), scoreHandler, 10);
	}
	
	@Test
	public void testInitData() {
		JsonStructure actual = processor.initalData(request);
		
		String expected =
				"{ \"grid\" : {"
						+ "\"1\" : {\"x\" : 0.0, \"y\" : 0.0},"
						+ "\"2\" : {\"x\" : 1.0, \"y\" : 0.0}"
					+ "}"
				+ "}";
		
		assertEquals(jsonFrom(expected), actual);
	}
	
	@Test
	public void testPreprocess() throws ServletException, IOException {
		JsonStructure actual = processor.preprocess(asyncRequest, 1);
		
		String expected =
				"{"
					+ "\"id\" : \"1\","
					+ "\"state\" : {"
						+ "\"position\" : {"
							+ "\"x\" : 2.0,"
							+ "\"y\" : 0.0"
						+ "},"
						+ "\"status\" : \"ACTIVE\","
						+ "\"laps\" : {"
								+ "\"count\" : 1,"
								+ "\"lapTime\" : 1.0"
							+ "},"
						+ "\"control\" : {"
							+ "\"speed\" : 1,"
							+ "\"steering\" : 90"
						+ "}"
					+ "},"
					+ "\"events\" : ["
						+ "{"
							+ "\"start\" : {\"x\" : 0.0, \"y\" : 0.0},"
							+ "\"end\" : {\"x\" : 1.0, \"y\" : 0.0},"
							+ "\"start_time\" : 1.0,"
							+ "\"end_time\" : 1.5"
						+ "},"
						+ "{"
							+ "\"start\" : {\"x\" : 1.0, \"y\" : 0.0},"
							+ "\"end\" : {\"x\" : 2.0, \"y\" : 0.0},"
							+ "\"start_time\" : 1.5,"
							+ "\"end_time\" : 2.0"
						+ "}"
					+ "]"
				+ "}";
		
		assertEquals(jsonFrom(expected), actual);
	}
	
	@Test
	public void testTimeoutResponse() throws ServletException, IOException {
		JsonStructure actual = processor.timeoutResponse(asyncRequest, 4, 5);
		
		String expected = "{ \"serverTime\" : 5}";
		
		assertEquals(jsonFrom(expected), actual);
		
	}
	
	@Test
	public void testProcess() throws ServletException, IOException {
		List<JsonStructure> actual = processor.process(ImmutableList.of(clockedRequest_1, clockedRequest_2));
		
		String expectedItem =
				"{"
					+ "\"id\" : \"%s\","
					+ "\"state\" : {"
						+ "\"position\" : {"
							+ "\"x\" : 0.0," // according to #setupRequests
							+ "\"y\" : 0.0"
						+ "},"
						+ "\"status\" : \"ACTIVE\","
						+ "\"laps\" : {"
								+ "\"count\" : 1,"
								+ "\"lapTime\" : 1.0"
							+ "},"
						+ "\"control\" : {"
							+ "\"speed\" : 1,"
							+ "\"steering\" : 90"
						+ "}"
					+ "},"
					+ "\"eventData\" : {"
						+ "\"1\" : ["
							+ "{"
								+ "\"start\" : {\"x\" : 0.0, \"y\" : 0.0},"
								+ "\"end\" : {\"x\" : 1.0, \"y\" : 0.0},"
								+ "\"start_time\" : 1.0,"
								+ "\"end_time\" : 1.5"
							+ "},"
							+ "{"
								+ "\"start\" : {\"x\" : 1.0, \"y\" : 0.0},"
								+ "\"end\" : {\"x\" : 2.0, \"y\" : 0.0},"
								+ "\"start_time\" : 1.5,"
								+ "\"end_time\" : 2.0"
							+ "}"
						+ "],"
						+ "\"2\" : ["
							+ "{"
								+ "\"start\" : {\"x\" : 0.0, \"y\" : 0.0},"
								+ "\"end\" : {\"x\" : 1.0, \"y\" : 0.0},"
								+ "\"start_time\" : 1.0,"
								+ "\"end_time\" : 1.5"
							+ "},"
							+ "{"
								+ "\"start\" : {\"x\" : 1.0, \"y\" : 0.0},"
								+ "\"end\" : {\"x\" : 2.0, \"y\" : 0.0},"
								+ "\"start_time\" : 1.5,"
								+ "\"end_time\" : 2.0"
							+ "}"
						+ "]"
					+ "}"
				+ "}";
		
		JsonStructure expectedJsonItem_1 = jsonFrom(String.format(expectedItem, "1"));
		JsonStructure expectedJsonItem_2 = jsonFrom(String.format(expectedItem, "2"));
		
		assertEquals(ImmutableList.of(expectedJsonItem_1, expectedJsonItem_2), actual);
	}
	
	@Test
	public void testProcessWithRedirect() throws ServletException, IOException {
		when(clockedRequest_1.getTime()).thenReturn(15);
		when(clockedRequest_2.getTime()).thenReturn(15);
		
		List<JsonStructure> actual = processor.process(ImmutableList.of(clockedRequest_1, clockedRequest_2));
		
		String expectedItem =
				"{"
					+ "\"id\" : \"%s\","
					+ "\"redirect\" : \"" + HANDLER_ID.toString() + "\","
					+ "\"state\" : {"
						+ "\"position\" : {"
							+ "\"x\" : 0.0," // according to #setupRequests
							+ "\"y\" : 0.0"
						+ "},"
						+ "\"status\" : \"ACTIVE\","
						+ "\"laps\" : {"
								+ "\"count\" : 1,"
								+ "\"lapTime\" : 1.0"
							+ "},"
						+ "\"control\" : {"
							+ "\"speed\" : 1,"
							+ "\"steering\" : 90"
						+ "}"
					+ "},"
					+ "\"eventData\" : {"
						+ "\"1\" : ["
							+ "{"
								+ "\"start\" : {\"x\" : 0.0, \"y\" : 0.0},"
								+ "\"end\" : {\"x\" : 1.0, \"y\" : 0.0},"
								+ "\"start_time\" : 1.0,"
								+ "\"end_time\" : 1.5"
							+ "},"
							+ "{"
								+ "\"start\" : {\"x\" : 1.0, \"y\" : 0.0},"
								+ "\"end\" : {\"x\" : 2.0, \"y\" : 0.0},"
								+ "\"start_time\" : 1.5,"
								+ "\"end_time\" : 2.0"
							+ "}"
						+ "],"
						+ "\"2\" : ["
							+ "{"
								+ "\"start\" : {\"x\" : 0.0, \"y\" : 0.0},"
								+ "\"end\" : {\"x\" : 1.0, \"y\" : 0.0},"
								+ "\"start_time\" : 1.0,"
								+ "\"end_time\" : 1.5"
							+ "},"
							+ "{"
								+ "\"start\" : {\"x\" : 1.0, \"y\" : 0.0},"
								+ "\"end\" : {\"x\" : 2.0, \"y\" : 0.0},"
								+ "\"start_time\" : 1.5,"
								+ "\"end_time\" : 2.0"
							+ "}"
						+ "]"
					+ "}"
				+ "}";
		
		JsonStructure expectedJsonItem_1 = jsonFrom(String.format(expectedItem, "1"));
		JsonStructure expectedJsonItem_2 = jsonFrom(String.format(expectedItem, "2"));
		
		assertEquals(ImmutableList.of(expectedJsonItem_1, expectedJsonItem_2), actual);
	}
	
	private JsonStructure jsonFrom(String expected) {
		return Json.createReader(new StringReader(expected)).read();
	}
}