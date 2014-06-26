package thomasb.race.engine;

import static thomasb.race.engine.Ray.IntersectionType.LINE_SEGMENT;

import java.util.ArrayList;
import java.util.List;

import thomasb.race.engine.Ray.HalfPlane;
import thomasb.race.engine.Ray.Intersection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

final class TrackPolygon {
	private final List<PointDouble> corners;
	private final TrackType type;

	public TrackPolygon(List<PointDouble> points, TrackType type) {
		this.corners = points;
		this.type = type;
	}
	
	public List<Intersection> intersectionPoints(Ray ray) {
		List<Intersection> intersectionPoints = new ArrayList<>();
		PointDouble previousCorner = Iterables.getLast(corners);
		for (PointDouble corner : corners) {
			Intersection intersection = ray.getIntersection(previousCorner, corner);
			if (intersection != null && intersection.getType() != LINE_SEGMENT) {
				if (!intersectionPoints.isEmpty() && (intersection.startOnRay() || intersection.endOnRay())) {
					int lastIndex = intersectionPoints.size() - 1;
					Intersection lastIntersectionPoint = intersectionPoints.get(lastIndex);
					if (lastIntersectionPoint.distance() == intersection.distance() &&
							!lastIntersectionPoint.getHalfPlanes().containsAll(intersection.getHalfPlanes())) {
						Intersection merged = lastIntersectionPoint.merge(intersection);
						intersectionPoints.set(lastIndex, merged);
					} else {
						intersectionPoints.add(intersection);
					}
				} else {
					intersectionPoints.add(intersection);
				}
			}
			
			previousCorner = corner;
		}
		
		if (!intersectionPoints.isEmpty()) {
			int lastIndex = intersectionPoints.size() - 1;
			Intersection lastIntersectionPoint = intersectionPoints.get(lastIndex);
			Intersection intersection = intersectionPoints.get(0);
			if (lastIntersectionPoint.distance() == intersection.distance() &&
					!lastIntersectionPoint.getHalfPlanes().containsAll(intersection.getHalfPlanes())) {
				Intersection merged = lastIntersectionPoint.merge(intersection);
				intersectionPoints.set(0, merged);
				intersectionPoints.remove(lastIndex);
			}
		}
			
		return ImmutableList.copyOf(intersectionPoints);
	}
	
	public boolean containsStartPoint(Ray ray) {
		return containsStartPoint(ray, intersectionPoints(ray));
	}
	
	public boolean containsStartPoint(Ray ray, List<Intersection> intersectionPoints) {
		int boundaryCrossings = 0;
		
		HalfPlane previousHalfPlane = null;
		for (Intersection intersection : intersectionPoints) {
			List<HalfPlane> halfPlanes = intersection.getHalfPlanes();
			if (!halfPlanes.contains(HalfPlane.ON_RAY) || (previousHalfPlane != null && !halfPlanes.contains(previousHalfPlane))) {
				boundaryCrossings += intersection.distance() > 0.0 ? 1 : 0;
			}
			
			if (previousHalfPlane == null && halfPlanes.contains(HalfPlane.ON_RAY)) {
				previousHalfPlane = halfPlanes.get(0) == HalfPlane.ON_RAY ?
						halfPlanes.get(1) : halfPlanes.get(0);
			} else {
				previousHalfPlane = null;
			}
		}
		
		return boundaryCrossings % 2 == 1;
	}
	
	public TrackType getType() {
		return type;
	}
	
	public List<PointDouble> getCorners() {
		return corners;
	}
}
