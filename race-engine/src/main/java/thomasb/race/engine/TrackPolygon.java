package thomasb.race.engine;

import static java.lang.Math.signum;
import static java.util.Collections.sort;
import static thomasb.race.engine.Ray.IntersectionType.LINE_SEGMENT;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import thomasb.race.engine.Ray.HalfPlane;
import thomasb.race.engine.Ray.Intersection;
import thomasb.race.engine.Ray.IntersectionType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public final class TrackPolygon implements TrackSection {
	private static final Comparator<Intersection> DISTANCE_COMPARATOR = new Comparator<Intersection>() {
		@Override
		public int compare(Intersection o1, Intersection o2) {
			return (int) signum(o1.distance() - o2.distance());
		}
	};
	
	private final List<? extends PointDouble> corners;
	private final SectionType type;

	public TrackPolygon(List<? extends PointDouble> points, SectionType type) {
		this.corners = points;
		this.type = type;
	}
	
	static TrackPolygon fromTrackSection(TrackSection section) {
		if (section instanceof TrackPolygon) {
			return (TrackPolygon) section;
		}
		
		return new TrackPolygon(section.getCorners(), section.getType());
	}
	
	List<Intersection> intersectionPoints(Ray ray) {
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
			} else if (intersection != null &&
					intersection.getIntersectionStart().equals(ray.getStartPoint()) &&
					!ray.getStartPoint().equals(corner)) {
				intersectionPoints.add(intersection);
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
		
		sort(intersectionPoints, DISTANCE_COMPARATOR);
		
		return ImmutableList.copyOf(intersectionPoints);
	}
	
	boolean containsStartPoint(Ray ray) {
		return containsStartPoint(ray, intersectionPoints(ray));
	}
	
	boolean containsStartPoint(Ray ray, List<Intersection> intersectionPoints) {
		if (!intersectionPoints.isEmpty() && intersectionPoints.get(0).getType() == IntersectionType.LINE_SEGMENT) {
			return true;
		}
		
		int boundaryCrossings = 0;
		
		HalfPlane previousHalfPlane = null;
		for (Intersection intersection : intersectionPoints) {
			List<HalfPlane> halfPlanes = intersection.getHalfPlanes();
			if (!halfPlanes.contains(HalfPlane.ON_RAY) || (previousHalfPlane != null && !halfPlanes.contains(previousHalfPlane))) {
				boundaryCrossings += 1;
			}
			
			if (previousHalfPlane == null && halfPlanes.contains(HalfPlane.ON_RAY)) {
				previousHalfPlane = halfPlanes.get(0) == HalfPlane.ON_RAY ?
						halfPlanes.get(1) : halfPlanes.get(0);
			} else {
				previousHalfPlane = null;
			}
		}
		
		if (intersectionPoints.size() > 1 && intersectionPoints.get(0).distance() == 0.0) {
			return boundaryCrossings % 2 == 0;
		}
		
		return boundaryCrossings % 2 == 1;
	}
	
	@Override
	public SectionType getType() {
		return type;
	}
	
	@Override
	public List<? extends PointDouble> getCorners() {
		return corners;
	}
}
