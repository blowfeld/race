package thomasb.race.engine;

import static com.google.common.collect.Collections2.filter;
import static thomasb.race.engine.Ray.IntersectionType.LINE_SEGMENT;
import static thomasb.race.engine.Ray.IntersectionType.POINT;

import java.util.ArrayList;
import java.util.List;

import thomasb.race.engine.Ray.HalfPlane;
import thomasb.race.engine.Ray.Intersection;
import thomasb.race.engine.Ray.IntersectionType;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

final class TrackPolygon {
	private static final Predicate<Intersection> POINT_FILTER = new Predicate<Intersection>() {
		@Override
		public boolean apply(Intersection input) {
			return input.getType() == IntersectionType.POINT;
		}
	};
	
	private final List<PointDouble> corners;
	private final TrackType type;

	public TrackPolygon(List<PointDouble> points, TrackType type) {
		this.corners = points;
		this.type = type;
	}
	
	public List<Intersection> intersectionPoints(Ray ray) {
		List<Intersection> intersectionPoints = new ArrayList<>();
		PointDouble previousCorner = Iterables.getLast(corners);
		Intersection previousIntersection = ray.getIntersection(previousCorner, corners.get(0));
		IntersectionType previousIntersectionType = previousIntersection == null ?
				null : previousIntersection.getType();
		HalfPlane previousHalfPlane = ray.detectHalfPlane(corners.get(0)) == HalfPlane.ON_RAY && previousIntersectionType != IntersectionType.LINE_SEGMENT ?
				ray.detectHalfPlane(previousCorner) : null;
		
		for (PointDouble corner : corners) {
			Intersection intersection = ray.getIntersection(previousCorner, corner);
			if (intersection != null) {
				if (intersection.getType() == LINE_SEGMENT) {
					// always add line segments, as the polygon boundary may be included or not
					intersectionPoints.add(intersection);
					previousHalfPlane = null;
				} else if (previousIntersectionType == LINE_SEGMENT) {
					// since this is not a line segment, the intersection point is the end point
					// of the previous line segment
				} else if (intersection.endOnRay()) {
					// we will add this intersection point in the next step if necessary,
					// either with a line segment, or if we change half planes
				} else if (previousHalfPlane != ray.detectHalfPlane(corner)) {
					// we cross the polygon boundary in a corner point
					intersectionPoints.add(intersection);
				} else {
					// we touch the polygon in a corner point, but do not cross the boundary.
					// Corner points are never included in the polygon
				}
				
				// Remember the half plane if the end touches the ray
				if (intersection.endOnRay() && intersection.getType() == POINT) {
					previousHalfPlane = ray.detectHalfPlane(previousCorner);
				} else {
					previousHalfPlane = null;
				}
				
				previousIntersectionType = intersection.getType();
			}
			
			previousCorner = corner;
		}
		
		return ImmutableList.copyOf(intersectionPoints);
	}
	
	public boolean containsStartPoint(Ray ray) {
		return containsStartPoint(ray, intersectionPoints(ray));
	}
	
	public boolean containsStartPoint(Ray ray, List<Intersection> intersectionPoints) {
		return filter(intersectionPoints, POINT_FILTER).size() % 2 == 1;
	}
	
	public TrackType getType() {
		return type;
	}
}
