package thomasb.race.engine;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.signum;

import java.util.List;

import com.google.common.collect.ImmutableList;


class Ray {
	enum HalfPlane { LEFT, RIGHT, ON_RAY }
	enum IntersectionType { POINT, LINE_SEGMENT; }
	
	private final VectorPoint startPoint;
	private final VectorPoint rayVector;

	Ray(PointDouble startPoint, int direction) {
		this.startPoint = VectorPoint.from(startPoint);
		this.rayVector = VectorPoint.fromDirection(direction);
	}
	
	HalfPlane detectHalfPlane(PointDouble point) {
		VectorPoint diff = VectorPoint.from(point).diff(startPoint);
		
		double signedArea = rayVector.signedArea(diff);
		
		if (signedArea == 0.0) {
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
		if (point1.equals(point2)) {
			return null;
		}
		
		HalfPlane startPlane = detectHalfPlane(point1);
		HalfPlane endPlane = detectHalfPlane(point2);
		if (startPlane == endPlane && startPlane != HalfPlane.ON_RAY) {
			return null;
		}
		
		if (startPlane == HalfPlane.ON_RAY && endPlane == HalfPlane.ON_RAY) {
			VectorPoint startDiff = VectorPoint.from(point1).diff(startPoint);
			VectorPoint endDiff = VectorPoint.from(point2).diff(startPoint);
			
			boolean startOnRay = signum(rayVector.dot(startDiff)) >= 0;
			boolean endOnRay = signum(rayVector.dot(endDiff)) >= 0;

			if (!(startOnRay || endOnRay)) {
				return null;
			}
			
			if (!startOnRay) {
				return new Intersection(0, startPoint, point2);
			}
			
			if (!endOnRay) {
				return new Intersection(0, startPoint, point1);
			}
			
			if (startDiff.norm() < endDiff.norm()) {
				return new Intersection(startDiff.norm(), point1, point2);
			}
			
			return new Intersection(endDiff.norm(), point2, point1);
		}
		
		VectorPoint x = rayVector;
		VectorPoint y_z = VectorPoint.from(point2).diff(point1);
		VectorPoint x_z = VectorPoint.from(point2).diff(startPoint);
		
		double inverse_0_0 = y_z.getY();
		double inverse_0_1 = - y_z.getX();
		
		double normalization = 1 / (x.getX() * y_z.getY() - y_z.getX() * x.getY());
		double distance = normalization * (inverse_0_0 * x_z.getX() + inverse_0_1 * x_z.getY());
		
		return distance < 0 ? null : new Intersection(distance, startPlane, endPlane);
	}
	
	class Intersection {
		private final double distance;
		private final PointDouble intersectionStart;
		private final PointDouble intersectionEnd;
		private final IntersectionType intersectionType;
		private final boolean startOnRay;
		private final boolean endOnRay;
		private final List<HalfPlane> halfPlanes;
		
		Intersection(double distance,
				PointDouble intersectionStart,
				PointDouble intersectionEnd) {
			this.distance = distance;
			this.intersectionStart = intersectionStart;
			this.intersectionEnd = intersectionEnd;
			this.intersectionType = intersectionStart.equals(intersectionEnd) ?
					IntersectionType.POINT : IntersectionType.LINE_SEGMENT;
			this.startOnRay = true;
			this.endOnRay = true;
			this.halfPlanes = ImmutableList.of(HalfPlane.ON_RAY);
		}
		
		Intersection(double distance, HalfPlane startPlane, HalfPlane endPlane) {
			this.distance = distance;
			this.intersectionStart = startPoint.add(rayVector.multiply(distance));
			this.intersectionEnd = this.intersectionStart;
			this.intersectionType = IntersectionType.POINT;
			this.startOnRay = startPlane == HalfPlane.ON_RAY;
			this.endOnRay = endPlane == HalfPlane.ON_RAY;
			this.halfPlanes = ImmutableList.of(startPlane, endPlane);
		}
		
		double distance() {
			return distance;
		}
		
		VectorPoint startPoint() {
			return startPoint;
		}
		
		IntersectionType getType() {
			return intersectionType;
		}
		
		PointDouble getIntersectionStart() {
			return intersectionStart;
		}
		
		PointDouble getIntersectionEnd() {
			return intersectionEnd;
		}
		
		boolean startOnRay() {
			return startOnRay;
		}
		
		boolean endOnRay() {
			return endOnRay;
		}
		
		List<HalfPlane> getHalfPlanes() {
			return halfPlanes;
		}
		
		Intersection merge(Intersection other) {
			checkArgument(other.intersectionType != Ray.IntersectionType.LINE_SEGMENT &&
					(startOnRay && other.endOnRay) || (endOnRay && other.startOnRay),
					"Intersection must be a corner point");
			
			if (startOnRay) {
				return new Intersection(distance, halfPlanes.get(1), other.halfPlanes.get(0));
			}
			
			return new Intersection(distance, halfPlanes.get(0), other.halfPlanes.get(1));
		}
	}

	PointDouble getStartPoint() {
		return startPoint;
	}
	
	VectorPoint rayVector() {
		return rayVector;
	}
}