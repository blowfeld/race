package thomasb.race.engine;

import static com.google.common.collect.Collections2.filter;
import static java.lang.Math.signum;
import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import thomasb.race.engine.Ray.HalfPlane;
import thomasb.race.engine.Ray.Intersection;
import thomasb.race.engine.Ray.IntersectionType;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PolygonRaceTrack implements RaceTrack {
	private static final Comparator<BoundaryPoint> DISTANCE_COMPARATOR = new Comparator<BoundaryPoint>() {
		@Override
		public int compare(BoundaryPoint o1, BoundaryPoint o2) {
			return (int) signum(o1.distance() - o2.distance());
		}
	};
	
	private static final Predicate<TrackSegment> NON_ZERO_LENGTH_FILTER = new Predicate<TrackSegment>() {
		@Override
		public boolean apply(TrackSegment input) {
			return !input.getStart().equals(input.getEnd());
		}
	};

	private static final List<HalfPlane> HALF_PLANES_OF_FINISH = ImmutableList.of(HalfPlane.LEFT, HalfPlane.RIGHT);
	
	private final List<TrackPolygon> trackSections;
	
	private final PointDouble finish1;
	private final PointDouble finish2;

	private final Iterable<PointDouble> gridPoints;
	
	public PolygonRaceTrack(List<TrackPolygon> sections,
			PointDouble finish1, PointDouble finish2,
			Iterable<PointDouble> gridPoints) {
		this.finish1 = finish1;
		this.finish2 = finish2;
		this.gridPoints = gridPoints;
		
		checkSectionsDontIntersect(sections);
		Builder<TrackPolygon> unique = ImmutableList.builder();
		TrackPolygon previousTrackType = sections.get(0);
		for (int i = 0; i < sections.size(); i++) {
			if (!sections.get(i).getType().equals(previousTrackType)) {
				unique.add(sections.get(i));
			}
		}
		
		this.trackSections = unique.build();
	}
	
	private void checkSectionsDontIntersect(List<TrackPolygon> sections) {
		// TODO check sections contain each other
	}

	@Override
	public List<TrackSegment> segmentsFor(PointDouble startPoint, int direction) {
		Ray ray = new Ray(startPoint, direction);
		
		Intersection finishIntersection = ray.getIntersection(finish1, finish2);
		double finishDistance = -1.0;
		if (finishIntersection != null) {
			finishDistance = finishIntersection.distance();
		}
		
		// Cross finish line in specified direction
		int crossedFinished = 0;
		if (finishIntersection != null) {
			if (!finishIntersection.getHalfPlanes().equals(HALF_PLANES_OF_FINISH) &&
				!finishIntersection.getHalfPlanes().contains(HalfPlane.ON_RAY)) {
				crossedFinished = -1;
			} else {
				crossedFinished = 1;
			}
		}
		
		
		int startSection = 0;
		List<BoundaryPoint> allIntersections = new ArrayList<>();
		for (int i = 0; i < trackSections.size(); i++) {
			TrackPolygon section = trackSections.get(i);
			List<Intersection> intersectionPoints = section.intersectionPoints(ray);
			if (!section.containsStartPoint(ray, intersectionPoints)) {
				startSection += 1;
			};
			
			for (Intersection intersection : intersectionPoints) {
				allIntersections.add(new BoundaryPoint(intersection, i));
			}
		}
		
		sort(allIntersections, DISTANCE_COMPARATOR);
		
		int currentSection = startSection;
		
		PointDouble segmentStart = startPoint;
		double startDistance = 0.0;
		List<TrackSegment> segments = new ArrayList<>();
		if (!allIntersections.isEmpty() && allIntersections.get(0).intersection.getType() == IntersectionType.LINE_SEGMENT) {
			allIntersections = allIntersections.subList(1, allIntersections.size());
		}
		for (BoundaryPoint intersection : allIntersections) {
			PointDouble segmentEnd = intersection.getPoint();
			
			int maxSpeed = determineMaxSpeed(intersection, currentSection == -1 ? startSection : currentSection);
			int finish = startDistance <= finishDistance  && finishDistance <= intersection.distance() ?
					crossedFinished : 0;
			
			RaceTrackSegment trackSegment;
			if (finish > 0) {
				segments.add(new RaceTrackSegment(segmentStart, finishIntersection.getIntersectionStart(), maxSpeed, finish));
				segments.add(new RaceTrackSegment(finishIntersection.getIntersectionStart(), segmentEnd, maxSpeed, 0));
				
			} else {
				trackSegment = new RaceTrackSegment(segmentStart, segmentEnd, maxSpeed, finish);
				segments.add(trackSegment);
			}
			
			if (intersection.type == currentSection) {
				currentSection += 1;
			} else {
				currentSection -= 1;
			}
			
			if (maxSpeed == 0) {
				break;
			}
			
			segmentStart = segmentEnd;
			startDistance = intersection.distance();
		}
		
		return ImmutableList.copyOf(filter(segments, NON_ZERO_LENGTH_FILTER));
	}
	
	private int determineMaxSpeed(BoundaryPoint intersection, int currentSection) {
		return trackSections.get(currentSection).getType().getMaxSpeed();
	}
	
	@Override
	public Iterable<PointDouble> getStartGrid() {
		return gridPoints;
	}
	
	@Override
	public List<PointDouble> getContour() {
		return trackSections.get(0).getCorners();
	}
	
	private static class BoundaryPoint {
		private final Intersection intersection;
		private final int type;

		BoundaryPoint(Intersection intersection, int type) {
			this.intersection = intersection;
			this.type = type;
		}
		
		double distance() {
			return intersection.distance();
		}
		
		PointDouble getPoint() {
			return intersection.getIntersectionStart();
		}
	}
}
