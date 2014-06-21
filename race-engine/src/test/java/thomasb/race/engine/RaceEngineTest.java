package thomasb.race.engine;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import thomasb.race.engine.entities.ControlStateImp;
import thomasb.race.engine.entities.PathSegmentImp;
import thomasb.race.engine.entities.PointDoubleImp;
import thomasb.race.engine.entities.RacePathImp;

import com.google.common.collect.ImmutableList;

public class RaceEngineTest {
	private RaceEngine engine;
	
	@Mock private Speed speed_0;
	@Mock private Speed speed_1;
	@Mock private Speed speed_2;
	
	@Mock private Steering steer_left;
	@Mock private Steering steer_straight;
	@Mock private Steering steer_right;
	
	@Before
	public void setupControls() {
		when(speed_0.getSpeed()).thenReturn(0);
		when(speed_1.getSpeed()).thenReturn(1);
		when(speed_2.getSpeed()).thenReturn(2);
		
		when(steer_left.getDegrees()).thenReturn(45);
		when(steer_straight.getDegrees()).thenReturn(0);
		when(steer_right.getDegrees()).thenReturn(-45);
	}
	
	@Test
	public void calculatePathStaysInPlaceIfNotMooving() {
		ControlState currentState = new ControlStateImp(steer_left, speed_0);
		PointDouble startPoint = new PointDoubleImp(1.0, 2.0);
		
		RacePath actualPath = engine.calculatePath(startPoint,
				currentState,
				1);
		
		List<PathSegmentImp> expectedSegment = ImmutableList.of(new PathSegmentImp(startPoint, startPoint));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertEquals(expectedPath, actualPath);
	}
}
