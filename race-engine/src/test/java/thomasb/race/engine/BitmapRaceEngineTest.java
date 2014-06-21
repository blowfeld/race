package thomasb.race.engine;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class BitmapRaceEngineTest {
	private RaceEngine engine;
	
	
	@Before
	public void setupControls() {
	}
	
	@Before
	public void setupEngine() {
		engine = new BitmapRaceEngine();
	}
	
	@Test
	public void pathStaysInPlaceIfNotMooving() {
		ControlState currentState = Mocks.controlState(0, 90);
		PointDouble startPoint = new PointDoubleImp(1.0, 2.0);
		
		RacePath actualPath = engine.calculatePath(startPoint, currentState, 1);
		
		List<PathSegmentImp> expectedSegment = ImmutableList.of(new PathSegmentImp(startPoint, startPoint));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertEquals(expectedPath, actualPath);
	}
}
