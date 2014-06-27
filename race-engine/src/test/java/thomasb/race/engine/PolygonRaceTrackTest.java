package thomasb.race.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static thomasb.race.engine.RaceMatchers.isCloseTo;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class PolygonRaceTrackTest extends Test2D {
	private static final double PRECISION = 1e-10;
	
	private RaceTrack raceTrack;

	@Before
	public void setupTrack() {
		raceTrack = new PolygonRaceTrack(ImmutableList.of(
				new TrackPolygon(ImmutableList.of(
						points[8][12], points[8][8], points[12][8], points[12][12]),
						TrackType.WALL),
				new TrackPolygon(ImmutableList.of(
						points[6][14], points[6][6], points[14][6], points[14][14]),
						TrackType.GREEN),
				new TrackPolygon(ImmutableList.of(
						points[4][16], points[4][4], points[16][4], points[16][16]),
						TrackType.ASPHALT),
				new TrackPolygon(ImmutableList.of(
						points[2][18], points[2][2], points[18][2], points[18][18]),
						TrackType.GREEN),
				new TrackPolygon(ImmutableList.of(
						points[0][20], points[0][0], points[20][0], points[20][20]),
						TrackType.WALL)),
				points[2][10], points[8][10], Collections.<PointDouble>emptyList(), 1);
	}
	
	@Test
	public void xAxisFromAsphaltToCenter() {
		List<TrackSegment> actualSegments = raceTrack.segmentsFor(points[10][5], 0);
		
		List<TrackSegment> expectedSegments = ImmutableList.of(
				Mocks.trackSegment(points[10][5], points[10][6], 2, 0),
				Mocks.trackSegment(points[10][6], points[10][8], 1, 0),
				Mocks.trackSegment(points[10][8], points[10][12], 0, 0));
		
		assertSegments(expectedSegments, actualSegments);
	}
	
	@Test
	public void xAxisFromAsphaltToPeriphery() {
		List<TrackSegment> actualSegments = raceTrack.segmentsFor(points[10][15], 0);
		
		List<TrackSegment> expectedSegments = ImmutableList.of(
				Mocks.trackSegment(points[10][15], points[10][16], 2, 0),
				Mocks.trackSegment(points[10][16], points[10][18], 1, 0),
				Mocks.trackSegment(points[10][18], points[10][20], 0, 0));
		
		assertSegments(expectedSegments, actualSegments);
	}
	
	@Test
	public void startFromSectionBoundaryInside() {
		List<TrackSegment> actualSegments = raceTrack.segmentsFor(points[10][14], 0);
		
		List<TrackSegment> expectedSegments = ImmutableList.of(
				Mocks.trackSegment(points[10][14], points[10][16], 2, 0),
				Mocks.trackSegment(points[10][16], points[10][18], 1, 0),
				Mocks.trackSegment(points[10][18], points[10][20], 0, 0));
		
		assertSegments(expectedSegments, actualSegments);
	}
	
	@Test
	public void startFromSectionBoundaryOutside() {
		List<TrackSegment> actualSegments = raceTrack.segmentsFor(points[10][16], 0);
		
		List<TrackSegment> expectedSegments = ImmutableList.of(
				Mocks.trackSegment(points[10][16], points[10][18], 1, 0),
				Mocks.trackSegment(points[10][18], points[10][20], 0, 0));
		
		assertSegments(expectedSegments, actualSegments);
	}
	
	@Test
	public void startFromSectionBoundaryTouchBoundary() {
		List<TrackSegment> actualSegments = raceTrack.segmentsFor(points[4][9], 0);
		
		List<TrackSegment> expectedSegments = ImmutableList.of(
				Mocks.trackSegment(points[4][9], points[4][10], 2, 1),
				Mocks.trackSegment(points[4][10], points[4][16], 2, 0),
				Mocks.trackSegment(points[4][16], points[4][18], 1, 0),
				Mocks.trackSegment(points[4][18], points[4][20], 0, 0));
		
		assertSegments(expectedSegments, actualSegments);
	}
	
	@Test
	public void crossFinish() {
		List<TrackSegment> actualSegments = raceTrack.segmentsFor(points[5][9], 0);
		
		List<TrackSegment> expectedSegments = ImmutableList.of(
				Mocks.trackSegment(points[5][9], points[5][10], 2, 1),
				Mocks.trackSegment(points[5][10], points[5][16], 2, 0),
				Mocks.trackSegment(points[5][16], points[5][18], 1, 0),
				Mocks.trackSegment(points[5][18], points[5][20], 0, 0));
		
		assertSegments(expectedSegments, actualSegments);
	}
	
	@Test
	public void dontCrossFinishInWrongDirection() {
		List<TrackSegment> actualSegments = raceTrack.segmentsFor(points[5][11], 180);
		
		List<TrackSegment> expectedSegments = ImmutableList.of(
				Mocks.trackSegment(points[5][11], points[5][4], 2, -1),
				Mocks.trackSegment(points[5][4], points[5][2], 1, 0),
				Mocks.trackSegment(points[5][2], points[5][0], 0, 0));
		
		assertSegments(expectedSegments, actualSegments);
	}
	
	private void assertSegments(List<TrackSegment> expectedSegments,
			List<TrackSegment> actualSegments) {
		assertEquals(expectedSegments.size(), actualSegments.size());
		
		for (int i = 0; i < expectedSegments.size(); i++) {
			assertThat(actualSegments.get(i), isCloseTo(expectedSegments.get(i), PRECISION));
		}
	}
}
