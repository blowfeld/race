package thomasb.race.engine;

public class RaceMatchers {
	
	public static PointMatcher isCloseTo(PointDouble expected, double precision) {
		return new PointMatcher(expected, precision);
	}
	
	public static PathSegmentMatcher isCloseTo(PathSegment expected, double precision) {
		return new PathSegmentMatcher(expected, precision);
	}
	
	public static TrackSegmentMatcher isCloseTo(TrackSegment expected, double precision) {
		return new TrackSegmentMatcher(expected, precision);
	}
	
	public static RacePathMatcher isCloseTo(RacePath expected, double precision) {
		return new RacePathMatcher(expected, precision);
	}
	
}
