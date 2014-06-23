package thomasb.race.engine;

import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;
import static java.lang.Math.sqrt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static thomasb.race.engine.RaceMatchers.isCloseTo;

import org.junit.Before;
import org.junit.Test;

import thomasb.race.engine.Ray.HalfPlane;

public class RayTest extends Test2D {
	private static final double PRECISION = 1e-15;
	
	private Ray ray;

	@Before
	public void setupRay() {
		ray = new Ray(points[10][10], 0);
	}
	
	@Test
	public void leftHalfPlane() {
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 20; y++) {
				assertEquals(HalfPlane.LEFT, ray.detectHalfPlane(points[x][y]));
			}
		}
	}
	
	@Test
	public void rightHalfPlane() {
		for (int x = 11; x < 20; x++) {
			for (int y = 0; y < 20; y++) {
				assertEquals(HalfPlane.RIGHT, ray.detectHalfPlane(points[x][y]));
			}
		}
	}
	
	@Test
	public void onLine() {
		for (int y = 0; y < 20; y++) {
			assertEquals(HalfPlane.ON_RAY, ray.detectHalfPlane(points[10][y]));
		}
	}
	
	@Test
	public void diagonal() {
		Ray diagonalRay = new Ray(points[10][10], 45); 
		for (int x = 0; x < 20; x++) {
			for (int y = 0; y < 20; y++) {
				HalfPlane plane = null;
				if (x != y) { // ray does not touch integer grid points (sin & cos are irrational)
					plane = x > y ? HalfPlane.RIGHT : HalfPlane.LEFT;
					assertEquals(plane, diagonalRay.detectHalfPlane(points[x][y]));
				}
			}
		}
	}
	
	@Test
	public void intersectAtStart() {
		Intersection intersection = ray.getIntersection(points[5][10], points[15][10]);
		
		assertEquals(intersection.distance(), 0.0, PRECISION);;
		assertThat(intersection.intersectionPoint(), isCloseTo(points[10][10], PRECISION));;
	}
	
	@Test
	public void intersectWitPositiveDistance() {
		Intersection intersection = ray.getIntersection(points[9][15], points[11][15]);
		
		assertEquals(5.0, intersection.distance(), PRECISION);;
		assertThat(intersection.intersectionPoint(), isCloseTo(points[10][15], PRECISION));;
	}
	
	@Test
	public void intersectWitNegativeDistance() {
		Intersection intersection = ray.getIntersection(points[9][9], points[11][9]);
		
		assertEquals(-1.0, intersection.distance(), PRECISION);;
		assertThat(intersection.intersectionPoint(), isCloseTo(points[10][9], PRECISION));;
	}
	
	@Test
	public void intersectLeftRightEqualsRightLeft() {
		Intersection leftRight = ray.getIntersection(points[11][15], points[9][15]);
		Intersection rightLeft = ray.getIntersection(points[11][15], points[9][15]);
		
		assertEquals(leftRight.distance(), rightLeft.distance(), 0.0);;
		assertThat(leftRight.intersectionPoint(), isCloseTo(rightLeft.intersectionPoint(), PRECISION));
	}
	
	@Test
	public void intersectWitDiagonal() {
		Intersection intersection = ray.getIntersection(points[9][9], points[11][11]);
		
		assertEquals(0.0, intersection.distance(), PRECISION);;
		assertThat(intersection.intersectionPoint(), isCloseTo(points[10][10], PRECISION));;
	}
	
	@Test
	public void diagonalIntersectsWithPositiveDistance() {
		Ray diagonalRay = new Ray(points[10][10], 45);
		
		Intersection intersection = diagonalRay.getIntersection(points[9][11], points[12][11]);
		
		assertEquals(sqrt(2), intersection.distance(), PRECISION);;
		assertThat(intersection.intersectionPoint(), isCloseTo(points[11][11], PRECISION));;
	}
	
	@Test
	public void diagonalIntersectsWithNegativesDistance() {
		Ray diagonalRay = new Ray(points[10][10], 45);
		
		Intersection intersection = diagonalRay.getIntersection(points[8][9], points[10][9]);
		
		assertEquals(-sqrt(2), intersection.distance(), PRECISION);;
		assertThat(intersection.intersectionPoint(), isCloseTo(points[9][9], PRECISION));;
	}
	
	@Test
	public void pointsOnLine() {
		Intersection intersection = ray.getIntersection(points[10][9], points[10][9]);
		
		assertTrue(isNaN(intersection.distance()));
		assertTrue(isNaN(intersection.intersectionPoint().getX()));
		assertTrue(isNaN(intersection.intersectionPoint().getY()));
	}
	
	@Test
	public void parallelRay() {
		Intersection intersection = ray.getIntersection(points[9][9], points[9][11]);
		
		assertTrue(isInfinite(intersection.distance()));
		assertTrue(isNaN(intersection.intersectionPoint().getX()));
		assertTrue(isInfinite(intersection.intersectionPoint().getY()));
	}
}
