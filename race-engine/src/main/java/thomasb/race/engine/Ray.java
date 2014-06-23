package thomasb.race.engine;


class Ray {
	enum HalfPlane { LEFT, RIGHT, ON_LINE }
	
	private final VectorPoint startPoint;
	private final VectorPoint rayVector;

	Ray(PointDouble startPoint, int direction) {
		this.startPoint = VectorPoint.from(startPoint);
		this.rayVector = VectorPoint.fromDirection(direction);
	}
	
	HalfPlane detectHalfPlane(PointDouble point) {
		VectorPoint diff = VectorPoint.from(point).diff(startPoint);
		
		double signedArea = rayVector.signedArea(diff);
		
		if (signedArea == 0) {
			return HalfPlane.ON_LINE;
		}
		
		return signedArea > 0 ? HalfPlane.LEFT : HalfPlane.RIGHT;
	}

	public boolean doesIntersect(PointDouble point1, PointDouble point2) {
		return detectHalfPlane(point1) == detectHalfPlane(point2);
	}
	/**
	 * a * x + b * y_z + z_x = 0 => (x.T, y.T) dot (a,b).T = x_z
	 * 
	 * (a,b).T = (x.T, y.T).inv dot x_z
	 * 
	 * @param point1
	 * @param point2
	 * @return
	 */
	public Ray.Intersection getIntersection(PointDouble point1, PointDouble point2) {
		VectorPoint x = rayVector;
		VectorPoint y_z = VectorPoint.from(point2).diff(point1);
		VectorPoint x_z = VectorPoint.from(point2).diff(startPoint);
		
		double inverseNormal = 1 / (x.getX() * y_z.getY() - y_z.getX() * x.getY());
		double inverse_0_0 = y_z.getY();
		double inverse_0_1 = - y_z.getX();
//			double inverse_1_0 = - x.getY();
//			double inverse_1_1 = x.getX();
//			
		double a = inverseNormal * (inverse_0_0 * x_z.getX() + inverse_0_1 * x_z.getY());
//			double b = inverseNormal * (inverse_1_0 * x_z.getX() + inverse_1_1 * x_z.getY());
		
		return new Intersection(a, startPoint, rayVector);
	}
	
	static class Intersection {
		private final double a;
		private final VectorPoint startPoint;
		private final VectorPoint rayVector;

		public Intersection(double a, VectorPoint startPoint, VectorPoint rayVector) {
			this.a = a;
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
}