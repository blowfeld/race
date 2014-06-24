package thomasb.race.engine;

import static java.lang.Math.sqrt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static thomasb.race.engine.RaceMatchers.isCloseTo;

import org.junit.Before;
import org.junit.Test;

import thomasb.race.engine.Ray.HalfPlane;
import thomasb.race.engine.Ray.Intersection;

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
		assertThat(intersection.getIntersectionStart(), isCloseTo(points[10][10], PRECISION));;
	}
	
	@Test
	public void intersectWitPositiveDistance() {
		Intersection intersection = ray.getIntersection(points[9][15], points[11][15]);
		
		assertEquals(5.0, intersection.distance(), PRECISION);;
		assertThat(intersection.getIntersectionStart(), isCloseTo(points[10][15], PRECISION));;
	}
	
	@Test
	public void noIntersection() {
		Intersection intersection = ray.getIntersection(points[9][9], points[11][9]);
		
		assertNull(intersection);
	}
	
	@Test
	public void intersectLeftRightEqualsRightLeft() {
		Intersection leftRight = ray.getIntersection(points[11][15], points[9][15]);
		Intersection rightLeft = ray.getIntersection(points[11][15], points[9][15]);
		
		assertEquals(leftRight.distance(), rightLeft.distance(), 0.0);;
		assertThat(leftRight.getIntersectionStart(), isCloseTo(rightLeft.getIntersectionStart(), PRECISION));
	}
	
	@Test
	public void intersectWitDiagonal() {
		Intersection intersection = ray.getIntersection(points[9][9], points[11][11]);
		
		assertEquals(0.0, intersection.distance(), PRECISION);;
		assertThat(intersection.getIntersectionStart(), isCloseTo(points[10][10], PRECISION));;
	}
	
	@Test
	public void diagonalIntersectsWithPositiveDistance() {
		Ray diagonalRay = new Ray(points[10][10], 45);
		
		Intersection intersection = diagonalRay.getIntersection(points[9][11], points[12][11]);
		
		assertEquals(sqrt(2), intersection.distance(), PRECISION);
		assertThat(intersection.getIntersectionStart(), isCloseTo(points[11][11], PRECISION));;
	}
	
	@Test
	public void diagonalNoIntersection() {
		Ray diagonalRay = new Ray(points[10][10], 45);
		
		Intersection intersection = diagonalRay.getIntersection(points[8][9], points[10][9]);
		
		assertNull(intersection);
	}
	
	@Test
	public void pointsOnLineNoInteresection() {
		Intersection intersection = ray.getIntersection(points[10][8], points[10][9]);
		
		assertNull(intersection);
	}
	
	@Test
	public void pointsOnLinePartialIntersection() {
		Intersection intersection = ray.getIntersection(points[10][8], points[10][11]);
		
		assertEquals(0.0, intersection.distance(), PRECISION);
		assertThat(intersection.getIntersectionStart(), isCloseTo(points[10][10], PRECISION));;
		assertThat(intersection.getIntersectionEnd(), isCloseTo(points[10][11], PRECISION));;
	}
	
	@Test
	public void pointsOnLineFullIntersection() {
		Intersection intersection = ray.getIntersection(points[10][11], points[10][15]);
		
		assertEquals(1.0, intersection.distance(), PRECISION);
		assertThat(intersection.getIntersectionStart(), isCloseTo(points[10][11], PRECISION));;
		assertThat(intersection.getIntersectionEnd(), isCloseTo(points[10][15], PRECISION));;
	}
	
	@Test
	public void pointsOnLineFullIntersectionReverseOrder() {
		Intersection intersection = ray.getIntersection(points[10][15], points[10][11]);
		
		assertEquals(1.0, intersection.distance(), PRECISION);
		assertThat(intersection.getIntersectionStart(), isCloseTo(points[10][11], PRECISION));;
		assertThat(intersection.getIntersectionEnd(), isCloseTo(points[10][15], PRECISION));;
	}
	
	@Test
	public void samePointsDoNotIntersect() {
		Intersection intersection = ray.getIntersection(points[10][11], points[10][11]);
		
		assertNull(intersection);
	}
	
	@Test
	public void onePointsOnRayDoesNotIntersect() {
		Intersection intersection1 = ray.getIntersection(points[10][11], points[11][11]);
		Intersection intersection2 = ray.getIntersection(points[11][11], points[10][11]);
		
		assertNull(intersection1);
		assertNull(intersection2);
	}
	
	@Test
	public void parallelRay() {
		Intersection intersection = ray.getIntersection(points[9][9], points[9][11]);
		
		assertNull(intersection);
	}
}
