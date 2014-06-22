package thomasb.race.engine;

import static java.lang.Math.sqrt;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static thomasb.race.engine.RacePathMatcher.isCloseTo;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class RaceEngineImpTest {
	@Mock PointDouble point_0_0;
	@Mock PointDouble point_0_1;
	@Mock PointDouble point_0_2;
	@Mock PointDouble point_1_0;
	@Mock PointDouble point_1_1;
	@Mock PointDouble point_1_2;
	@Mock PointDouble point_2_0;
	@Mock PointDouble point_2_2;
	@Mock PointDouble point_10_0;
	
	@Mock RaceTrack raceTrack;
	
	private RaceEngine engine;

	@Before
	public void setupPoints() {
		setupPoint(point_0_0, 0.0, 0.0);
		setupPoint(point_0_1, 0.0, 1.0);
		setupPoint(point_0_2, 0.0, 2.0);
		setupPoint(point_1_0, 1.0, 0.0);
		setupPoint(point_1_1, 1.0, 1.0);
		setupPoint(point_1_2, 1.0, 2.0);
		setupPoint(point_2_0, 2.0, 0.0);
		setupPoint(point_2_2, 2.0, 2.0);
		setupPoint(point_10_0, 10.0, 0.0);
	}
	
	public void setupPoint(PointDouble point, double x, double y) {
		when(point.getX()).thenReturn(x);
		when(point.getY()).thenReturn(y);
	}
	
	@Before
	public void setupTrack() {
		TrackSegment asphaltVert = new RaceTrackSegment(
				point_0_0 , point_0_1, 2, false, false);
		
		TrackSegment greenVert = new RaceTrackSegment(
				point_0_1 , point_0_2, 1, false, false);
		
		TrackSegment wallVert = new RaceTrackSegment(
				point_0_2 , point_0_2, 1, false, true);
		
		when(raceTrack.partitions(point_0_0, 0))
			.thenReturn(ImmutableList.of(asphaltVert, greenVert, wallVert));
		
		TrackSegment asphaltHor = new RaceTrackSegment(
				point_0_0 , point_10_0, 2, false, false);
		
		when(raceTrack.partitions(point_0_0, 90))
			.thenReturn(ImmutableList.of(asphaltHor));
		
		TrackSegment asphaltDiag = new RaceTrackSegment(
				point_0_0 , point_2_2, 2, false, false);
		
		when(raceTrack.partitions(point_0_0, 45))
			.thenReturn(ImmutableList.of(asphaltDiag));
	}
	
	@Before
	public void setupEngine() {
		engine = new RaceEngineImp(raceTrack);
	}
	
	@Test
	public void pathStaysInPlaceIfNotMooving() {
		ControlState controlState = Mocks.controlState(0, 90);
		PointDouble startPoint = point_1_2;
		
		RacePath actualPath = engine.calculatePath(startPoint, 0.0, 1.1, controlState);
		
		List<RacePathSegment> expectedSegment = ImmutableList.of(
				new RacePathSegment(startPoint, startPoint, 0.0, 1.0));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertThat(actualPath, isCloseTo(expectedPath));
	}
	
	@Test
	public void pathOnAsphaltOnlyIsFast() {
		ControlState currentState = Mocks.controlState(2, 90);
		PointDouble startPoint = point_0_0;
		
		RacePath actualPath = engine.calculatePath(startPoint, 0.0, 1.0, currentState);
		
		PointDouble expectedEnd = point_2_0;
		List<RacePathSegment> expectedSegment = ImmutableList.of(
				new RacePathSegment(startPoint, expectedEnd, 0.0, 1.0));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertThat(actualPath, isCloseTo(expectedPath));
	}
	
	@Test
	public void pathFollowsDirection() {
		ControlState currentState = Mocks.controlState(1, 45);
		PointDouble startPoint = point_0_0;
		
		RacePath actualPath = engine.calculatePath(startPoint, 0.0, sqrt(2), currentState);
		
		PointDouble expectedEnd = point_1_1;
		List<RacePathSegment> expectedSegment = ImmutableList.of(
				new RacePathSegment(startPoint, expectedEnd, 0.0, sqrt(2)));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertThat(actualPath, isCloseTo(expectedPath));
	}
	
	@Test
	public void pathIntersectsGreenSlowsDown() {
		ControlState currentState = Mocks.controlState(2, 0);
		PointDouble startPoint = point_0_0;
		
		RacePath actualPath = engine.calculatePath(startPoint, 0.0, 1.5, currentState);
		
		PointDouble expectedAsphaltEnd = point_0_1;
		RacePathSegment expectedAsphaltSegment = 
				new RacePathSegment(startPoint, expectedAsphaltEnd, 0.0, 0.5);
		
		PointDouble expectedGreenEnd = point_0_2;
		RacePathSegment expectedGreenSegment = 
				new RacePathSegment(expectedAsphaltEnd, expectedGreenEnd, 0.5, 1.5);

		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE,
				ImmutableList.of(expectedAsphaltSegment, expectedGreenSegment));
		
		assertThat(actualPath, isCloseTo(expectedPath));
	}
}
