package thomasb.race.app.handlers;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static thomasb.race.engine.PlayerStatus.ACTIVE;
import static thomasb.race.engine.RaceMatchers.isCloseTo;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
	private static final double PRECISION = 1e-10;
	private static final UUID HANDLER_ID = UUID.randomUUID();
	private static final JsonString ID_1 = Json.createArrayBuilder().add("1").build().getJsonString(0);
	private static final JsonString ID_2 = Json.createArrayBuilder().add("2").build().getJsonString(0);
	
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
	
	@Mock HttpServletRequest initRequest;
	@Mock HttpServletRequest request;
	@Mock HttpServletResponse response;
	@Mock AsyncContext asyncRequest;
	@Mock HttpSession session;
	
	ClockedRequest<RaceData> clockedRequest_1;
	ClockedRequest<RaceData> clockedRequest_2;
	
	private RaceProcessor processor;
	
	@Before
	public void setup() {
		setupPoints();
		setupRequests();
		setupClockedRequests();
		setupStates();
		setupSegments();
		setupTrack();
		setupEngine();
		setupProcessor();
	}
	
	public void setupPoints() {
		when(point_0_0.getX()).thenReturn(0.0);
		when(point_0_0.getY()).thenReturn(0.0);
		
		when(point_1_0.getX()).thenReturn(1.0);
		when(point_1_0.getY()).thenReturn(0.0);
		
		when(point_2_0.getX()).thenReturn(2.0);
		when(point_2_0.getY()).thenReturn(0.0);
	}
	
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
	
	public void setupRequests() {
		when(session.getId()).thenReturn("1");
		when(initRequest.getSession()).thenReturn(session);
		
		String baseRequestDataString =
				"{"
						+ "\"id\" : \"1\","
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
						+ "\"command\" : 37"
				+ "}";
		
		String requestDataJson = String.format(baseRequestDataString, "1");
		when(request.getParameter(ClockedRequest.DATA_PARAMETER))
				.thenReturn(requestDataJson);
		
		when(asyncRequest.getRequest()).thenReturn(request);
	}
	
	@SuppressWarnings("unchecked")
	public void setupClockedRequests() {
		clockedRequest_1 = mock(ClockedRequest.class);
		clockedRequest_2 = mock(ClockedRequest.class);
		
		when(clockedRequest_1.getData()).thenReturn(new RaceData(ID_1, path));
		when(clockedRequest_2.getData()).thenReturn(new RaceData(ID_2, path));
	}
	
	public void setupTrack() {
		Iterable<? extends PointDouble> gridPoints = ImmutableList.of(point_0_0, point_1_0, point_2_0);
		Mockito.<Iterable<? extends PointDouble>>when(track.getStartGrid()).thenReturn(gridPoints);
	}
	
	public void setupEngine() {
		when(path.getEndState()).thenReturn(endState);
		Mockito.<List<? extends PathSegment>>when(path.getSegments())
				.thenReturn(ImmutableList.of(segment_1, segment_2));
		
		when(engine.calculatePath(any(PlayerState.class),
				doubleThat(comparesEqualTo(1.0)), doubleThat(comparesEqualTo(1.0))))
				.thenReturn(path);
	}
	
	public void setupProcessor() {
		when(scoreHandler.getId()).thenReturn(HANDLER_ID);
		
		List<String> participants = ImmutableList.of("1", "2");
		
		processor = new RaceProcessor(participants, track , engine, new RaceJsonConverter(), scoreHandler, 10);
	}
	
	@Test
	public void testInitData() {
		JsonStructure actual = processor.initalData(initRequest);
		
		String expected =
				"{"
					+ "\"id\" : \"1\","
					+ "\"participants\" : [\"1\", \"2\"],"
					+ "\"grid\" : {"
							+ "\"1\" : {\"x\" : 0.0, \"y\" : 0.0},"
							+ "\"2\" : {\"x\" : 1.0, \"y\" : 0.0}"
						+ "}"
				+ "}";
		
		assertEquals(jsonFrom(expected), actual);
	}
	
	@Test
	public void testPreprocess() throws ServletException, IOException {
		RaceData actual = processor.preprocess(asyncRequest, 1);
		
		assertEquals(ID_1, actual.getJsonId());
		assertThat(actual.getPath().getEndState().getPosition(), isCloseTo(point_2_0, PRECISION));
		assertEquals(actual.getPath().getEndState().getPlayerStatus(), ACTIVE);
		assertEquals(actual.getPath().getEndState().getControlState().getSpeed(), 1);
		assertEquals(actual.getPath().getEndState().getControlState().getSteering(), 90);
		assertEquals(actual.getPath().getEndState().getLaps().getCount(), 1);
		assertEquals(actual.getPath().getEndState().getLaps().getLapTime(), 1.0, PRECISION);
		assertThat(actual.getPath().getSegments().get(0), isCloseTo(segment_1, PRECISION));
		assertThat(actual.getPath().getSegments().get(1), isCloseTo(segment_2, PRECISION));
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
