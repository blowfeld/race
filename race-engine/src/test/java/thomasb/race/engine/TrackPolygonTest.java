package thomasb.race.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static thomasb.race.engine.RaceMatchers.isCloseTo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
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
	
	private TrackPolygon trackPolygon;
	
	@Before
	public void setupPolygon() {
		List<PointDouble> corners = ImmutableList.of(
				points[5][5], points[5][15], points[15][15], points[15][5]);
		
		trackPolygon = new TrackPolygon(corners, TrackType.ASPHALT);
	}
	
	@Test
	public void rayStartingFromInsideIntersectsOnce() {
		PointDouble startPoint = points[10][10];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		assertPointsClose(actual, Lists.newArrayList(points[10][15]), startPoint);
	}
	
	@Test
	public void rayStartingFromOutsideIntersectsTwice() {
		PointDouble startPoint = points[10][0];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		List<PointDouble> expectedIntersections = Lists.newArrayList(
				points[10][15], points[10][5]);
		
		assertPointsClose(actual, expectedIntersections, startPoint);
	}
	
	@Test
	public void touchingRayDoesNotIntersect() {
		PointDouble startPoint = points[15][0];
		Ray ray = new Ray(startPoint, 0);
		
		List<Intersection> actual = trackPolygon.intersectionPoints(ray);
		
		List<PointDouble> expectedIntersections = Lists.newArrayList();
		
		assertPointsClose(actual, expectedIntersections, startPoint);
	}
	
	private void assertPointsClose(List<Intersection> actualIntersections, List<PointDouble> expected, PointDouble startPoint) {
		assertEquals(expected.size(), actualIntersections.size());
		
		List<Intersection> actual = Lists.newArrayList(actualIntersections);
		Collections.sort(actual, INTERSECTION_COMP);
		Collections.sort(expected, pointComp(VectorPoint.from(startPoint)));
		
		for (int i = 0; i < expected.size(); i++) {
			PointDouble expectedPoint = expected.get(i);
			Intersection actualIntersection = actual.get(i);
			
			assertThat(actualIntersection.intersectionPoint(), isCloseTo(expectedPoint, PRECISION));
			assertEquals(VectorPoint.from(expectedPoint).diff(startPoint).norm(), actualIntersection.distance(), PRECISION);
		}
	}
}
