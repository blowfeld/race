package thomasb.race.engine;

import static java.util.Collections.sort;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static thomasb.race.engine.RaceMatchers.isCloseTo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import thomasb.race.engine.Ray.Intersection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TrackPolygonTest extends Test2D {
	private static final double PRECISION = 1e-15;
	
	private static final Comparator<Intersection> INTERSECTION_COMP = new Comparator<Intersection>() {
		@Override
		public int compare(Intersection o1, Intersection o2) {
			return Double.compare(o1.distance(), o2.distance());
		}
	};
	
	private static Comparator<PointDouble> pointComp(final VectorPoint startPoint) {
		return new Comparator<PointDouble>() {
				@Override
				public int compare(PointDouble o1, PointDouble o2) {
					return Double.compare(startPoint.diff(o1).norm(),
							startPoint.diff(o2).norm());
				}
		};
	}
	
	public TrackPolygon setupPolygon(PointDouble... points) {
		List<PointDouble> corners = ImmutableList.copyOf(points);
		
		return new TrackPolygon(corners, TrackType.ASPHALT);
	}
	
	@Test
	public void rayStartingFromInsideIntersectsOnce() {
		TrackPolygon trackPolygon = setupPolygon(points[5][5], points[5][15], points[15][15], points[15][5]);
		
		PointDouble startPoint = points[10][10];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		assertPointsClose(actual, Lists.newArrayList(points[10][15]), startPoint);
	}
	
	@Test
	public void rayStartingFromOutsideIntersectsTwice() {
		TrackPolygon trackPolygon = setupPolygon(points[5][5], points[5][15], points[15][15], points[15][5]);
		
		PointDouble startPoint = points[10][0];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		List<PointDouble> expectedIntersections = Lists.newArrayList(
				points[10][15], points[10][5]);
		
		assertPointsClose(actual, expectedIntersections, startPoint);
	}
	
	@Test
	public void rayStartingFromOutsideIntersectsFirstCornerAndTwice() {
		TrackPolygon trackPolygon = setupPolygon(points[10][5], points[15][10], points[10][15], points[5][10]);
		
		PointDouble startPoint = points[10][0];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		List<PointDouble> expectedIntersections = Lists.newArrayList(
				points[10][5], points[10][15]);
		
		assertPointsClose(actual, expectedIntersections, startPoint);
	}
	
	@Test
	public void rayStartingFromOutsideIntersectsSecondCornerAndTwice() {
		TrackPolygon trackPolygon = setupPolygon(points[5][10], points[10][5], points[15][10], points[10][15]);
		
		PointDouble startPoint = points[10][0];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		List<PointDouble> expectedIntersections = Lists.newArrayList(
				points[10][5], points[10][15]);
		
		assertPointsClose(actual, expectedIntersections, startPoint);
	}
	
	@Test
	public void rayStartingFromOutsideIntersectsLastCornerAndTwice() {
		TrackPolygon trackPolygon = setupPolygon(points[15][10], points[10][15], points[5][10], points[10][5]);
		
		PointDouble startPoint = points[10][0];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		List<PointDouble> expectedIntersections = Lists.newArrayList(
				points[10][5], points[10][15]);
		
		assertPointsClose(actual, expectedIntersections, startPoint);
	}
	
	@Test
	public void rayStartingFromInsideIntersectsFirstCornerOnce() {
		TrackPolygon trackPolygon = setupPolygon(points[10][15], points[5][10], points[10][5], points[15][10]);
		
		PointDouble startPoint = points[10][10];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		List<PointDouble> expectedIntersections = Lists.newArrayList(points[10][15]);
		
		assertPointsClose(actual, expectedIntersections, startPoint);
	}
	
	@Test
	public void touchingRayIntersectsInLineSegmentStartOnFirstCorner() {
		TrackPolygon trackPolygon = setupPolygon(points[5][15], points[15][15], points[15][5], points[5][5]);
		
		PointDouble startPoint = points[15][0];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		List<PointDouble> expectedIntersections = Lists.newArrayList(
				points[15][5], points[15][15]);
		
		assertPointsClose(actual, expectedIntersections, startPoint);
	}
	
	@Test
	public void containsStartingPointOfRayStartingFromInside() {
		TrackPolygon trackPolygon = setupPolygon(points[5][5], points[5][15], points[15][15], points[15][5]);
		
		PointDouble startPoint = points[10][10];
		Ray ray = new Ray(startPoint, 0);
		
		assertTrue(trackPolygon.containsStartPoint(ray));
	}
	
	@Test
	public void containsStartingPointOfRayStartingOnBoundary() {
		TrackPolygon trackPolygon = setupPolygon(points[5][5], points[5][15], points[15][15], points[15][5]);
		
		PointDouble startPoint = points[10][15];
		Ray rayOutside = new Ray(startPoint, 0);
		
		assertFalse(trackPolygon.containsStartPoint(rayOutside));

		Ray rayInside = new Ray(startPoint, 180);
		
		assertTrue(trackPolygon.containsStartPoint(rayInside));
	}
	
	@Test
	public void doesNotContainStartingPointOfRayStartingFromOutside() {
		TrackPolygon trackPolygon = setupPolygon(points[5][5], points[5][15], points[15][15], points[15][5]);
		
		PointDouble startPoint = points[10][0];
		Ray ray = new Ray(startPoint, 0);
		
		assertFalse(trackPolygon.containsStartPoint(ray));
	}
	
	@Test
	public void touchingRayIntersectsInLineSegmentAndEntersPolygon() {
		TrackPolygon trackPolygon = setupPolygon(points[5][5],
				points[10][5],
				points[10][10],
				points[15][10],
				points[15][15],
				points[5][15]);
		
		PointDouble startPoint = points[10][0];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		List<PointDouble> expectedIntersections = Lists.newArrayList(
				points[10][5], points[10][10], points[10][15]);
		
		assertPointsClose(actual, expectedIntersections, startPoint);
		assertFalse(trackPolygon.containsStartPoint(ray));
	}
	
	@Test
	public void touchingRayIntersectsInLineSegmentAndLeavesPolygon() {
		TrackPolygon trackPolygon = setupPolygon(points[5][5],
				points[15][5],
				points[15][10],
				points[10][10],
				points[10][15],
				points[5][15]);
		
		PointDouble startPoint = points[10][8];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		List<PointDouble> expectedIntersections = Lists.newArrayList(
				points[10][10], points[10][15]);
		
		assertPointsClose(actual, expectedIntersections, startPoint);
		
		assertTrue(trackPolygon.containsStartPoint(ray));
	}
	
	@Test
	public void rayHasNoIntersection() {
		TrackPolygon trackPolygon = setupPolygon(
				points[5][5], points[5][15], points[15][15], points[15][5]);
		
		PointDouble startPoint = points[2][0];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		List<PointDouble> expectedIntersections = Collections.emptyList();
		
		assertPointsClose(actual, expectedIntersections, startPoint);
		assertFalse(trackPolygon.containsStartPoint(ray));
	}
	
	private void assertPointsClose(List<Intersection> actualIntersections, List<PointDouble> expected, PointDouble startPoint) {
		assertEquals(expected.size(), actualIntersections.size());
		
		List<Intersection> actual = Lists.newArrayList(actualIntersections);
		sort(actual, INTERSECTION_COMP);
		sort(expected, pointComp(VectorPoint.from(startPoint)));
		
		for (int i = 0; i < expected.size(); i++) {
			PointDouble expectedPoint = expected.get(i);
			Intersection actualIntersection = actual.get(i);
			
			assertThat(actualIntersection.getIntersectionStart(), isCloseTo(expectedPoint, PRECISION));
			assertEquals(VectorPoint.from(expectedPoint).diff(startPoint).norm(), actualIntersection.distance(), PRECISION);
		}
	}
}
