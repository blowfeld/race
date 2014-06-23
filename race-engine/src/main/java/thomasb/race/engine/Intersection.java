package thomasb.race.engine;

import thomasb.race.engine.Ray.HalfPlane;

final class Intersection {
	private final double a;
	private final VectorPoint startPoint;
	private final VectorPoint rayVector;

	Intersection(double a, VectorPoint startPoint, VectorPoint rayVector) {
		this.a = a;
		this.startPoint = startPoint;
		this.rayVector = rayVector;
	}
	
	Intersection(VectorPoint startPoint, VectorPoint intersectionPoint) {
		this.a = intersectionPoint.diff(startPoint).norm();
		this.startPoint = startPoint;
		this.rayVector = rayVector;
	}
	
	double distance() {
		return a;
	}
	
	VectorPoint intersectionPoint() {
		return startPoint.add(rayVector.multiply(a));
	}
}