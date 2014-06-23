package thomasb.race.engine;

public class RaceMatchers {
	
	static PointMatcher isCloseTo(PointDouble expected, double precision) {
		return new PointMatcher(expected, precision);
	}
	
	static PathSegmentMatcher isCloseTo(PathSegment expected, double precision) {
		return new PathSegmentMatcher(expected, precision);
	}
	
	static RacePathMatcher isCloseTo(RacePath expected, double precision) {
		return new RacePathMatcher(expected, precision);
	}
	
}
