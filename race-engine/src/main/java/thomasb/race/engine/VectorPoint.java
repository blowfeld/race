package thomasb.race.engine;

import static java.lang.Double.compare;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import static java.util.Objects.hash;

public final class VectorPoint implements PointDouble {
	private final double x;
	private final double y;
	
	public VectorPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	static VectorPoint from(PointDouble point) {
		return point instanceof VectorPoint ?
			(VectorPoint) point :
			new VectorPoint(point.getX(), point.getY());
	}
	
	static VectorPoint fromDirection(int direction) {
		return new VectorPoint(sin(toRadians(direction)),
				cos(toRadians(direction)));
	}
	
	@Override
	public double getX() {
		return x;
	}
	
	@Override
	public double getY() {
		return y;
	}
	
	VectorPoint add(PointDouble other) {
		return new VectorPoint(x + other.getX(), y + other.getY());
	}
	
	VectorPoint diff(PointDouble other) {
		return new VectorPoint(x - other.getX(), y - other.getY());
	}
	
	VectorPoint multiply(double lambda) {
		return new VectorPoint(lambda * x, lambda * y);
	}
	
	double dot(PointDouble other) {
		return x * other.getX() + y * other.getY();
	}
	
	boolean isClose(PointDouble other, double precision) {
		VectorPoint delta = this.diff(other);
		
		return sqrt(delta.dot(delta)) <= precision;
	}
	
	@Override
	public int hashCode() {
		return hash(x, y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof PointDouble)) {
			return false;
		}
		
		PointDouble other = (PointDouble) obj;
		return (compare(x, other.getX()) == 0) &&
				(compare(y, other.getY()) == 0);
	}
	
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
}
