package thomasb.race.engine;

import static org.junit.Assert.assertThat;
import static thomasb.race.engine.RacePathMatcher.isCloseTo;

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
		PointDouble startPoint = new VectorPoint(1.0, 2.0);
		
		RacePath actualPath = engine.calculatePath(startPoint, currentState, 1);
		
		List<PathSegmentImp> expectedSegment = ImmutableList.of(new PathSegmentImp(startPoint, startPoint));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertThat(actualPath, isCloseTo(expectedPath));
	}
	
	@Test
	public void pathOnAsphaltOnlyIsFast() {
		ControlState currentState = Mocks.controlState(2, 0);
		PointDouble startPoint = new VectorPoint(0.0, 0.0);
		
		RacePath actualPath = engine.calculatePath(startPoint, currentState, 1);
		
		PointDouble expectedEnd = new VectorPoint(0, 2.0);
		List<PathSegmentImp> expectedSegment = ImmutableList.of(new PathSegmentImp(startPoint, expectedEnd));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertThat(actualPath, isCloseTo(expectedPath));
	}
}
