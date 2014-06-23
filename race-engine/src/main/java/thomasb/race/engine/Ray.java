package thomasb.race.engine;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Double.compare;


class Ray {
	enum HalfPlane { LEFT, RIGHT, ON_RAY }
	
	private final VectorPoint startPoint;
	private final VectorPoint rayVector;

	Ray(PointDouble startPoint, int direction) {
		this.startPoint = VectorPoint.from(startPoint);
		this.rayVector = VectorPoint.fromDirection(direction);
	}
	
	HalfPlane detectHalfPlane(PointDouble point) {
		VectorPoint diff = VectorPoint.from(point).diff(startPoint);
		
		double signedArea = rayVector.signedArea(diff);
		
		if (compare(signedArea, 0.0) == 0) {
			return HalfPlane.ON_RAY;
		}
		
		return signedArea > 0 ? HalfPlane.LEFT : HalfPlane.RIGHT;
	}

	/**
	 * Calculate the intersection point with the ray specified by the given points.
	 * <p>
	 * With x as the rayVector, y_z = point_1 - point2, z_x = point2 - startPoint, a = distance: 
	 * <p>
	 * a * x + b * y_z + z_x = 0
	 * <p>
	 * => (x.T, y.T) dot (a,b).T = x_z
	 * <p>
	 * => (a,b).T = (x.T, y.T).inv dot x_z
	 * 
	 * @param point1
	 * @param point2
	 * 
	 * @return intersection point
	 */
	Ray.Intersection getIntersection(PointDouble point1, PointDouble point2) {
		VectorPoint x = rayVector;
		VectorPoint y_z = VectorPoint.from(point2).diff(point1);
		VectorPoint x_z = VectorPoint.from(point2).diff(startPoint);
		
		double inverse_0_0 = y_z.getY();
		double inverse_0_1 = - y_z.getX();
		
		double normalization = 1 / (x.getX() * y_z.getY() - y_z.getX() * x.getY());
		double distance = normalization * (inverse_0_0 * x_z.getX() + inverse_0_1 * x_z.getY());
		
		return new Intersection(distance);
	}
	
	Ray.Intersection pointOnRay(PointDouble pointOnRay) {
		checkArgument(detectHalfPlane(pointOnRay) == HalfPlane.ON_RAY, "point is not on the ray: %s", pointOnRay);
		
		return new Intersection(pointOnRay);
	}
	
	
	class Intersection {
		private final double distance;
		
		Intersection(PointDouble pointOnRay) {
			this(startPoint.diff(pointOnRay).norm());
		}

		Intersection(double distance) {
			this.distance = distance;
		}
		
		double distance() {
			return distance;
		}
		
		VectorPoint startPoint() {
			return startPoint;
		}
		
		VectorPoint intersectionPoint() {
			return startPoint.add(rayVector.multiply(distance));
		}
	}
}