package thomasb.race.engine;

import static java.lang.Math.sqrt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class VectorPointTest {
	private static final double PRECISION = 0.000001;

	@Test
	public void fromDirection() {
		VectorPoint unitX = VectorPoint.fromDirection(90);
		
		assertEquals(1.0, unitX.getX(), PRECISION);
		assertEquals(0.0, unitX.getY(), PRECISION);
		
		VectorPoint unitSW = VectorPoint.fromDirection(210);
		
		assertEquals(-0.5, unitSW.getX(), PRECISION);
		assertEquals(-0.5 * sqrt(3), unitSW.getY(), PRECISION);
	}
	
	@Test
	public void addition() {
		VectorPoint unitX = new VectorPoint(1.0, 0.0);
		VectorPoint unitY = new VectorPoint(0.0, 1.0);
		
		VectorPoint sum = unitX.add(unitY);
		
		assertEquals(1.0, sum.getX(), 0.0);
		assertEquals(1.0, sum.getY(), 0.0);
		
		assertEquals(unitY.add(unitX), unitY.add(unitX));
	}
	
	@Test
	public void subtraction() {
		VectorPoint unitX = new VectorPoint(1.0, 0.0);
		VectorPoint unitY = new VectorPoint(0.0, 1.0);
		
		VectorPoint sum = unitX.diff(unitY);
		
		assertEquals(1.0, sum.getX(), 0.0);
		assertEquals(-1.0, sum.getY(), 0.0);
		
		assertEquals(unitY.add(unitX), unitY.add(unitX));
	}
	
	@Test
	public void multiplication() {
		VectorPoint point = new VectorPoint(1.0, 0.5);
		
		VectorPoint stretched = point.multiply(2);
		
		assertEquals(2.0, stretched.getX(), 0.0);
		assertEquals(1.0, stretched.getY(), 0.0);
	}
	
	@Test
	public void dotPerpendicular() {
		VectorPoint unitX = new VectorPoint(1.0, 0.0);
		VectorPoint unitY = new VectorPoint(0.0, 1.0);
		
		double product = unitX.dot(unitY);
		
		assertEquals(0.0, product, 0.0);
		assertEquals(unitX.dot(unitY), unitY.dot(unitX), 0.0);
	}
	
	@Test
	public void dot() {
		VectorPoint unitX = new VectorPoint(1.0, 2.0);
		VectorPoint unitY = new VectorPoint(2.0, 3.0);
		
		double product = unitX.dot(unitY);
		
		assertEquals(8.0, product, PRECISION);
		assertEquals(unitX.dot(unitY), unitY.dot(unitX), PRECISION);
	}
	
	@Test
	public void closePointsAreClose() {
		VectorPoint a = new VectorPoint(1.0, 2.0);
		VectorPoint b = new VectorPoint(1.000001, 2.000001);
		
		assertTrue(a.isClose(b, 0.0001));
	}
	
	@Test
	public void distantPointsAreNotClose() {
		VectorPoint a = new VectorPoint(1.0, 2.0);
		VectorPoint b = new VectorPoint(1.0001001, 2.0);
		
		assertFalse(a.isClose(b, 0.0001));
	}
}
