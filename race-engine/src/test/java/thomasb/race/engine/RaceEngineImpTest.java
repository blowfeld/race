package thomasb.race.engine;

import static java.lang.Math.sqrt;
import static org.junit.Assert.assertEquals;
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
	private static final double PRECISION = 1e-15;
	
	@Mock PointDouble point_0_0;
	@Mock PointDouble point_0_1;
	@Mock PointDouble point_0_2;
	@Mock PointDouble point_0_3;
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
		setupPoint(point_0_3, 0.0, 3.0);
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
		
		TrackSegment asphaltVert2 = new RaceTrackSegment(
				point_0_2 , point_0_3, 2, false, true);
		
		when(raceTrack.partitions(point_0_0, 0))
			.thenReturn(ImmutableList.of(asphaltVert, greenVert, asphaltVert2));
		
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
		
		assertThat(actualPath, isCloseTo(expectedPath, PRECISION));
	}
	
	@Test
	public void pathOnAsphaltOnlyIsFast() {
		ControlState currentState = Mocks.controlState(2, 90);
		PointDouble startPoint = point_0_0;
		
		RacePath actualPath = engine.calculatePath(startPoint, 0.0, 1.0, currentState);
		
		List<RacePathSegment> expectedSegment = ImmutableList.of(
				new RacePathSegment(startPoint, point_2_0, 0.0, 1.0));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertThat(actualPath, isCloseTo(expectedPath, PRECISION));
	}
	
	@Test
	public void pathFollowsDirection() {
		ControlState currentState = Mocks.controlState(1, 45);
		PointDouble startPoint = point_0_0;
		
		RacePath actualPath = engine.calculatePath(startPoint, 0.0, sqrt(2), currentState);
		
		List<RacePathSegment> expectedSegment = ImmutableList.of(
				new RacePathSegment(startPoint, point_1_1, 0.0, sqrt(2)));
		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE, expectedSegment);
		
		assertThat(actualPath, isCloseTo(expectedPath, PRECISION));
	}
	
	@Test
	public void pathSlowsDownOnGreen() {
		ControlState currentState = Mocks.controlState(2, 0);
		PointDouble startPoint = point_0_0;
		
		RacePath actualPath = engine.calculatePath(startPoint, 0.0, 1.5, currentState);
		
		RacePathSegment expectedAsphaltSegment = 
				new RacePathSegment(startPoint, point_0_1, 0.0, 0.5);
		
		RacePathSegment expectedGreenSegment = 
				new RacePathSegment(point_0_1, point_0_2, 0.5, 1.5);

		RacePath expectedPath = new RacePathImp(PlayerStatus.ACTIVE,
				ImmutableList.of(expectedAsphaltSegment, expectedGreenSegment));
		
		assertThat(actualPath, isCloseTo(expectedPath, PRECISION));
	}
	
	@Test
	public void pathSpeedsUpAfterGreen() {
		ControlState currentState = Mocks.controlState(2, 0);
		PointDouble startPoint = point_0_0;
		
		RacePath actualPath = engine.calculatePath(startPoint, 0.0, 2.0, currentState);
		
		RacePathSegment expectedEndSegment = 
				new RacePathSegment(point_0_2, point_0_3, 1.5, 2.0);
		
		assertEquals(actualPath.getSegments().get(2), expectedEndSegment);
	}
}
