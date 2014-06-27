package thomasb.race.engine;

import static java.lang.Math.sqrt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static thomasb.race.engine.RaceMatchers.isCloseTo;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class RaceEngineImpTest extends Test2D {
	private static final double PRECISION = 1e-15;
	
	@Mock RaceTrack raceTrack;

	private RaceLap zeroLaps = new RaceLap(0, 0.0);
	
	private RaceEngine engine;

	@Before
	public void setupTrack() {
		TrackSegment asphaltVert = new RaceTrackSegment(
				points[0][0] , points[0][1], 2, 0);
		
		TrackSegment greenVert = new RaceTrackSegment(
				points[0][1] , points[0][2], 1, 0);
		
		TrackSegment asphaltVert2 = new RaceTrackSegment(
				points[0][2] , points[0][3], 2, 1);
		
		TrackSegment asphaltVert3 = new RaceTrackSegment(
				points[0][3] , points[0][4], 2, 0);
		
		when(raceTrack.segmentsFor(points[0][0], 0))
			.thenReturn(ImmutableList.of(asphaltVert,
					greenVert,
					asphaltVert2,
					asphaltVert3));
		
		TrackSegment asphaltHor = new RaceTrackSegment(
				points[0][0] , points[10][0], 2, 0);
		
		TrackSegment wallHor = new RaceTrackSegment(
				points[10][0] , points[10][0], 0, 0);
		
		when(raceTrack.segmentsFor(points[0][0], 90))
			.thenReturn(ImmutableList.of(asphaltHor, wallHor));
		
		TrackSegment asphaltDiag = new RaceTrackSegment(
				points[0][0] , points[2][2], 2, 0);
		
		when(raceTrack.segmentsFor(points[0][0], 45))
			.thenReturn(ImmutableList.of(asphaltDiag));
	}
	
	@Before
	public void setupEngine() {
		engine = new RaceEngineImp(raceTrack);
	}
	
	@Test
	public void pathStaysInPlaceIfNotMooving() {
		ControlState controlState = Mocks.controlState(0, 90);
		PointDouble startPoint = points[1][2];
		
		RacePath actualPath = engine.calculatePath(createState(controlState, startPoint), 0.0, 1.1);
		
		List<RacePathSegment> expectedSegments = ImmutableList.of(
				new RacePathSegment(startPoint, startPoint, 0.0, 1.0));
		RacePath expectedPath = new RacePathImp(createState(controlState, startPoint), expectedSegments);
		
		assertThat(actualPath, isCloseTo(expectedPath, PRECISION));
	}
	
	@Test
	public void pathOnAsphaltOnlyIsFast() {
		ControlState currentState = Mocks.controlState(2, 90);
		PointDouble startPoint = points[0][0];
		
		RacePath actualPath = engine.calculatePath(createState(currentState, startPoint), 0.0, 1.0);
		
		List<RacePathSegment> expectedSegments = ImmutableList.of(
				new RacePathSegment(startPoint, points[2][0], 0.0, 1.0));
		RacePath expectedPath = new RacePathImp(createState(currentState, points[2][0]), expectedSegments);
		
		assertThat(actualPath, isCloseTo(expectedPath, PRECISION));
	}
	
	@Test
	public void pathFollowsDirection() {
		ControlState currentState = Mocks.controlState(1, 45);
		PointDouble startPoint = points[0][0];
		
		RacePath actualPath = engine.calculatePath(createState(currentState, startPoint), 0.0, sqrt(2));
		
		List<RacePathSegment> expectedSegments = ImmutableList.of(
				new RacePathSegment(startPoint, points[1][1], 0.0, sqrt(2)));
		RacePath expectedPath = new RacePathImp(createState(currentState, points[1][1]), expectedSegments);
		
		assertThat(actualPath, isCloseTo(expectedPath, PRECISION));
	}
	
	@Test
	public void pathSlowsDownOnGreen() {
		ControlState currentState = Mocks.controlState(2, 0);
		PointDouble startPoint = points[0][0];
		
		RacePath actualPath = engine.calculatePath(createState(currentState, startPoint), 0.0, 1.5);
		
		RacePathSegment expectedAsphaltSegment =
				new RacePathSegment(startPoint, points[0][1], 0.0, 0.5);
		
		RacePathSegment expectedGreenSegment =
				new RacePathSegment(points[0][1], points[0][2], 0.5, 1.5);

		RacePath expectedPath = new RacePathImp(createState(currentState, points[0][2]),
				ImmutableList.of(expectedAsphaltSegment, expectedGreenSegment));
		
		assertThat(actualPath, isCloseTo(expectedPath, PRECISION));
	}
	
	@Test
	public void pathSpeedsUpAfterGreen() {
		ControlState currentState = Mocks.controlState(2, 0);
		PointDouble startPoint = points[0][0];
		
		RacePath actualPath = engine.calculatePath(createState(currentState, startPoint), 0.0, 2.0);
		
		RacePathSegment expectedEndSegment =
				new RacePathSegment(points[0][2], points[0][3], 1.5, 2.0);
		
		assertThat(actualPath.getSegments().get(2), isCloseTo(expectedEndSegment, PRECISION));
	}
	
	@Test
	public void pathTerminatesAtWall() {
		ControlState currentState = Mocks.controlState(2, 90);
		PointDouble startPoint = points[0][0];
		
		RacePath actualPath = engine.calculatePath(createState(currentState, startPoint), 0.0, 10.0);
		
		RacePathSegment expectedAsphaltSegment =
				new RacePathSegment(points[0][0], points[10][0], 0.0, 5.0);
		RacePathSegment expectedWallSegment =
				new RacePathSegment(points[10][0], points[10][0], 5.0, 10.0);
		
		RacePath expectedPath = new RacePathImp(new RacePlayerState(points[10][0], currentState, zeroLaps, PlayerStatus.TERMINATED),
				ImmutableList.of(expectedAsphaltSegment, expectedWallSegment));
		
		assertThat(actualPath, isCloseTo(expectedPath, PRECISION));
	}
	
	@Test
	public void pathCrossesFinishIncrementsLap() {
		ControlState currentState = Mocks.controlState(1, 0);
		PointDouble startPoint = points[0][0];
		
		RacePath actualPath = engine.calculatePath(createState(currentState, startPoint), 0.0, 4.0);
		
		assertEquals(1, actualPath.getEndState().getLaps().getCount());
		assertEquals(3.0, actualPath.getEndState().getLaps().getLapTime(), PRECISION);
	}
	
	private PlayerState createState(ControlState control, PointDouble position) {
		return new RacePlayerState(position, control, zeroLaps, PlayerStatus.ACTIVE);
	}
}
