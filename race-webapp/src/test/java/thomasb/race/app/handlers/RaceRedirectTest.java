package thomasb.race.app.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static thomasb.race.engine.PlayerStatus.ACTIVE;
import static thomasb.race.engine.PlayerStatus.FINISHED;
import static thomasb.race.engine.PlayerStatus.TERMINATED;

import java.util.Collections;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonString;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import thomasb.race.engine.PlayerState;
import thomasb.race.engine.PlayerStatus;
import thomasb.race.engine.RacePath;
import thomasb.web.clocking.ClockedRequest;
import thomasb.web.handler.RequestHandler;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class RaceRedirectTest {
	private static final UUID HANDLER_ID = UUID.randomUUID();
	private static final JsonString JSON_ID = Json.createArrayBuilder().add(HANDLER_ID.toString()).build().getJsonString(0);
	
	@Mock RequestHandler handler;
	@Mock RacePath path;
	
	ClockedRequest<RaceData> request_1;
	ClockedRequest<RaceData> request_2;
	
	PlayerState state;
	
	@Before
	public void setupHandler() {
		when(handler.getId()).thenReturn(HANDLER_ID);
	}
	
	@SuppressWarnings("unchecked")
	public void setupRequest(int time, PlayerStatus status) {
		request_1 = mock(ClockedRequest.class);
		request_2 = mock(ClockedRequest.class);
		
		when(request_1.getTime()).thenReturn(time);
		when(request_2.getTime()).thenReturn(time);
		
		state = mock(PlayerState.class);
		when(state.getPlayerStatus()).thenReturn(status);
		when(path.getEndState()).thenReturn(state);
		when(request_1.getData()).thenReturn(new RaceData(JSON_ID, path));
		when(request_2.getData()).thenReturn(new RaceData(JSON_ID, path));
	}
	
	@Test
	public void urlIsNullWhenActiveInTime() {
		setupRequest(1, ACTIVE);
		
		RaceRedirect redirect = new RaceRedirect(handler, 10);
		String actual = redirect.url(ImmutableList.of(request_1, request_2));
		
		assertNull(actual);
	}
	
	@Test
	public void urlIsNullIfNoRequests() {
		RaceRedirect redirect = new RaceRedirect(handler, 10);
		String actual = redirect.url(Collections.<ClockedRequest<RaceData>>emptyList());
		
		assertNull(actual);
	}
	
	@Test
	public void urlIsNullAtMaxTime() {
		setupRequest(10, ACTIVE);
		
		RaceRedirect redirect = new RaceRedirect(handler, 10);
		String actual = redirect.url(ImmutableList.of(request_1, request_2));
		
		assertNull(actual);
	}
	
	@Test
	public void urlIfGreaterMaxTime() {
		setupRequest(11, ACTIVE);
		
		RaceRedirect redirect = new RaceRedirect(handler, 10);
		String actual = redirect.url(ImmutableList.of(request_1, request_2));
		
		assertEquals(HANDLER_ID.toString(), actual);
	}
	
	@Test
	public void urlIfAllFinished() {
		setupRequest(1, FINISHED);
		
		RaceRedirect redirect = new RaceRedirect(handler, 10);
		String actual = redirect.url(ImmutableList.of(request_1, request_2));
		
		assertEquals(HANDLER_ID.toString(), actual);
	}
	
	@Test
	public void urlIfAllTerminated() {
		setupRequest(1, TERMINATED);
		
		RaceRedirect redirect = new RaceRedirect(handler, 10);
		String actual = redirect.url(ImmutableList.of(request_1, request_2));
		
		assertEquals(HANDLER_ID.toString(), actual);
	}
}
