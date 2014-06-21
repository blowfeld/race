package thomasb.race.engine;

import static java.lang.Double.compare;
import static java.util.Objects.hash;

public final class PointDoubleImp implements PointDouble {
	private final double x;
	private final double y;
	
	public PointDoubleImp(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public double getX() {
		return x;
	}
	
	@Override
	public double getY() {
		return y;
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
