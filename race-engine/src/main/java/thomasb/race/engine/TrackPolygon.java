package thomasb.race.engine;

import java.util.Iterator;
import java.util.List;

import thomasb.race.engine.Ray.HalfPlane;
import thomasb.race.engine.Ray.Intersection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

final class TrackPolygon {
	private final List<PointDouble> corners;
	private final TrackType type;

	public TrackPolygon(List<PointDouble> points, TrackType type) {
		this.corners = points;
		this.type = type;
	}

	public List<Intersection> intersectionPoints(Ray ray) {
		PointDouble previous = Iterables.getLast(corners);
		
		HalfPlane previousPlane = HalfPlane.ON_RAY;
		Iterator<PointDouble> reverseItr = Lists.reverse(corners).iterator();
		while (reverseItr.hasNext() && previousPlane == HalfPlane.ON_RAY) {
			previousPlane = ray.detectHalfPlane(reverseItr.next());
		}
		
		Builder<Intersection> intersectionPoints = ImmutableList.builder();
		PointDouble onRayStack = null;
		for (PointDouble point : corners) {
			HalfPlane halfPlane = ray.detectHalfPlane(point);
			if (halfPlane == HalfPlane.ON_RAY) {
				onRayStack = point;
			} else if (halfPlane != previousPlane) {
				if (onRayStack != null) {
					Intersection pointOnRay = ray.pointOnRay(onRayStack);
					if (pointOnRay.distance() > 0) {
						intersectionPoints.add(pointOnRay);
					}
					onRayStack = null;
				}
				Intersection intersection = ray.getIntersection(previous, point);
				if (intersection.distance() > 0) {
					intersectionPoints.add(intersection);
				}
				
				previousPlane = halfPlane;
			} else {
				onRayStack = null;
				previousPlane = halfPlane;
			}
			
			previous = point;
		}
		
		return intersectionPoints.build();
	}

	public TrackType getType() {
		return type;
	}
}
