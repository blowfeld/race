package thomasb.race.engine;

import static com.google.common.collect.Collections2.filter;

import java.util.List;

import thomasb.race.engine.Ray.Intersection;
import thomasb.race.engine.Ray.IntersectionType;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
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
		PointDouble previous = Iterables.getLast(corners);
		
		Builder<Intersection> intersectionPoints = ImmutableList.builder();
		for (PointDouble corner : corners) {
			Intersection intersection = ray.getIntersection(previous, corner);
			if (intersection != null) {
				intersectionPoints.add(intersection);
			}
			
			previous = corner;
		}
		
		return intersectionPoints.build();
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
