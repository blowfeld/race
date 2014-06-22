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
		ControlState controlState = Mocks.controlState(0, 90);
		PointDouble startPoint = new VectorPoint(1.0, 2.0);
		
		RacePath actualPath = engine.calculatePath(startPoint, 0.0, 1.1, controlState);
		
		List<RacePathSegment> expectedSegment = ImmutableList.of(
				new RacePathSegment(startPoint, startPoint, 0.0, 1.0));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertThat(actualPath, isCloseTo(expectedPath));
	}
	
	@Test
	public void pathOnAsphaltOnlyIsFast() {
		ControlState currentState = Mocks.controlState(2, 0);
		PointDouble startPoint = new VectorPoint(0.0, 0.0);
		
		RacePath actualPath = engine.calculatePath(startPoint, 0.0, 1.0, currentState);
		
		PointDouble expectedEnd = new VectorPoint(0, 2.0);
		List<RacePathSegment> expectedSegment = ImmutableList.of(
				new RacePathSegment(startPoint, expectedEnd, 0.0, 1.0));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertThat(actualPath, isCloseTo(expectedPath));
	}
	
	@Test
	public void pathFollowsDirection() {
		ControlState currentState = Mocks.controlState(1, 90);
		PointDouble startPoint = new VectorPoint(0.0, 0.0);
		
		RacePath actualPath = engine.calculatePath(startPoint, 0.0, 1.0, currentState);
		
		PointDouble expectedEnd = new VectorPoint(1.0, 0.0);
		List<RacePathSegment> expectedSegment = ImmutableList.of(
				new RacePathSegment(startPoint, expectedEnd, 0.0, 1.0));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertThat(actualPath, isCloseTo(expectedPath));
	}
	
	public void pathIntersectsGreen() {
		
	}
}
